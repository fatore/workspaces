package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Town;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.Party;
import br.usp.sdext.models.State;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Citizenship;
import br.usp.sdext.models.candidate.Sex;
import br.usp.sdext.models.candidate.status.Job;
import br.usp.sdext.models.candidate.status.MaritalStatus;
import br.usp.sdext.models.candidate.status.Schooling;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.util.Misc;

public class CandidaturesParser extends AbstractParser { 

	private int year;
	private Long numCandidates = 0L;
	private int incompleteCandidates = 0;

	private HashMap<Model, Model> statesMap;
	private HashMap<Model, Model> townsMap;

	private HashMap<Model, Model> candidatesMap = new HashMap<>();
	private HashMap<Model, Model> sexMap = new HashMap<>();
	private HashMap<Model, Model> ctzsMap = new HashMap<>();

	private ArrayList<Model> duppersList = new ArrayList<>();

	private Set<Model> statusSet = new HashSet<>();
	private HashMap<Model, Model> jobsMap = new HashMap<>();
	private HashMap<Model, Model> mStatusMap = new HashMap<>();
	private HashMap<Model, Model> schMap = new HashMap<>();

	private HashMap<Model, Model> electionsMap = new HashMap<>();
	private HashMap<Model, Model> partiesMap = new HashMap<>();
	private HashMap<Model, Model> coalitionsMap = new HashMap<>();

	private Set<Model> candidaturesSet = new HashSet<>();

	public CandidaturesParser() {

		statesMap = State.init();
		townsMap = new HashMap<>();
	}

	protected void loadFile(File file) throws Exception {

		year = Integer.parseInt(file.getParentFile().getName());

		if (file.getName().matches("([^\\s]+(\\.(?i)txt))")) {

			System.out.println("Parsing file " + file.getName());
			parseFile(file);
		}
	}

	private void parseFile(File file) throws Exception {

		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));

		String line = null;

		while ((line = in.readLine()) != null) {

			try {
				parseLine(line);
			} catch (Exception e) {
				String exceptionClass = null;
				String exceptionMethod = null;

				for (StackTraceElement element : e.getStackTrace()) {

					if (element.getClassName().contains("br.usp.sdext.models")) {
						exceptionClass = element.getClassName();
						exceptionMethod = element.getMethodName();
						break;
					}
				}
				Log log = new Log(line,"CAUSED BY: " + exceptionMethod 
						+ " IN CLASS: " + exceptionClass, e.getMessage());
				log.save();
				incompleteCandidates++;
			}new HashMap<>();
		}

		if (in != null) {
			in.close();
		}
	}

	private void parseLine(String line) throws Exception {

		// Break line where finds ";"
		String pieces[] = line.split("\";\"");

		// remove double quotes
		for (int i = 0; i < pieces.length; i++) {
			pieces[i] = pieces[i].replace("\"", "");
		}

		Candidate candidate = parseCandidate(pieces);
		
		Election election = parseElection(pieces);
		
		Party party = new Party(
				Misc.parseInt(pieces[16]), // no 
				Misc.parseStr(pieces[17]),  // acronym
				Misc.parseStr(pieces[18])); // name
		
		Coalition coalition = new Coalition(
				Misc.parseLong(pieces[19]), // code
				Misc.parseStr(pieces[20]), // acronym
				Misc.parseStr(pieces[22]), // name
				Misc.parseStr(pieces[21])); // composition
		
		Candidature candidature = new Candidature(
				Misc.parseStr(pieces[13]), // ballot name
				Misc.parseInt(pieces[12]), // ballot no
				Misc.parseLong(pieces[14]), // sit ID
				Misc.parseStr(pieces[15]), // sit
				Misc.parseFloat(pieces[39]), // max exp
				Misc.parseLong(pieces[40]), // result id
				Misc.parseStr(pieces[41])); // result
		
		party = (Party) Model.fetch(party, partiesMap);
		coalition = (Coalition) Model.fetch(coalition, coalitionsMap);

		// Bind objects.
		candidature.setCandidate(candidate);
		candidature.setElection(election);
		candidature.setParty(party);
		candidature.setCoalition(coalition);

		// Add candidature if does not exists already
		if (!candidaturesSet.contains(candidature)) {
			candidaturesSet.add(candidature);
		}
	}

	private Candidate parseCandidate(String[] pieces) throws Exception {

		// Parse data.
		Candidate candidate = new Candidate(
				Misc.parseLong(pieces[26]), // voterID
				Misc.parseStr(pieces[10]), // name
				Misc.parseDate(pieces[25])); // birth date

		// Look for the candidate in map ...
		Candidate mapCandidate = (Candidate) candidatesMap.get(candidate);

		// ... if didn't find anything.
		if (mapCandidate == null) {

			Sex sex =  new Sex(
					Misc.parseLong(pieces[28]), // tseID
					Misc.parseStr(pieces[29])); // label 

			Citizenship ctz = new Citizenship(
					Misc.parseLong(pieces[34]), // tseID
					Misc.parseStr(pieces[35])); // label

			State birthState = new State(Misc.parseStr(pieces[36])); // label

			Town birthTown = new Town(
					Misc.parseLong(pieces[37]), // tseID
					Misc.parseStr(pieces[38]));  // label

			// Fetch data.
			sex = (Sex) Model.fetchAndSave(sex, sexMap);
			ctz = (Citizenship) Model.fetchAndSave(ctz, ctzsMap);
			birthState = (State) Model.fetch(birthState, statesMap);

			birthTown.setState(birthState);
			birthTown = (Town) Model.fetch(birthTown,townsMap);

			// Set data.
			candidate.setSex(sex);
			candidate.setBirthTown(birthTown);
			candidate.setCitizenship(ctz);

			// Set the ID for the new Candidate ...
			candidate.setId(numCandidates++);

			// ... and put it in the map.
			candidatesMap.put(candidate, candidate);

		}
		// ... if found something in the map.
		else {
			// Take a look if the objects are similar ...
			if (mapCandidate.similar(candidate)) {

				candidate = mapCandidate;
			} 
			// Objects aren't similar! 
			// VoterID is the same but not other attributes. !?
			else {

				// Set both candidates as "duppers".
				mapCandidate.setDupper(true);
				candidate.setDupper(true);

				// Set the ID for the new Candidate ...
				candidate.setId(numCandidates++);

				// ... and add the new Candidate to a duppers list
				duppersList.add(candidate);
			}
		}

		// Parse Status.
		Status status = new Status(
				year,
				Misc.parseInt(pieces[27]), // age
				Misc.parseDate(pieces[25]), // birth date
				Misc.parseLong(pieces[11])); // number sequence (TSE_ID)


		Job job = Job.parse(pieces);
		job = (Job) Model.fetch(job, jobsMap);

		MaritalStatus maritalStatus = MaritalStatus.parse(pieces);
		maritalStatus = (MaritalStatus) Model.fetchAndSave(maritalStatus, mStatusMap);

		Schooling schooling = Schooling.parse(pieces);
		schooling = (Schooling) Model.fetchAndSave(schooling, schMap);

		status.setJob(job);
		status.setMaritalStatus(maritalStatus);
		status.setSchooling(schooling);

		// Bind objects.
		status.setCandidate(candidate);

		// Add status if does not exists already
		if (!statusSet.contains(status)) {
			statusSet.add(status);
		} 

		return candidate;
	}

	private Election parseElection(String[] pieces) throws Exception {
		
		Election election = new Election(
				Misc.parseInt(pieces[2]), // year
				Misc.parseInt(pieces[3]), // round 
				Misc.parseLong(pieces[8]), // posID
				Misc.parseStr(pieces[9]), // post
				Misc.parseStr(pieces[4]) // description
		);
		
		State electionState = new State(Misc.parseStr(pieces[5]));
		electionState = (State) State.fetch(electionState, statesMap);
		
		election.setState(electionState);

		if ((election.getYear() - Election.FIRST_MAIN_ELECTION) % 4 != 0) {

			Town electionTown = new Town(null, Misc.parseStr(pieces[7]));
			Long ueId = Misc.parseLong(pieces[6]);
			electionTown.setState(electionState);
			
			electionTown = (Town) Town.fetch(electionTown, townsMap);

			if (electionTown.getUeId() == null) {
				electionTown.setUeId(ueId);
			} else {
				if (!electionTown.getUeId().equals(ueId)) {
					System.err.println("Same city different ueIds.");
				}
			}
			election.setTown(electionTown);
		} 
		else {

			election.setTown(null);
		}
		
		election = (Election) Model.fetch(election, electionsMap);
		
		return election;
	}
	
	protected void save() {

		long start = System.currentTimeMillis();   

		System.out.println("\nTotal objects loaded");
		System.out.println("\tCandidates: " + candidatesMap.size());
		System.out.println("\tDuplicate Candidates: " + duppersList.size());
		System.out.println("\tIncomplete Candidates: " + incompleteCandidates);
		System.out.println("\tElections: " + electionsMap.size());
		System.out.println("\tParties: " + partiesMap.size());
		System.out.println("\tCoalitions: " + coalitionsMap.size());
		System.out.println("\tCandidatures: " + candidaturesSet.size());

		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");

		System.out.println("\tSaving candidates...");
		Model.bulkSave(townsMap.values());
		Model.bulkSave(candidatesMap.values());

		System.out.println("\tSaving duppers...");
		Model.bulkSave(duppersList);

		System.out.println("\tSaving candidates status...");
		Model.bulkSave(jobsMap.values());
		Model.bulkSave(statusSet);

		System.out.println("\tSaving elections...");
		Model.bulkSave(electionsMap.values());

		System.out.println("\tSaving parties...");
		Model.bulkSave(partiesMap.values());

		System.out.println("\tSaving coalitions...");
		Model.bulkSave(coalitionsMap.values());

		System.out.println("\tSaving candidatures...");
		Model.bulkSave(candidaturesSet);

		long elapsedTime = System.currentTimeMillis() - start;

		System.out.printf("Finished saving after %d mins and %d secs\n",
				+  (int) (elapsedTime / 60000),(int) (elapsedTime % 60000) / 1000);
	}
}