package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import br.usp.sdext.core.Log;
import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.status.Estate;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.location.State;
import br.usp.sdext.parsers.bindings.EstateBinding;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class EstateParser extends AbstractParser {

	private LocationParser locationParser;
	private CandidateParser candidateParser;
	private CandidatureParser candidatureParser;
	
	private ArrayList<Model> estateList = new ArrayList<>();
	private ArrayList<Model> logs = new ArrayList<>();
	
	private Long notFound = 0L;

	public EstateParser(LocationParser locationParser, CandidateParser candidateParser,
			CandidatureParser candidatureParser) {
		
		this.locationParser = locationParser;
		this.candidateParser = candidateParser;
		this.candidatureParser = candidatureParser;
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

				parseEstate(pieces);

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

	private Estate parseEstate(String[] pieces) throws Exception {

		Integer year = Misc.parseInt(pieces[2]);

		Long candidateTseId = Misc.parseLong(pieces[5]);
		
		if (candidateTseId == null) {
			
			throw new ParseException("Candidate TSE id is invalid: ", pieces[5]);
		}
		
		State state = new State(pieces[4]);

		State mappedState = (State) locationParser.getStatesMap().get(state);

		if (mappedState == null) {

			throw new ParseException("Election state not found in map" , state.getAcronym());
		}
		
		EstateBinding estateBinding = new EstateBinding(year, mappedState, candidateTseId);
		
		Candidature mappedCandidature = (Candidature) candidatureParser.getEstateBindings().get(estateBinding);
		
		if (mappedCandidature == null) {
			
			notFound++;
			return null;
		}
		
		Candidate mappedCandidate = mappedCandidature.getCandidate();
		
		Status parsedStatus = new Status(year, mappedCandidate);
		
		Status mappedStatus = (Status) candidateParser.getStatusMap().get(parsedStatus);
		
		if (mappedStatus == null) {
			
			throw new ParseException("Candidate status not found", parsedStatus.toString());
		}
		
		Long estateTseId = Misc.parseLong(pieces[6]);
		String label = Misc.parseStr(pieces[7]);
		String detail = Misc.parseStr(pieces[8]);
		Float value = Misc.parseFloat(pieces[9]);
		Date registryDate = Misc.parseDate(pieces[10]);
		
		Estate estate = new Estate(estateTseId, label, detail, value, registryDate);
		estate.setId(new Long(estateList.size()));
		estateList.add(estate);
		mappedStatus.addEstate(estate);
		
		return estate;		
	}

	public void save() {

		Model.bulkSave(logs);
		System.out.println("Saving estates...");
		System.out.println("\tInvalid states: " + notFound);
		System.out.println("\tValid states: " + estateList.size());
		Model.bulkSave(estateList);
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

		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2010");
		electionParser.addSpecialValues();

		CandidateParser candidateParser = new CandidateParser(locationParser);

		CoalitionParser coalitionParser = new CoalitionParser();

		PartyParser partyParser = new PartyParser();

		CandidatureParser candidatureParser = new CandidatureParser(locationParser, electionParser, 
				candidateParser, partyParser, coalitionParser);

		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2010");
		
		EstateParser estateParser = new EstateParser(locationParser, candidateParser, candidatureParser);
		
		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2010");

		locationParser.save();
		electionParser.save();
		coalitionParser.save();
		partyParser.save();
		estateParser.save();
		candidateParser.save();
		candidatureParser.save();
	}
}




