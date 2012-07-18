package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.Party;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.util.Misc;

public class MainParser extends AbstractParser {
	
	private MiscParser miscParser;
	private CandidateParser candidateParser;
	private ElectionParser electionParser;
	private PartyParser partyParser;
	private CoalitionParser coalitionParser;
	private HashMap<Model, Model> candidatureMap = new HashMap<>();

	private int incompleteEntries = 0;

	
	public MainParser() {
		
		miscParser = new MiscParser();
		candidateParser = new CandidateParser(miscParser);
		electionParser = new ElectionParser(miscParser);
		partyParser = new PartyParser();
		coalitionParser = new CoalitionParser();
	}

	protected void loadFile(File file) throws Exception {

		miscParser.setYear(Integer.parseInt(file.getParentFile().getName()));

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
				incompleteEntries++;
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

		Candidate candidate = (Candidate) candidateParser.parse(pieces);
		
		Election election = (Election) electionParser.parse(pieces);
		Party party = (Party) partyParser.parse(pieces);
		Coalition coalition = (Coalition) coalitionParser.parse(pieces);
		
		Candidature candidature = new Candidature(
				Misc.parseStr(pieces[13]), // ballot name
				Misc.parseInt(pieces[12]), // ballot no
				Misc.parseLong(pieces[14]), // sit ID
				Misc.parseStr(pieces[15]), // sit
				Misc.parseFloat(pieces[39]), // max exp
				Misc.parseLong(pieces[40]), // result id
				Misc.parseStr(pieces[41])); // result

		
		// Bind objects.
		candidature.setCandidate(candidate);
		candidature.setElection(election);
		candidature.setParty(party);
		candidature.setCoalition(coalition);
		
		Candidature mappedCandidature = (Candidature) candidatureMap.get(candidature);

		if (mappedCandidature == null) {
			
			candidature.setId(new Long(candidatureMap.size()));
			candidatureMap.put(candidature, candidature);
			
		} else {
			
			System.err.println("something is wrong");
			throw new Exception();
		}
	}

	public void save() {

		long start = System.currentTimeMillis();   

		System.out.println("\nTotal objects loaded");
		System.out.println("\tCandidates: " + candidateParser.getCandidatesMap().size());
		System.out.println("\tDuplicate Candidates: " + candidateParser.getDuppersList().size());
		System.out.println("\tElections: " + electionParser.getElectionsMap().size());
		System.out.println("\tParties: " + partyParser.getPartiesMap().size());
		System.out.println("\tCoalitions: " + coalitionParser.getCoalitionsMap().size());
		System.out.println("\tCandidatures: " + candidatureMap.size());
		System.out.println("\tIncomplete Entries: " + incompleteEntries);

		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");
		
		miscParser.save();
		candidateParser.save();
		electionParser.save();
		partyParser.save();
		coalitionParser.save();
		

		System.out.println("\tSaving candidatures...");
		Model.bulkSave(candidatureMap.values());

		long elapsedTime = System.currentTimeMillis() - start;

		System.out.printf("Finished saving after %d mins and %d secs\n",
				+  (int) (elapsedTime / 60000),(int) (elapsedTime % 60000) / 1000);
	}
}