package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.util.LevenshteinDistance;
import br.usp.sdext.util.ParseException;

public class ElectionParser extends AbstractParser {

	private LocationParser locationParser;
	
	private HashMap<Model, Model> electionsMap = new HashMap<>();

	private ArrayList<Model> logs = new ArrayList<>();

	public ElectionParser(LocationParser locationParser) {

		this.locationParser = locationParser;
	}

	@Override
	protected void loadFile(File file) throws Exception {

		if (file.getName().matches("([^\\s]+(\\.(?i)txt))")) {

			parseFile(file);
		}
	}

	private void parseFile(File file) throws Exception {

		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));

		String line = null;

		while ((line = in.readLine()) != null) {

			try {

				// Break line where finds ";"
				String pieces[] = line.split("\";\"");

				// remove double quotes
				for (int i = 0; i < pieces.length; i++) {
					pieces[i] = pieces[i].replace("\"", "");
				}

				parseElection(pieces);

			} catch (ParseException e) {

				String exceptionClass = null;
				String exceptionMethod = null;

				for (StackTraceElement element : e.getStackTrace()) {

					if (element.getClassName().contains("br.usp.sdext.parsers")) {

						exceptionClass = element.getClassName();
						exceptionMethod = element.getMethodName();
						break;
					}
				}

				Log log = new Log(line,"CAUSED BY: " + exceptionMethod 
						+ " IN CLASS: " + exceptionClass, e.getMessage(), e.getDetail());
				logs.add(log);
			}
		}

		if (in != null) {
			in.close();
		}
	}

	private void parseElection(String[] pieces) throws Exception {

		// Parse year.
		Integer year = parseInt(pieces[2]);

		// Parse description.
		String description = pieces[3];	

		// Parse and fetch state.
		State state = new State();
		state.setAcronym(pieces[4]);

		State mappedState = (State) locationParser.getStatesMap().get(state);

		if (mappedState == null) {

			throw new ParseException("State not found in map" , state.getAcronym());
		}

		Town mappedTown;

		// If it is not a major election then parse town.
		if ((year - Election.FIRST_MAIN_ELECTION) % 4 != 0) {

			Integer tseCode = parseInt(pieces[5]);

			if (tseCode == null) {

				throw new ParseException("Invalid TSE code", pieces[5]);
			}

			Town town = new Town();
			
			town.setNamex(parseTownNamex(pieces[6]));
			town.setState(mappedState);
			
			// Try to find town by name and state.
			if ((mappedTown = (Town) locationParser.getTownsMap().get(town)) == null) {

				town.setTseCode(tseCode);
				
				// Try to find town by TSE code.
				if ((mappedTown = (Town) locationParser.getTownsMapByTSE().get(town)) == null) {

					// Check if town is not lost.
					if (!locationParser.getLostTownsMap().containsKey(town)) {

						// Try to find similar.
						if ((mappedTown = findMisspelledTown(town, 1)) == null) {

							Town test = new Town();
							test.setNamex(town.getNamex().replaceAll("DO OESTE", "D'OESTE"));
							test.setState(town.getState());
							mappedTown = (Town) locationParser.getTownsMap().get(test);
						}
						
						// Try individually guesses..
						mappedTown = guessTownNames(town);

						// If all failed then town is lost.
						if (mappedTown == null) {
							
//							mappedTown = new Town();
//							mappedTown.setNamex(town.getNamex());
//							mappedTown.setState(town.getState());
//							
//							mappedTown = (Town) Model.fetch(mappedTown, locationParser.getTownsMap());
							
							locationParser.getLostTownsMap().put(town, town);
							logs.add(new Log("Town not found", town.getNamex() + ", " + town.getState().getAcronym()));
							
							return;
						}

					}
					else {

//						mappedTown = (Town) locationParser.getLostTownsMap().get(town);
						return;
					}
				}
			} 

			// Check if TSE code has not already been set. 
			if (mappedTown.getTseCode() == null) {

				// Set TSE code.
				mappedTown.setTseCode(tseCode);
				
				// Add town to TSE code map.
				locationParser.getTownsMapByTSE().put(tseCode, mappedTown);

			} else {

				// Check for data inconsistency.
				if (!mappedTown.getTseCode().equals(tseCode)) {

					throw new ParseException("Same city different TSE ids");
				}
			}
		} 
		// A major election, town does not matter.
		else {
			
			mappedTown = null;
		}

		// Parse post.
		Integer postCode = parseInt(pieces[7]);
		String post = pieces[8];
		
		// Parse number of jobs.

		Integer noJobs = parseInt(pieces[9]);
		
		Election election = new Election(year, description, mappedState, mappedTown, postCode, post, noJobs);
		
		Model.fetch(election, electionsMap);
	}

	private Town guessTownNames(Town town) {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer parseInt(String str) {

		if (str.replace(" ", "").equals("")) return null;

		return Integer.parseInt(str);
	}

	private String parseTownNamex(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);

		str = str.replaceAll("[^\\p{ASCII}]", "");

		str = str.replaceAll("[\\s\\-()]", " ");

		return str.toUpperCase().trim();
	}

	private Town findMisspelledTown(Town missTown, int threshold) {

		Iterator<Entry<Model, Model>> i = locationParser.getTownsMap().entrySet().iterator();

		while (i.hasNext()) {

			Town town = (Town) ((Entry<Model, Model>) i.next()).getValue();

			if (missTown.getState().equals(town.getState())) {

				int distance = LevenshteinDistance.computeLevenshteinDistance(missTown.getNamex(), town.getNamex());

				if (distance <= threshold) {

					return town;
				}
			}
		}
		return null;
	}

	public void save() {

		Model.bulkSave(logs);
		
		System.out.println("Saving elections...");
		Model.bulkSave(electionsMap.values());
		
		System.out.println("Done!");
	}

	public static void main(String[] args) throws Exception {

		LocationParser locationParser = new LocationParser();

		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/reg.csv", "region");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/uf.csv", "state");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/meso.csv", "meso");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/micro.csv", "micro");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/mun.csv", "town");

		ElectionParser electionParser = new ElectionParser(locationParser);
		
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2012");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2010");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2008");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2006");

		locationParser.save();
		electionParser.save();
	}
}













