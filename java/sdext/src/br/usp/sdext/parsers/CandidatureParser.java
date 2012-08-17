package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.util.ParseException;

public class CandidatureParser extends AbstractParser {
	
	private LocationParser locationParser;
	private ElectionParser electionParser;
	
	private ArrayList<Model> logs = new ArrayList<>();
	
	public CandidatureParser(LocationParser locationParser,
			ElectionParser electionParser) {
		
		this.locationParser = locationParser;
		this.electionParser = electionParser;
	}

	protected void loadFile(File file) throws Exception {

		if (file.getName().matches("([^\\s]+(\\.(?i)txt))")) {

			System.out.println("Parsing candidatures from " + file.getName());
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

				parseCandidature(pieces);

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

	private void parseCandidature(String[] pieces) throws Exception {

		Integer round = parseInt(pieces[3]);
		
		if (round.equals(2)) {
			
			System.err.println("Segundo turno!");
		}
		
		Election mappedElection = parseElection(pieces);
	}
	
	private Election parseElection(String[] pieces) throws Exception {
		
		Integer year = parseInt(pieces[2]); // year
		Integer postCode = parseInt(pieces[8]); // posID
		
		// Parse and fetch state.
		State state = new State(pieces[5]);

		State mappedState = (State) locationParser.getStatesMap().get(state);

		if (mappedState == null) {

			throw new ParseException("State not found in map" , state.getAcronym());
		}
		
		Town mappedTown = null;
		
		if ((year - Election.FIRST_MAIN_ELECTION) % 4 != 0) {
			
			Integer tseCode = parseInt(pieces[6]);
			
			if (tseCode == null) {

				throw new ParseException("Invalid TSE code", pieces[6]);
			}
			
			Town town = new Town();
			town.setTseCode(parseInt(pieces[6]));
			
			if ((mappedTown = (Town) locationParser.getTownsMapByTSE().get(tseCode)) == null) {
				
				throw new ParseException("Town TSE code not found", tseCode + "");
			}
		} 
		
		Election election = new Election(year, mappedState, mappedTown, postCode);
		
		Election mappedElection = (Election) electionParser.getElectionsMap().get(election);
		
		if (mappedElection == null) {
			
			// Check if election is a plebiscite.
			if (pieces[4].contains("PLEBISCITO")) {
				
				throw new ParseException("Candidature of a plebiscite", pieces[9]);
			}
			
			// Check if post is for vice or substitute.
			if (pieces[9].contains("VICE") || pieces[9].contains("SUPLENTE")) {
				
				throw new ParseException("Candidature for a non elective post", pieces[9]);
			}
				
			throw new ParseException("Candidature election not found", election.toString());
		}
		
		return mappedElection;
	}
	
	private Integer parseInt(String str) {

		if (str.replace(" ", "").equals("")) return null;

		return Integer.parseInt(str);
	}

	public void save() {

		Model.bulkSave(logs);
	}

	public static void main(String[] args) throws Exception {

		LocationParser locationParser = new LocationParser();

		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/reg.csv", "region");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/uf.csv", "state");
		locationParser.addSpecialValues();
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/meso.csv", "meso");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/micro.csv", "micro");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/mun.csv", "town");

		ElectionParser electionParser = new ElectionParser(locationParser);
		
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2012");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2010");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2008");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2006");
		electionParser.addSpecialValues();
		
		CandidatureParser candidatureParser = new CandidatureParser(locationParser, electionParser);
		
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2006");

//		locationParser.save();
//		electionParser.save();
//		candidatureParser.save();
	}
}