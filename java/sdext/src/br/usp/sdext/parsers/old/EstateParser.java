package br.usp.sdext.parsers.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.old.Estate;
import br.usp.sdext.models.old.Log;
import br.usp.sdext.models.old.Status;
import br.usp.sdext.parsers.AbstractParser;
import br.usp.sdext.util.Misc;

public class EstateParser extends AbstractParser {
	
	private HashMap<EstateBinding, Status> bindings = new HashMap<>();
	private ArrayList<Model> estateList = new ArrayList<>();
	private ArrayList<Model> logs = new ArrayList<>();
	
	public EstateParser(HashMap<EstateBinding, Status> bindings) {
		
		this.bindings = bindings;
	}
	
	protected void loadFile(File file) throws Exception {

		if (file.getName().matches("([^\\s]+(\\.(?i)txt))")) {

			System.out.println("Parsing candidates estates from " + file.getName());
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

					if (element.getClassName().contains("br.usp.sdext.parsers")) {
						exceptionClass = element.getClassName();
						exceptionMethod = element.getMethodName();
						break;
					}
				}
				Log log = new Log(line,"CAUSED BY: " + exceptionMethod 
						+ " IN CLASS: " + exceptionClass, e.getMessage());
				logs.add(log);
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
		
		Integer year = Misc.parseInt(pieces[2]);
		
		Long estateTseId = null;
		Long candidateTseId = null;
		String label = null;
		String detail = null;
		Float value = null;
		Date registryDate = null;

		candidateTseId = Misc.parseLong(pieces[5]);
		estateTseId = Misc.parseLong(pieces[6]);
		label = Misc.parseStr(pieces[7]);
		detail = Misc.parseStr(pieces[8]);
		value = Misc.parseFloat(pieces[9]);
		registryDate = Misc.parseDate(pieces[10]);
		
		if (candidateTseId == null) {throw new Exception("Candidate Tse id is invalid: " + pieces[5]);}
		
		Status status = new Status();
		status.setTseID(candidateTseId);
		status.setYear(year);
		
		Status mappedStatus = (Status) bindings.get(new EstateBinding(status));
		
		if (mappedStatus == null) {
			
			throw new Exception("Candidate status not found. year: " + year + ", tseID:" + candidateTseId);
			
		} else {
			
			Estate estate = new Estate(estateTseId, label, detail, value, registryDate);
			estate.setId(new Long(estateList.size()));
			estateList.add(estate);
			mappedStatus.addEstate(estate);
		}
	}

	public void save() {

		long start = System.currentTimeMillis();   

		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");
		
		System.out.println("\tSaving estates...");
		Model.bulkSave(estateList);
		Model.bulkSave(logs);

		long elapsedTime = System.currentTimeMillis() - start;

		System.out.printf("Finished saving after %d mins and %d secs\n",
				+  (int) (elapsedTime / 60000),(int) (elapsedTime % 60000) / 1000);
	}

	protected void printResults() {
		
		System.out.println("\nTotal objects loaded");
		System.out.println("\tEstates: " + estateList.size());
		System.out.println("\tLog Entries: " + logs.size());
	}
}