package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Candidate;
import br.usp.sdext.models.Candidature;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.Party;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.parsers.AccountabilityBinding;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class CandidatureParser extends AbstractParser {
	
	private LocationParser locationParser;
	private ElectionParser electionParser;
	private CandidateParser candidateParser;
	private PartyParser partyParser;
	private CoalitionParser coalitionParser;
	
	private ArrayList<Model> logs = new ArrayList<>();
	
	private int dups = 0; 
	
	private HashMap<Model, Model> candidatureMap = new HashMap<>();
	private HashMap<AccountabilityBinding, Model> accountabilityBindings = new HashMap<>();
	private HashMap<AccountabilityBinding, Model> estateBindings = new HashMap<>();

	public CandidatureParser(LocationParser locationParser,
			ElectionParser electionParser, CandidateParser candidateParser,
			PartyParser partyParser, CoalitionParser coalitionParser) {
		
		this.locationParser = locationParser;
		this.electionParser = electionParser;
		this.candidateParser = candidateParser;
		this.partyParser = partyParser;
		this.coalitionParser = coalitionParser;
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
		
		Election mappedElection = parseElection(pieces);
		Candidate mappedCandidate = candidateParser.parseCandidate(pieces);
		Party mappedParty = (Party) partyParser.parse(pieces);
		Coalition mappedCoalition = (Coalition) coalitionParser.parse(pieces);
		
		// TSE ID.
		Long tseId = Misc.parseLong(pieces[11]); // id
		if (tseId == null) {throw new Exception("Candidate TSE id is invalid: " + pieces[11]);}
		
		Candidature parsedCandidature = new Candidature(
				Misc.parseStr(pieces[13]), // ballot name
				Misc.parseInt(pieces[12]), // ballot no
				Misc.parseLong(pieces[14]), // sit ID
				Misc.parseStr(pieces[15]), // sit
				Misc.parseFloat(pieces[39]), // max exp
				Misc.parseLong(pieces[40]), // result id
				Misc.parseStr(pieces[41]),
				tseId); // result

		// Bind objects.
		parsedCandidature.setElection(mappedElection);
		parsedCandidature.setCandidate(mappedCandidate);
		parsedCandidature.setParty(mappedParty);
		parsedCandidature.setCoalition(mappedCoalition);
		
		Candidature mappedCandidature = (Candidature) candidatureMap.get(parsedCandidature);

		if (mappedCandidature == null) {
			
			parsedCandidature.setId(new Long(candidatureMap.size()));
			candidatureMap.put(parsedCandidature, parsedCandidature);
			accountabilityBindings.put(new AccountabilityBinding(parsedCandidature), parsedCandidature); 
			
		} else {

			if (round.equals(2)) {
				
				mappedCandidature.setResultID(Misc.parseLong(pieces[40]));
				mappedCandidature.setResult(Misc.parseStr(pieces[41]));
				
			} else {
				
				dups++;
			}
		}
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

		System.out.println("Saving candidatures...");
		Model.bulkSave(logs);
		System.out.println("\tDuplicated candidatures: " + dups);
		System.out.println("\tTotal candidatures: " + candidatureMap.size());
		
		Model.bulkSave(candidatureMap.values());
		
		System.out.println("Done!");
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
		
		CandidateParser candidateParser = new CandidateParser(locationParser);
		
		CoalitionParser coalitionParser = new CoalitionParser();
		
		PartyParser partyParser = new PartyParser();
		
		CandidatureParser candidatureParser = new CandidatureParser(locationParser, electionParser, 
				candidateParser, partyParser, coalitionParser);
		
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2006");

		locationParser.save();
		electionParser.save();
		candidateParser.save();
		coalitionParser.save();
		partyParser.save();
		candidatureParser.save();
	}
}