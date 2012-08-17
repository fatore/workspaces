package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Candidate;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.models.old.Status;
import br.usp.sdext.parsers.old.EstateBinding;
import br.usp.sdext.util.LevenshteinDistance;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class CandidateParser extends AbstractParser {

	private LocationParser locationParser;

	private HashMap<Model, Model> candidatesMap = new HashMap<>();

	private HashMap<Model, Model> sexMap = new HashMap<>();
	private HashMap<Model, Model> ctzsMap = new HashMap<>();
	private HashMap<Model, Model> statusMap = new HashMap<>();
	private HashMap<Model, Model> jobsMap = new HashMap<>();
	private HashMap<Model, Model> maritalStatusMap = new HashMap<>();
	private HashMap<Model, Model> schoolingMap = new HashMap<>();
	
	private HashMap<EstateBinding, Status> bindings = new HashMap<>();

	private ArrayList<Model> duppersList = new ArrayList<>();

	private Long numElements = 0L;

	private ArrayList<Model> logs = new ArrayList<>();

	public CandidateParser(LocationParser locationParser) {

		this.locationParser = locationParser;
	}
	
	public Long getNumElements() {return numElements;}
	public HashMap<Model, Model> getCandidatesMap() {return candidatesMap;}
	public HashMap<Model, Model> getSexMap() {return sexMap;}
	public HashMap<Model, Model> getCtzsMap() {return ctzsMap;}
	public HashMap<Model, Model> getStatusMap() {return statusMap;}
	public HashMap<Model, Model> getJobsMap() {return jobsMap;}
	public HashMap<Model, Model> getMaritalStatusMap() {return maritalStatusMap;}
	public HashMap<Model, Model> getSchMap() {return schoolingMap;}
	public HashMap<EstateBinding, Status> getBindings() {return bindings;}
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

	private void parseCandidate(String[] pieces) throws Exception {

		String namex = parseCandidateNamex(pieces[10]);

		if (namex == null) {

			throw new ParseException("Candidate name is invalid: ", pieces[10]);
		}

		Long voterID = parseLong(pieces[26]);

		if (voterID == null) {

			throw new ParseException("Candidate voter id is invalid: ", pieces[26]);
		}

		Date birthDate = Misc.parseDate(pieces[25]);

		if (birthDate == null) {

			throw new ParseException("Candidate birth date is invalid: ", pieces[25]);
		}

		Candidate parsedCandidate = new Candidate(voterID, namex, birthDate);

		Candidate mappedCandidate = (Candidate) candidatesMap.get(parsedCandidate);

		if (mappedCandidate == null) {

			mappedCandidate = (Candidate) Model.persist(parsedCandidate, candidatesMap);

		} else {

			// Check if doesn't has same name
			if(!mappedCandidate.hasSameName(parsedCandidate)) {
				
				Candidate similar = findMisspelledCandidate(parsedCandidate, 3);
				
				if (similar == null) {
					
					System.err.println();
					System.err.println(parsedCandidate.toString());
					System.err.println(mappedCandidate.toString());
					System.err.println();
					
				} else {
					
					mappedCandidate = similar;
				}
			}
		}

	}
	
	public Candidate findMisspelledCandidate(Candidate missCandidate, int threshold) {

		Iterator<Entry<Model, Model>> i = candidatesMap.entrySet().iterator();

		while (i.hasNext()) {

			Candidate candidate = (Candidate) ((Entry<Model, Model>) i.next()).getValue();

			if (missCandidate.getVoterID().equals(candidate.getVoterID())) {

				int distance = LevenshteinDistance.computeLevenshteinDistance(
						missCandidate.getNamex(), candidate.getNamex());

				if (distance <= threshold) {

					return candidate;
				}
			}
		}
		return null;
	}

	private void parseMisc(String[] pieces) throws Exception {

		String name = parseCandidateNamex(pieces[10]);

		if (name == null) {

			throw new ParseException("Candidate name is invalid: ", pieces[10]);
		}

		Long voterID = parseLong(pieces[26]);

		if (voterID == null) {

			throw new ParseException("Candidate voter id is invalid: ", pieces[26]);
		}

		Date birthDate = Misc.parseDate(pieces[25]);

		if (birthDate == null) {

			throw new ParseException("Candidate birth date is invalid: ", pieces[25]);
		}

		Candidate candidate = new Candidate(voterID, name, birthDate);

		// Look for the candidate in map ...
		Candidate mappedCandidate = (Candidate) candidatesMap.get(candidate);

		// ... if didn't find anything.
		if (mappedCandidate == null) {

			// Birth town.
			Town mappedTown = parseBirthTown(pieces);

			// Set the ID for the new Candidate ...
			candidate.setId(numElements++);

			// ... and put it in the map.
			candidatesMap.put(candidate, candidate);
		}
		// ... if found something in the map.
		else {
			// Take a look if the objects are similar ...
			if (mappedCandidate.hasSameName(candidate)) {

				candidate = mappedCandidate;
			} 
			// Objects aren't similar! 
			// VoterID is the same but not other attributes. !?
			else {

				// Set both candidates as "duppers".
				mappedCandidate.setDupper(true);
				candidate.setDupper(true);

				// Set the ID for the new Candidate ...
				candidate.setId(numElements++);

				// ... and add the new Candidate to a duppers list
				duppersList.add(candidate);
			}
		}
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

	private Long parseLong(String str) {

		if (str.replace(" ", "").equals("")) return null;

		Long number = Long.parseLong(str);

		if (number < 1) {

			return null;
		}

		return number;
	}


	public void save() {

		Model.bulkSave(logs);

		System.out.println("Saving canidates...");
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

		candidateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2012");

		locationParser.save();
		candidateParser.save();
	}
}