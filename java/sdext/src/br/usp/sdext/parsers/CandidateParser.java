package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.sdext.core.Log;
import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Citizenship;
import br.usp.sdext.models.candidate.Sex;
import br.usp.sdext.models.candidate.status.Job;
import br.usp.sdext.models.candidate.status.MaritalStatus;
import br.usp.sdext.models.candidate.status.Schooling;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class CandidateParser extends AbstractParser {

	private LocationParser locationParser;

	private HashMap<Model, Model> candidatesMap = new HashMap<>();
	private HashMap<String, Model> candidatesMapByVoterId = new HashMap<>();
	
	private int diffVoter = 0;
	private int diffCand = 0;

	private HashMap<Model, Model> sexMap = new HashMap<>();
	private HashMap<Model, Model> ctzsMap = new HashMap<>();
	private HashMap<Model, Model> statusMap = new HashMap<>();
	private HashMap<Model, Model> jobsMap = new HashMap<>();
	private HashMap<Model, Model> maritalStatusMap = new HashMap<>();
	private HashMap<Model, Model> schoolingMap = new HashMap<>();

	private ArrayList<Model> duppersList = new ArrayList<>();

	private HashSet<Model> logs = new HashSet<>();

	public CandidateParser(LocationParser locationParser) {

		this.locationParser = locationParser;
	}
	
	public HashMap<Model, Model> getCandidatesMap() {return candidatesMap;}
	public HashMap<String, Model> getCandidatesMapByVoterId() {return candidatesMapByVoterId;}
	
	public HashMap<Model, Model> getSexMap() {return sexMap;}
	public HashMap<Model, Model> getCtzsMap() {return ctzsMap;}
	public HashMap<Model, Model> getStatusMap() {return statusMap;}
	public HashMap<Model, Model> getJobsMap() {return jobsMap;}
	public HashMap<Model, Model> getMaritalStatusMap() {return maritalStatusMap;}
	public HashMap<Model, Model> getSchMap() {return schoolingMap;}
	public ArrayList<Model> getDuppersList() {return duppersList;}

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

				parseCandidate(pieces);

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

	public Candidate parseCandidate(String[] pieces) throws Exception {

		// Parse candidate.
		String namex = parseCandidateNamex(pieces[10]);
		String voterID = pieces[26].trim();
		Date birthDate = Misc.parseDate(pieces[25]);
		
		if (namex == null) {

			throw new ParseException("Candidate name is invalid: ", pieces[10]);
		}

		if (voterID == null) {

			throw new ParseException("Candidate voter id is invalid: ", pieces[26]);
		}

		if (birthDate == null) {

			throw new ParseException("Candidate birth date is invalid: ", pieces[25]);
		}
		
		Candidate parsedCandidate =  new Candidate(voterID, namex, birthDate);

		// Check if name and birth date already exists
		Candidate mappedCandidate = (Candidate) candidatesMap.get(parsedCandidate);

		// if does not exist
		if (mappedCandidate == null) {

			// Check if same voter id has already been read
			mappedCandidate = (Candidate) candidatesMapByVoterId.get(parsedCandidate.getVoterID());
					
			// if found nothing then it's OK to persist
			if (mappedCandidate == null) {
				
				mappedCandidate = (Candidate) Model.persist(parsedCandidate, candidatesMap);
				candidatesMapByVoterId.put(mappedCandidate.getVoterID(), mappedCandidate);
			} 
			// else something is wrong with candidate
			else {
				
				// Accept if name is the same
				if (!parsedCandidate.hasSameNamex(mappedCandidate)) {
					
					// Accept if name is similar
					if (!parsedCandidate.hasSimilarNamex(mappedCandidate)) {
						
						// If all failed voter id is corrupted
						
						// Set mappedCandidate as duplicated
						mappedCandidate.setDupper(true);
						
						// Persist a new candidate to represent the new duplicate
						parsedCandidate.setDupper(true);
						mappedCandidate = (Candidate) Model.persist(parsedCandidate, candidatesMap);
						
						logs.add(new Log("Different candidates, but same voter id", parsedCandidate.getVoterID()));
						diffVoter++;
					} 
				} 
			}

		}
		// found by name and birth date
		else {

			// check if voter id is not the same
			if(!parsedCandidate.getVoterID().equals(mappedCandidate.getVoterID())) {

				logs.add(new Log("Same candidate, but different voter id",
						"parsed: " + parsedCandidate.getVoterID() + 
						"mapped: " + mappedCandidate.getVoterID()));
				diffCand++;
				mappedCandidate.setDupper(true);
			}
			return mappedCandidate;
		}
		
		// Parse remaining info.
		
		// Name.
		String name = Misc.parseStr(pieces[10]); 
		mappedCandidate.setName(name);
		
		// Sex.
		Long sexId = Misc.parseLong(pieces[28]); // tseID
		if (sexId == null) {throw new Exception("Sex id is invalid: " + pieces[28]);}
		String sexLabel = Misc.parseStr(pieces[29]); // label
		Sex sex = new Sex(sexId, sexLabel);
		sex = (Sex) Model.persist(sex, sexMap);
		mappedCandidate.setSex(sex);
		
		// Citizenship.
		Long ctzId = Misc.parseLong(pieces[34]); // tseID
		if (ctzId == null) {throw new Exception("Citizenship id is invalid: " + pieces[34]);}
		String ctzLabel = Misc.parseStr(pieces[35]); // label
		Citizenship ctz = new Citizenship(ctzId, ctzLabel);
		ctz = (Citizenship) Model.persist(ctz, ctzsMap);
		mappedCandidate.setCitizenship(ctz);
		
		Town birthTown = parseBirthTown(pieces);
		mappedCandidate.setBirthTown(birthTown);
		
		// Parse Status
		Integer year = parseInt(pieces[2]);
		
		Status status = new Status(year, mappedCandidate);
		
		// Age.
		Integer age = Misc.parseInt(pieces[27]); // age
		if (age == null) {age = Misc.getAge(birthDate);}
		status.setAge(age);

		// Job.
		Long jobId = Misc.parseLong(pieces[23]); // tseID
		if (jobId == null) {throw new Exception("Job id is invalid: " + pieces[23]);}
		String jobLabel = Misc.parseStr(pieces[24]); // label
		Job job = new Job(jobId, jobLabel);
		job = (Job) Model.persist(job, jobsMap);
		status.setJob(job);

		// Marital Status
		Long maritalId = Misc.parseLong(pieces[32]);
		if (maritalId == null) {throw new Exception("Marital status id is invalid: " + pieces[32]);}
		String maritalLabel = Misc.parseStr(pieces[33]);
		MaritalStatus maritalStatus =  new MaritalStatus(maritalId, maritalLabel);
		maritalStatus = (MaritalStatus) Model.persist(maritalStatus, maritalStatusMap);
		status.setMaritalStatus(maritalStatus);

		// Schooling.
		Long schoolingId = Misc.parseLong(pieces[30]);
		if (schoolingId == null) {throw new Exception("Schooling id is invalid: " + pieces[30]);}
		String schoolingLabel = Misc.parseStr(pieces[31]);
		Schooling schooling = new Schooling(schoolingId, schoolingLabel);
		schooling = (Schooling) Model.persist(schooling, schoolingMap);
		status.setSchooling(schooling);
		
		status = (Status) Model.persist(status, statusMap);
		
		return mappedCandidate;
	}

	public String parseCandidateNamex(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);

		str = str.replaceAll("[^\\p{ASCII}]", "");

		str = str.replaceAll("[\\s\\-()]", " ");

		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher = pattern.matcher(str);
		str = matcher.replaceAll(" ");

		return str.toUpperCase().trim();
	}

	private Town parseBirthTown(String pieces[]) throws Exception {

		Town mappedTown = null;

		Integer tseCode = parseInt(pieces[37]);

		if (tseCode != null) {

			mappedTown = (Town) locationParser.getTownsMapByTSE().get(tseCode);
		}

		if (mappedTown == null) {

			// Birth state.
			State mappedState = (State) locationParser.getStatesMap().get(new State(pieces[36]));

			if (mappedState == null) {

				mappedState = (State) locationParser.getStatesMap().get(new State("IG"));
			}

			Town town = new Town(locationParser.parseTownNamex(pieces[38]), mappedState);

			mappedTown = (Town) locationParser.getTownsMap().get(town);

			if (mappedTown == null) {

				mappedTown = locationParser.disambiguateTown(town);
			}

			// If all failed then town is lost.
			if (mappedTown == null) {

				if (mappedState.getAcronym().equals("DF")) {

					mappedState = (State) locationParser.getStatesMap().get(new State("GO"));
				} 

				if (mappedState.getAcronym().equals("IG")) {

					town.setNamex("IGNORADO OU EXTERIOR");

				} else {

					town.setNamex("MUNICIPIO IGNORADO   " + mappedState.getAcronym());
				}

				town.setState(mappedState);

				mappedTown = (Town) locationParser.getTownsMap().get(town);

				if (mappedTown == null) {

					System.err.println("error");
				}

				logs.add(new Log("Birth town not found.", pieces[38] + ", " + pieces[36] + ", " + pieces[37]));
			}
		}

		// Set TSE code if it is null
		if (tseCode != null && mappedTown.getTseCode() == null) {

			mappedTown.setTseCode(tseCode);
		}

		return mappedTown;
	}

	private Integer parseInt(String str) {

		if (str.replace(" ", "").equals("")) return null;

		Integer number = Integer.parseInt(str);

		if (number < 0) {

			return null;
		}

		return number;
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

		System.out.println("Saving candidates...");
		Model.bulkSave(logs);

		System.out.println("\t" + diffVoter + " duplicated voter ids.");
		System.out.println("\t" + diffCand + " candidates with more than one voter id.");
		System.out.println("\tTotal candidates: " + candidatesMap.size());
		
		Model.bulkSave(sexMap.values());
		Model.bulkSave(ctzsMap.values());
		Model.bulkSave(schoolingMap.values());
		Model.bulkSave(maritalStatusMap.values());
		Model.bulkSave(jobsMap.values());
		Model.bulkSave(candidatesMap.values());
		Model.bulkSave(statusMap.values());
		
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

		CandidateParser candidateParser = new CandidateParser(locationParser);

		candidateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2010");

		locationParser.save();
		candidateParser.save();
	}
}