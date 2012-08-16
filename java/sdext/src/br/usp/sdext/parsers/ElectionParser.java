package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.util.ParseException;

public class ElectionParser extends AbstractParser {

	private LocationParser locationParser;
	
	private HashMap<Model, Model> electionsMap = new HashMap<>();

	private ArrayList<Model> logs = new ArrayList<>();

	public ElectionParser(LocationParser locationParser) {

		this.locationParser = locationParser;
	}

	public HashMap<Model, Model> getElectionsMap() {return electionsMap;}
	




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
		State state = new State(pieces[4]);

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

			Town town = new Town(locationParser.parseTownNamex(pieces[6]), mappedState);
			
			// Try to find town by name and state.
			if ((mappedTown = (Town) locationParser.getTownsMap().get(town)) == null) {

				town.setTseCode(tseCode);
				
				// Try to find town by TSE code.
				if ((mappedTown = (Town) locationParser.getTownsMapByTSE().get(town.getTseCode())) == null) {

					// Check if town is not lost.
					if (!locationParser.getLostTownsMap().containsKey(town)) {

						// Try to disambiguate.
						mappedTown = locationParser.disambiguateTown(town);
						
						// If all failed then town is lost.
						if (mappedTown == null) {
							
							mappedTown = new Town();
							mappedTown.setNamex(town.getNamex());
							mappedTown.setState(town.getState());
							
							mappedTown = (Town) Model.fetch(mappedTown, locationParser.getTownsMap());
							
							locationParser.getLostTownsMap().put(town, town);
							
							logs.add(new Log("Town not found", town.getNamex() + ", " + town.getState().getAcronym()
							+ ", " + town.getTseCode()));
							
						}
					}
					else {

						mappedTown = (Town) locationParser.getLostTownsMap().get(town);
					}
				}
			} 

			// Check if TSE code has not already been set. 
			if (mappedTown.getTseCode() == null) {

				// Set TSE code.
				mappedTown.setTseCode(tseCode);
				
			} else {

				// Check for data inconsistency.
				if (!mappedTown.getTseCode().equals(tseCode)) {

					logs.add(new Log("Same city different TSE ids", mappedTown.getNamex() 
							+ ", " + mappedTown.getTseCode() + ", " + tseCode));
				}
			}
			
			// Add town to TSE code map.
			locationParser.getTownsMapByTSE().put(mappedTown.getTseCode(), mappedTown);
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

	private Integer parseInt(String str) {

		if (str.replace(" ", "").equals("")) return null;

		return Integer.parseInt(str);
	}

	public void save() {

		Model.bulkSave(logs);
		
		System.out.println("Saving " + electionsMap.size() +" elections...");
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













