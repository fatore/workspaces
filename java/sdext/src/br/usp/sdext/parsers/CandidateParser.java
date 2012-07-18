package br.usp.sdext.parsers;

import java.util.ArrayList;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Citizenship;
import br.usp.sdext.models.candidate.Sex;
import br.usp.sdext.models.candidate.status.Job;
import br.usp.sdext.models.candidate.status.MaritalStatus;
import br.usp.sdext.models.candidate.status.Schooling;
import br.usp.sdext.models.candidate.status.Status;

public class CandidateParser extends ModelParser {
	
	private MiscParser miscParser;

	private HashMap<Model, Model> candidatesMap = new HashMap<>();
	private HashMap<Model, Model> sexMap = new HashMap<>();
	private HashMap<Model, Model> ctzsMap = new HashMap<>();

	private ArrayList<Model> duppersList = new ArrayList<>();

	private HashMap<Model, Model> statusMap = new HashMap<>();
	private HashMap<Model, Model> jobsMap = new HashMap<>();
	private HashMap<Model, Model> maritalStatusMap = new HashMap<>();
	private HashMap<Model, Model> schMap = new HashMap<>();
	
	private Long numElements = 0L;
	
	public CandidateParser(MiscParser miscParser) {
		
		this.miscParser = miscParser;
	}

	public Long getNumElements() {return numElements;}
	public MiscParser getMiscParser() {return miscParser;}
	public HashMap<Model, Model> getCandidatesMap() {return candidatesMap;}
	public HashMap<Model, Model> getSexMap() {return sexMap;}
	public HashMap<Model, Model> getCtzsMap() {return ctzsMap;}
	public HashMap<Model, Model> getStatusMap() {return statusMap;}
	public HashMap<Model, Model> getJobsMap() {return jobsMap;}
	public HashMap<Model, Model> getMaritalStatusMap() {return maritalStatusMap;}
	public HashMap<Model, Model> getSchMap() {return schMap;}
	public ArrayList<Model> getDuppersList() {return duppersList;}

	public Model parse(String[] pieces) throws Exception {

		Candidate candidate = Candidate.parse(pieces);

		// Look for the candidate in map ...
		Candidate mapCandidate = (Candidate) candidatesMap.get(candidate);

		// ... if didn't find anything.
		if (mapCandidate == null) {

			Sex sex =  Sex.parse(pieces);
			Citizenship ctz = Citizenship.parse(pieces);
			State birthState = State.parse(pieces);
			Town birthTown = Town.parse(pieces);

			// Fetch data.
			sex = (Sex) Model.fetch(sex, sexMap);
			ctz = (Citizenship) Model.fetch(ctz, ctzsMap);
			birthState = (State) Model.fetch(birthState, miscParser.getStatesMap());

			birthTown.setState(birthState);
			birthTown = (Town) Model.fetch(birthTown, miscParser.getTownsMap());

			// Set data.
			candidate.setSex(sex);
			candidate.setBirthTown(birthTown);
			candidate.setCitizenship(ctz);

			// Set the ID for the new Candidate ...
			candidate.setId(numElements++);

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
				candidate.setId(numElements++);

				// ... and add the new Candidate to a duppers list
				duppersList.add(candidate);
			}
		}

		// Parse Status.
		Status status = Status.parse(pieces, miscParser.getYear());

		Job job = Job.parse(pieces);
		job = (Job) Model.fetch(job, jobsMap);

		MaritalStatus maritalStatus = MaritalStatus.parse(pieces);
		maritalStatus = (MaritalStatus) Model.fetch(maritalStatus, maritalStatusMap);

		Schooling schooling = Schooling.parse(pieces);
		schooling = (Schooling) Model.fetch(schooling, schMap);

		status.setJob(job);
		status.setMaritalStatus(maritalStatus);
		status.setSchooling(schooling);

		// Bind objects.
		status.setCandidate(candidate);
		
		Model.fetch(status, statusMap);

		return candidate;
	}

	@Override
	public void save() {

		System.out.println("\tSaving candidates...");
		Model.bulkSave(sexMap.values());
		Model.bulkSave(ctzsMap.values());
		Model.bulkSave(candidatesMap.values());

		System.out.println("\tSaving duppers...");
		Model.bulkSave(duppersList);

		System.out.println("\tSaving candidates status...");
		Model.bulkSave(schMap.values());
		Model.bulkSave(maritalStatusMap.values());
		Model.bulkSave(jobsMap.values());
		Model.bulkSave(statusMap.values());
	}
}
