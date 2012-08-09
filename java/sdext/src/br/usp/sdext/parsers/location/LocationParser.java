package br.usp.sdext.parsers.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.location.MesoRegion;
import br.usp.sdext.models.location.MicroRegion;
import br.usp.sdext.models.location.Region;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.parsers.ModelParser;

public class LocationParser extends ModelParser {

	private ArrayList<Model> logs = new ArrayList<>();

	private HashMap<Model, Model> regionsMap = new HashMap<>();
	private HashMap<Model, Model> statesMap = new HashMap<>();
	private HashMap<Model, Model> mesosMap = new HashMap<>();
	private HashMap<Model, Model> microsMap = new HashMap<>();
	private HashMap<Model, Model> townsMap = new HashMap<>();
	
	private HashMap<Integer, Model> statesMapByIBGE = new HashMap<>();

	public HashMap<Model, Model> getRegionsMap() {return regionsMap;}
	public HashMap<Model, Model> getStatesMap() {return statesMap;}
	public HashMap<Model, Model> getMesosMap() {return mesosMap;}
	public HashMap<Model, Model> getMicrosMap() {return microsMap;}
	public HashMap<Model, Model> getTownsMap() {return townsMap;}

	public void parseFile(String filename, String action) throws Exception {

		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(filename)), "ISO-8859-1"));

		// Skip first line.
		String line = in.readLine();

		while ((line = in.readLine()) != null) {

			try {
				
				// Break line where finds ";"
				String pieces[] = line.split("\\t");

				// remove double quotes
				for (int i = 0; i < pieces.length; i++) {
					pieces[i] = pieces[i].replace("\"", "");
				}

				switch (action) {

				case "region":
					parseRegion(pieces);
					break;

				case "state":
					parseState(pieces);
					break;
					
				case "meso":
					parseMesoRegion(pieces);
					break;
					
				case "micro":
					parseMicroRegion(pieces);
					break;
					
				case "town":
					parseTown(pieces);
					break;

				default:
					throw new Exception("Invalid parser action.");
				}

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

	private void parseRegion(String[] pieces) throws Exception {

		Integer ibgeCode = parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];

		String acronym = pieces[3];
		String acronymx = pieces[4];

		String status = pieces[5];

		Region region = new Region(ibgeCode, name, namex, acronym, acronymx, status);

		Region mappedRegion = (Region) regionsMap.get(region);

		if (mappedRegion == null) {

			region = (Region) Model.fetch(region, regionsMap);

		} else {

			throw new Exception("Region already exists in map.");
		}
	}

	private void parseState(String[] pieces) throws Exception {

		Integer ibgeCode = parseInt(pieces[0]);

		String acronym = pieces[1];

		String name = pieces[2];
		String namex = pieces[3];

		Integer sinpasCode = parseInt(pieces[4]);

		Region region = new Region();
		region.ibgeCode = parseInt(pieces[5]);
		region = (Region) Model.fetch(region, regionsMap);

		Float area = Float.parseFloat(pieces[6]);

		String status = pieces[7];

		State state = new State(ibgeCode, acronym, name, namex, sinpasCode, region, area, status);

		State mappedState = (State) statesMap.get(state);

		if (mappedState == null) {

			state = (State) Model.fetch(state, statesMap);
			
			statesMapByIBGE.put(state.ibgeCode, state);
			
		} else {

			throw new Exception("State already exists in map.");
		}
	}
	
	private void parseMesoRegion(String[] pieces) throws Exception {

		Integer ibgeCode = parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];
		
		String acronym = pieces[3];

		State state = new State();
		state = (State) statesMapByIBGE.get(parseInt(pieces[4]));

		String status = pieces[5];
		
		MesoRegion mesoRegion = new MesoRegion(ibgeCode, name, namex, acronym, state, status);
		
		MesoRegion mappedMeso = (MesoRegion) mesosMap.get(mesoRegion);
		
		if (mappedMeso == null) {
			
			mesoRegion = (MesoRegion) Model.fetch(mesoRegion, mesosMap);
			
		} else {
			
			throw new Exception("Meso region already exists in map.");
		}

	}
	
	private void parseMicroRegion(String[] pieces) throws Exception {

		Integer ibgeCode = parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];
		
		String acronym = pieces[3];

		State state = new State();
		state = (State) statesMapByIBGE.get(parseInt(pieces[4]));

		String status = pieces[5];
		
		MicroRegion microRegion = new MicroRegion(ibgeCode, name, namex, acronym, state, status);
		
		MicroRegion mappedMicro= (MicroRegion) mesosMap.get(microRegion);
		
		if (mappedMicro == null) {
			
			microRegion = (MicroRegion) Model.fetch(microRegion, microsMap);
			
		} else {
			
			throw new Exception("Meso region already exists in map.");
		}

	}
	
	private void parseTown(String[] pieces) throws Exception {
		
		Integer ibgeCode = parseInt(pieces[0]);
		Integer ibgeCodeVD = parseInt(pieces[1]);
		
		String status = pieces[2];
		
		Integer sinpasCode = parseInt(pieces[3]);
		Integer siafiCode = parseInt(pieces[4]);
		
		String name = pieces[5];
		String namex = pieces[6];
		
		String obs = pieces[7];
		
		String altCode = pieces[8];
		String altCodeVD = pieces[9];
		
		Boolean legalAmazon = (pieces[10].equals("S")) ? true : false;
		Boolean border = (pieces[11].equals("S")) ? true : false;
		Boolean capital = (pieces[12].equals("S")) ? true : false;
		
		State state = new State();
		state = (State) statesMapByIBGE.get(parseInt(pieces[13]));
		
		MesoRegion mesoRegion = new MesoRegion();
		mesoRegion.ibgeCode = parseInt(pieces[14]);
		mesoRegion = (MesoRegion) mesosMap.get(mesoRegion);
		
		MicroRegion microRegion = new MicroRegion();
		microRegion.ibgeCode = parseInt(pieces[15]);
		microRegion = (MicroRegion) microsMap.get(microRegion);
		
		Float latitude = Float.parseFloat(pieces[25]);
		Float longetude = Float.parseFloat(pieces[26]);
		
		Float altitude = Float.parseFloat(pieces[27]);
		Float area = Float.parseFloat(pieces[28]);
		
		Town town = new Town(ibgeCode, ibgeCodeVD, status, sinpasCode, siafiCode,
				name, namex, obs, altCode, altCodeVD, legalAmazon, border, capital,
				state, mesoRegion, microRegion, latitude, longetude, altitude, area);
		
	}
	
	private Integer parseInt(String str) {
		
		if (str.replace(" ", "").equals("")) return null;

		Integer number = Integer.parseInt(str);
		
		return number;
	}

	@Override
	public void save() {

		Model.bulkSave(logs);
		System.out.println("\tSaving logs...");

		System.out.println("\tSaving regions...");
		Model.bulkSave(regionsMap.values());

		System.out.println("\tSaving states...");
		Model.bulkSave(statesMap.values());
		
		System.out.println("\tSaving meso regions...");
		Model.bulkSave(mesosMap.values());
		
		System.out.println("\tSaving micro regions...");
		Model.bulkSave(microsMap.values());
		
		System.out.println("\tSaving towns...");
		Model.bulkSave(townsMap.values());
	}

	public static void main(String[] args) throws Exception {

		LocationParser locationParser = new LocationParser();

		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/reg.csv", "region");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/uf.csv", "state");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/meso.csv", "meso");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/micro.csv", "micro");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/mun.csv", "town");

		locationParser.save();
	}
}
