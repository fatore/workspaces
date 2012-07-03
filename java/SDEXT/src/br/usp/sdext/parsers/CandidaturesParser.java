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
import br.usp.sdext.models.Coalition;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.Party;
import br.usp.sdext.models.candidate.BirthState;
import br.usp.sdext.models.candidate.BirthTown;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Citizenship;
import br.usp.sdext.models.candidate.Sex;
import br.usp.sdext.models.candidate.status.Job;
import br.usp.sdext.models.candidate.status.MaritalStatus;
import br.usp.sdext.models.candidate.status.Schooling;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.candidature.Candidature;

public class CandidaturesParser extends AbstractParser { 

	private int year;

	private HashMap<Model, Model> candidatesMap = new HashMap<>();
	private HashMap<Model, Model> sexMap = new HashMap<>();
	private HashMap<Model, Model> ctzsMap = new HashMap<>();
	private HashMap<Model, Model> townsMap = new HashMap<>();
	private HashMap<Model, Model> statesMap = new HashMap<>();
	private Long numCandidates;

	private ArrayList<Model> duppersList = new ArrayList<>();

	private Set<Model> statusSet = new HashSet<>();
	private HashMap<Model, Model> jobsMap = new HashMap<>();
	private HashMap<Model, Model> mStatusMap = new HashMap<>();
	private HashMap<Model, Model> schMap = new HashMap<>();

	private HashMap<Model, Model> electionsMap;
	private HashMap<Model, Model> partiesMap;
	private HashMap<Model, Model> coalitionsMap;

	private Set<Model> candidaturesSet;

	public CandidaturesParser() {

		numCandidates = 0L;
		electionsMap = new HashMap<>();
		partiesMap = new HashMap<>();
		coalitionsMap = new HashMap<>();
		candidaturesSet = new HashSet<>();
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
			}
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
		Election election = (Election) Model.fetch(Election.parse(pieces), electionsMap);
		Party party = (Party) Model.fetch(Party.parse(pieces), partiesMap);
		Coalition coalition = (Coalition) Model.fetch(Coalition.parse(pieces), coalitionsMap);
		Candidature candidature = Candidature.parse(pieces);

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

		Candidate candidate = Candidate.parse(pieces);

		Sex sex = (Sex) Model.fetchAndSave(Sex.parse(pieces), sexMap);
		Citizenship ctz = (Citizenship) Model.fetchAndSave(Citizenship.parse(pieces), ctzsMap);
		BirthTown town = (BirthTown) Model.fetch(BirthTown.parse(pieces),townsMap);
		BirthState state = (BirthState) Model.fetchAndSave(BirthState.parse(pieces),statesMap);

		candidate.setSex(sex);
		candidate.setBirthTown(town);
		candidate.setBirthState(state);
		candidate.setCitizenship(ctz);

		// Look for the candidate in map ...
		Candidate mapCandidate = (Candidate) candidatesMap.get(candidate);

		// ... if didn't find anything.
		if (mapCandidate == null) {

			// Set the ID for the new Candidate ...
			candidate.setId(numCandidates++);

			// ... and put it in the map.
			candidatesMap.put(candidate, candidate);

		}
		// ... if found something in the map.
		else {
			// Take a look if the objects are similar ...
			if (mapCandidate.similar(candidate)) {

				// Verify if the new candidate has more info details.
				if (candidate.validate() > mapCandidate.validate()) {

					// Set the new information to the mapped candidate
					mapCandidate.merge(candidate);
				} 
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
		Status status = Status.parse(pieces, year);

		Job job = (Job) Model.fetch(Job.parse(pieces), jobsMap);

		MaritalStatus maritalStatus = (MaritalStatus) 
				Model.fetchAndSave(MaritalStatus.parse(pieces), mStatusMap);

		Schooling schooling = (Schooling) Model.fetchAndSave(Schooling.parse(pieces), schMap);

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

	protected void save() {

		long start = System.currentTimeMillis();   

		System.out.println("\nTotal objects loaded");
		System.out.println("\tCandidates: " + candidatesMap.size());
		System.out.println("\tDuplicate Candidates: " + duppersList.size());
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