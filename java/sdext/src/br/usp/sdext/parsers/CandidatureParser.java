package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import br.usp.sdext.core.Log;
import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.candidature.Result;
import br.usp.sdext.models.candidature.Situation;
import br.usp.sdext.models.coalition.Coalition;
import br.usp.sdext.models.election.Election;
import br.usp.sdext.models.election.Post;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.models.party.Party;
import br.usp.sdext.parsers.bindings.AccountBinding;
import br.usp.sdext.parsers.bindings.EstateBinding;
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
	
	private HashMap<Model, Model> candidaturesMap = new HashMap<>();
	
	private HashMap<Model, Model> situationsMap = new HashMap<>();
	private HashMap<Model, Model> resultsMap = new HashMap<>();
	
	private HashMap<AccountBinding, Model> accountBindings = new HashMap<>();
	private HashMap<EstateBinding, Model> estateBindings = new HashMap<>();
	
	public HashMap<Model, Model> getCandidaturesMap() {return candidaturesMap;}
	public HashMap<Model, Model> getSituationsMap() {return situationsMap;}
	public HashMap<Model, Model> getResultsMap() {return resultsMap;}
	public HashMap<AccountBinding, Model> getAccountBindings() {return accountBindings;}
	public HashMap<EstateBinding, Model> getEstateBindings() {return estateBindings;}

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

	private Candidature parseCandidature(String[] pieces) throws Exception {

		Long tseId = Misc.parseLong(pieces[11]); 
		
		if(tseId == 60000000002L) {
			
			System.out.println();
		}
		
		if (tseId == null) {
			throw new Exception("Candidate TSE id is invalid: " + pieces[11]);
		}
		
		Long situationID = Misc.parseLong(pieces[14]);
		String situationLabel = Misc.parseStr(pieces[15]);
		
		if (situationID == null) {
			
			return null;
		}
		
		switch (situationID.intValue()) {
		
		case 4: // rejected W/R
		case 5: // canceled
		case 6: // resigns
		case 7: // dead
		case 8: // waiting judge
		case 9: // ineligible
		case 10: // revoked
		case 13: // empty
		case 14: // rejected
		case 17: // judging
		case 18: // revoked W/R
			return null;
			
		case 2: // accepted
		case 16: // accepted W/R
			break;
			
		default:
			System.err.println("Strange candidature situation: " + situationID + ", " + situationLabel);
			throw new ParseException("Strange candidature situation", situationID + ", " + situationLabel);
		}
		
		Situation situation = new Situation(situationID, situationLabel);
		situation = (Situation) Model.persist(situation, situationsMap);
		
		Long resultID = Misc.parseLong(pieces[40]);
		String resultLabel = Misc.parseStr(pieces[41]);
		
		if (resultID == null) {
			
			throw new ParseException("Candidature result is null");
		}
		
		Result result = new Result(resultID, resultLabel);
		result = (Result) Model.persist(result, resultsMap);
		
		Integer round = parseInt(pieces[3]);
		
		Election mappedElection = parseElection(pieces);
		Candidate mappedCandidate = candidateParser.parseCandidate(pieces);
		Party mappedParty = (Party) partyParser.parse(pieces);
		Coalition mappedCoalition = (Coalition) coalitionParser.parse(pieces);
		
		String ballotName = Misc.parseStr(pieces[13]);
		Integer ballotNo = Misc.parseInt(pieces[12]);
		Float maxExpenses = Misc.parseFloat(pieces[39]);
		
		Candidature parsedCandidature = new Candidature(mappedCandidate, mappedElection, mappedParty, 
				mappedCoalition, ballotName, ballotNo, situation, result, maxExpenses, tseId);
		
		// Bind objects.
		parsedCandidature.setElection(mappedElection);
		parsedCandidature.setCandidate(mappedCandidate);
		parsedCandidature.setParty(mappedParty);
		parsedCandidature.setCoalition(mappedCoalition);
		
		Candidature mappedCandidature = (Candidature) candidaturesMap.get(parsedCandidature);

		// Check if candidature already exists
		if (mappedCandidature == null) {
			
			mappedCandidature = (Candidature) Model.persist(parsedCandidature, candidaturesMap);
			
			accountBindings.put(new AccountBinding(mappedCandidature), mappedCandidature); 
			
			estateBindings.put(new EstateBinding(mappedCandidature), mappedCandidature);
			
		} else {
			
			if (round.equals(2)) {
				
				mappedCandidature.setResult(result);
				
			} else {
				
				if (!mappedCandidature.getResult().equals(parsedCandidature.getResult())) {
					
					if (parsedCandidature.getResult().getTseCode() == 1) {
						
						Result elected = (Result) resultsMap.get(new Result(1L));
						mappedCandidature.setResult(elected);
					} 
				}
			}
			dups++;
		}
		
		return mappedCandidature;
	}
	
	private Election parseElection(String[] pieces) throws Exception {
		
		Integer year = parseInt(pieces[2]); // year
		
		Post parsedPost = new Post(Misc.parseStr(pieces[9]));
		
		Post mappedPost = (Post) electionParser.getPostsMap().get(parsedPost);
		
		if (mappedPost == null) {
			
			throw new ParseException("Election post not found", pieces[8]);
		}
		
		// Parse and fetch state.
		State state = new State(pieces[5]);

		State mappedState = (State) locationParser.getStatesMap().get(state);

		if (mappedState == null) {

			throw new ParseException("Election state not found in map" , state.getAcronym());
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
		
		Election election = new Election(year, mappedState, mappedTown, mappedPost);
		
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

	public Long parseLong(String str) {

		if (str.replace(" ", "").equals("")) return null;

		Long number = Long.parseLong(str);

		if (number < 1) {

			return null;
		}

		return number;
	}
	
	public void save() {

		System.out.println("Saving candidatures...");
		Model.bulkSave(logs);
		System.out.println("\tDuplicated candidatures: " + dups);
		System.out.println("\tTotal candidatures: " + candidaturesMap.size());
		
		Model.bulkSave(situationsMap.values());
		Model.bulkSave(resultsMap.values());
		Model.bulkSave(candidaturesMap.values());
		
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
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2010");

		locationParser.save();
		electionParser.save();
		candidateParser.save();
		coalitionParser.save();
		partyParser.save();
		candidatureParser.save();
	}
}