package br.usp.sdext.parsers.candidature;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Citizenship;
import br.usp.sdext.models.candidate.Sex;
import br.usp.sdext.models.candidate.status.Job;
import br.usp.sdext.models.candidate.status.MaritalStatus;
import br.usp.sdext.models.candidate.status.Schooling;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.old.State;
import br.usp.sdext.models.old.Town;
import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.parsers.estate.EstateBinding;
import br.usp.sdext.util.Misc;

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
	
	private HashMap<EstateBinding, Status> bindings = new HashMap<>();
	
	private Long numElements = 0L;
	
	public CandidateParser(MiscParser miscParser) {
		
		this.miscParser = miscParser;
	}

	public Long getNumElements() {return numElements;}
	public HashMap<Model, Model> getCandidatesMap() {return candidatesMap;}
	public HashMap<Model, Model> getSexMap() {return sexMap;}
	public HashMap<Model, Model> getCtzsMap() {return ctzsMap;}
	public HashMap<Model, Model> getStatusMap() {return statusMap;}
	public HashMap<Model, Model> getJobsMap() {return jobsMap;}
	public HashMap<Model, Model> getMaritalStatusMap() {return maritalStatusMap;}
	public HashMap<Model, Model> getSchMap() {return schMap;}
	public HashMap<EstateBinding, Status> getBindings() {return bindings;}
	public ArrayList<Model> getDuppersList() {return duppersList;}

	public Model parse(String[] pieces) throws Exception {

		// Name.
		String name = Misc.parseStr(pieces[10]); // name
		if (name == null) {throw new Exception("Candidate name is invalid: " + pieces[10]);}
		
		// Birth date
		Date birthDate = Misc.parseDate(pieces[25]); // birth date
		if (birthDate == null) {throw new Exception("Candidate birth date is invalid: " + pieces[25]);}
		
		// Voter-ID
		Long candidateVoterID = Misc.parseLong(pieces[26]); // voterID
		if (candidateVoterID == null) {throw new Exception("Candidate voter id is invalid: " + pieces[26]);}
		
		Candidate candidate = new Candidate(candidateVoterID, name, birthDate);
		
		// Look for the candidate in map ...
		Candidate mapCandidate = (Candidate) candidatesMap.get(candidate);

		// ... if didn't find anything.
		if (mapCandidate == null) {
			
			// Sex.
			Long sexId = Misc.parseLong(pieces[28]); // tseID
			if (sexId == null) {throw new Exception("Sex id is invalid: " + pieces[28]);}
			String sexLabel = Misc.parseStr(pieces[29]); // label
			Sex sex = new Sex(sexId, sexLabel);
			sex = (Sex) Model.fetch(sex, sexMap);
			candidate.setSex(sex);
			
			// Citizenship.
			Long ctzId = Misc.parseLong(pieces[34]); // tseID
			if (ctzId == null) {throw new Exception("Citizenship id is invalid: " + pieces[34]);}
			String ctzLabel = Misc.parseStr(pieces[35]); // label
			Citizenship ctz = new Citizenship(ctzId, ctzLabel);
			ctz = (Citizenship) Model.fetch(ctz, ctzsMap);
			candidate.setCitizenship(ctz);

			// Birth state.
			String stateLabel = Misc.parseStr(pieces[36]); // label
			if (stateLabel == null) {throw new Exception("State label is invalid: " + pieces[36]);}
			State birthState =  new State(stateLabel);
			birthState = (State) Model.fetch(birthState, miscParser.getStatesMap());
			
			// Birth town.
			Long id = Misc.parseLong(pieces[37]);
			String label = Misc.parseStr(pieces[38]);
			if (label == null) {throw new Exception("Town label is invalid: " + pieces[38]);}
			Town birthTown = new Town(id, label);
			birthTown.setState(birthState);
			birthTown = (Town) Model.fetch(birthTown, miscParser.getTownsMap());
			candidate.setBirthTown(birthTown);


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

		// TSE ID.
		Long tseId = Misc.parseLong(pieces[11]); // id
		if (tseId == null) {throw new Exception("Candidate TSE id is invalid: " + pieces[11]);}
		
		// Age.
		Integer age = Misc.parseInt(pieces[27]); // age
		if (age == null) {age = Misc.getAge(birthDate);}
		
		Status status = new Status(miscParser.getYear(), age, tseId);

		// Job.
		Long jobId = Misc.parseLong(pieces[23]); // tseID
		if (jobId == null) {throw new Exception("Job id is invalid: " + pieces[23]);}
		String jobLabel = Misc.parseStr(pieces[24]); // label
		Job job = new Job(jobId, jobLabel);
		job = (Job) Model.fetch(job, jobsMap);
		status.setJob(job);

		// Marital Status
		Long maritalId = Misc.parseLong(pieces[32]);
		if (maritalId == null) {throw new Exception("Marital status id is invalid: " + pieces[32]);}
		String maritalLabel = Misc.parseStr(pieces[33]);
		MaritalStatus maritalStatus =  new MaritalStatus(maritalId, maritalLabel);
		maritalStatus = (MaritalStatus) Model.fetch(maritalStatus, maritalStatusMap);
		status.setMaritalStatus(maritalStatus);

		// Schooling.
		Long schoolingId = Misc.parseLong(pieces[30]);
		if (schoolingId == null) {throw new Exception("Schooling id is invalid: " + pieces[30]);}
		String schoolingLabel = Misc.parseStr(pieces[31]);
		Schooling schooling = new Schooling(schoolingId, schoolingLabel);
		schooling = (Schooling) Model.fetch(schooling, schMap);
		status.setSchooling(schooling);

		status.setCandidate(candidate);
		
		status = (Status) Model.fetch(status, statusMap);
		
		bindings.put(new EstateBinding(status), status);
		
		return candidate;
	}

	@Override
	public void save() {

		System.out.println("\tSaving candidates...");
		Model.bulkSave(sexMap.values());
		Model.bulkSave(ctzsMap.values());
		Model.bulkSave(schMap.values());
		Model.bulkSave(maritalStatusMap.values());
		Model.bulkSave(jobsMap.values());
		Model.bulkSave(candidatesMap.values());
		Model.bulkSave(statusMap.values());

		System.out.println("\tSaving duppers...");
		Model.bulkSave(duppersList);

	}
}
