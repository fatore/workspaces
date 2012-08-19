package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import br.usp.sdext.core.Log;
import br.usp.sdext.core.Model;
import br.usp.sdext.models.location.MesoRegion;
import br.usp.sdext.models.location.MicroRegion;
import br.usp.sdext.models.location.Region;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.util.LevenshteinDistance;
import br.usp.sdext.util.ParseException;

public class LocationParser {

	private ArrayList<Model> logs = new ArrayList<>();

	private HashMap<Model, Model> regionsMap = new HashMap<>();
	private HashMap<Model, Model> statesMap = new HashMap<>();
	private HashMap<Model, Model> mesosMap = new HashMap<>();
	private HashMap<Model, Model> microsMap = new HashMap<>();
	private HashMap<Model, Model> townsMap = new HashMap<>();

	private HashMap<Integer, Model> statesMapByIBGE = new HashMap<>();
	private HashMap<Integer, Model> townsMapByIBGE = new HashMap<>();
	private HashMap<Integer, Model> townsMapByTSE= new HashMap<>();

	private HashMap<Model, Model> lostTownsMap= new HashMap<>();
	
	public HashMap<Model, Model> getRegionsMap() {return regionsMap;}
	public HashMap<Model, Model> getStatesMap() {return statesMap;}
	public HashMap<Model, Model> getMesosMap() {return mesosMap;}
	public HashMap<Model, Model> getMicrosMap() {return microsMap;}
	public HashMap<Model, Model> getTownsMap() {return townsMap;}
	public HashMap<Integer, Model> getTownsMapByIBGE() {return townsMapByIBGE;}
	public HashMap<Model, Model> getLostTownsMap() {return lostTownsMap;}
	public HashMap<Integer, Model> getTownsMapByTSE() {return townsMapByTSE;}
	
	public void addSpecialValues() {
		
		Region mappedRegion = (Region) regionsMap.get(new Region(0));
		
		State state = new State(0, "BR", "Brasil", "BRASIL", -1, mappedRegion, 0f, "IGNOR");
		
		Model.persist(state, statesMap);
	}
	
	public void parseFile(String filename, String action) throws Exception {

		BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(filename)), "ISO-8859-1"));

		// Skip first line.
		String line = in.readLine();
		String pieces[] = null;

		while ((line = in.readLine()) != null) {

			try {

				// Break line where finds tab
				pieces = line.split("\\t");

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
					throw new Exception("Invalid parser action");
				}

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

	private void parseRegion(String[] pieces) throws Exception {

		Integer ibgeCode = Integer.parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];

		String acronym = pieces[3];
		String acronymx = pieces[4];

		String status = pieces[5];
		
		Region parsedRegion = new Region(ibgeCode, name, namex, acronym, acronymx, status);

		Region mappedRegion = (Region) regionsMap.get(parsedRegion);

		if (mappedRegion == null) {

			mappedRegion = (Region) Model.persist(parsedRegion, regionsMap);

		} else {

			throw new ParseException("Region already exists in map");
		}
	}

	private void parseState(String[] pieces) throws Exception {

		Integer ibgeCode = Integer.parseInt(pieces[0]);

		String acronym = pieces[1];

		String name = pieces[2];
		String namex = pieces[3];

		Integer sinpasCode = Integer.parseInt(pieces[4]);

		Region region = new Region();
		region.setIbgeCode(Integer.parseInt(pieces[5]));
		region = (Region) Model.persist(region, regionsMap);

		Float area = Float.parseFloat(pieces[6]);

		String status = pieces[7];

		State parsedState = new State(ibgeCode, acronym,
				name, namex, sinpasCode, region, area, status);
		
		State mappedState = (State) statesMap.get(parsedState);

		if (mappedState == null) {

			mappedState = (State) Model.persist(parsedState, statesMap);

			statesMapByIBGE.put(mappedState.getIbgeCode(), mappedState);

		} else {

			throw new ParseException("State already exists in map");
		}
	}

	private void parseMesoRegion(String[] pieces) throws Exception {

		Integer ibgeCode = Integer.parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];

		String acronym = pieces[3];

		State state = new State();
		state = (State) statesMapByIBGE.get(Integer.parseInt(pieces[4]));

		String status = pieces[5];
		
		MesoRegion parsedMeso = new MesoRegion(ibgeCode, name, namex, acronym, state, status);

		MesoRegion mappedMeso = (MesoRegion) mesosMap.get(parsedMeso);

		if (mappedMeso == null) {

			mappedMeso = (MesoRegion) Model.persist(parsedMeso, mesosMap);

		} else {

			throw new ParseException("Mesoregion already exists in map");
		}
	}

	private void parseMicroRegion(String[] pieces) throws Exception {

		Integer ibgeCode = Integer.parseInt(pieces[0]);

		String name = pieces[1];
		String namex = pieces[2];

		String acronym = pieces[3];

		State state = new State();
		state = (State) statesMapByIBGE.get(Integer.parseInt(pieces[4]));

		String status = pieces[5];
		
		MicroRegion parsedMicro = new MicroRegion(ibgeCode, name, namex, acronym, state, status);

		MicroRegion mappedMicro= (MicroRegion) mesosMap.get(parsedMicro);

		if (mappedMicro == null) {

			mappedMicro = (MicroRegion) Model.persist(parsedMicro, microsMap);

		} else {

			throw new ParseException("Microregion already exists in map");
		}
	}

	private void parseTown(String[] pieces) throws Exception {

		Integer ibgeCode = Integer.parseInt(pieces[0]);
		Integer ibgeCodeVD = Integer.parseInt(pieces[1]);

		String status = pieces[2];

		Integer sinpasCode = parseInt(pieces[3]);
		Integer siafiCode = parseInt(pieces[4]);

		String name = pieces[5];
		String namex = parseTownNamex(pieces[6]);

		String obs = pieces[7];

		String altCode = pieces[8];
		String altCodeVD = pieces[9];

		Boolean legalAmazon = (pieces[10].equals("S")) ? true : false;
		Boolean border = (pieces[11].equals("S")) ? true : false;
		Boolean capital = (pieces[12].equals("S")) ? true : false;

		State state = new State();
		state = (State) statesMapByIBGE.get(Integer.parseInt(pieces[13]));

		MesoRegion mesoRegion = new MesoRegion();
		mesoRegion.setIbgeCode(Integer.parseInt(pieces[14]));
		mesoRegion = (MesoRegion) mesosMap.get(mesoRegion);

		MicroRegion microRegion = new MicroRegion();
		microRegion.setIbgeCode(Integer.parseInt(pieces[15]));
		microRegion = (MicroRegion) microsMap.get(microRegion);

		Float latitude = Float.parseFloat(pieces[25]);
		Float longetude = Float.parseFloat(pieces[26]);

		Float altitude = Float.parseFloat(pieces[27]);
		Float area = Float.parseFloat(pieces[28]);
		
		Town parsedTown = new Town(ibgeCode, ibgeCodeVD, status, sinpasCode, siafiCode,
				name, namex, obs, altCode, altCodeVD, legalAmazon, border, capital,
				state, mesoRegion, microRegion, latitude, longetude, altitude, area);

		Town mappedTown = (Town) townsMap.get(parsedTown);

		if (mappedTown == null) {

			mappedTown = (Town) Model.persist(parsedTown, townsMap);

			townsMapByIBGE.put(mappedTown.getIbgeCode(), mappedTown);

		} else {

			throw new ParseException("Town already exists in map");
		}
	}

	private Integer parseInt(String str) {

		if (str.replace(" ", "").equals("")) return null;

		return Integer.parseInt(str);
	}

	public String parseTownNamex(String str) {

		str = Normalizer.normalize(str, Normalizer.Form.NFD);

		str = str.replaceAll("[^\\p{ASCII}]", "");

		str = str.replaceAll("[\\s\\-()]", " ");

		return str.toUpperCase().trim();
	}
	
	public Town findMisspelledTown(Town missTown, int threshold) {

		Iterator<Entry<Model, Model>> i = getTownsMap().entrySet().iterator();

		while (i.hasNext()) {

			Town town = (Town) ((Entry<Model, Model>) i.next()).getValue();

			if (missTown.getState().equals(town.getState())) {

				int distance = LevenshteinDistance.computeLevenshteinDistance(missTown.getNamex(), town.getNamex());

				if (distance <= threshold) {

					return town;
				}
			}
		}
		return null;
	}
	
	public Town guessTownNames(Town town) {
		
		switch (town.getNamex()) {
		
		case "SAO VALERIO DO TOCANTINS":
			town.setNamex("SAO VALERIO DA NATIVIDADE");
			break;
			
		case "ITAMARACA":
			town.setNamex("ILHA DE ITAMARACA");
			break;
			
		case "GOVERNADOR LOMANTO JUNIOR":
			town.setNamex("BARRO PRETO");
			break;
			
		case "TACIMA":
			town.setNamex("CAMPO DE SANTANA");
			break;
			
		case "SAO DOMINGOS DE POMBAL":
			town.setNamex("SAO DOMINGOS");
			break;
			
		case "SAO VICENTE DO SERIDO":
			town.setNamex("SERIDO");
			break;
			
		case "ASSU":
			town.setNamex("ACU");
			break;
			
		case "BOA SAUDE":
			town.setNamex("JANUARIO CICCO");
			break;
			
		case "CAMPO GRANDE":
			town.setNamex("AUGUSTO SEVERO");
			break;
			
		case "SERRA CAIADA":
			town.setNamex("PRESIDENTE JUSCELINO");
			break;
			
		case "COUTO DE MAGALHAES":
			town.setNamex("COUTO MAGALHAES");
			break;
			
		default:
			return null;
		}
		
		return (Town) townsMap.get(town);
	}
	
	public Town disambiguateTown(Town town) {
		
		Town mappedTown;
		
		// Try to find similar.
		if ((mappedTown = findMisspelledTown(town, 1)) == null) {

			Town test = new Town();
			test.setNamex(town.getNamex().replaceAll("DO OESTE", "D'OESTE"));
			test.setState(town.getState());
			mappedTown = (Town) getTownsMap().get(test);
		}
		
		if (mappedTown == null) {
			
			// Try individually guesses..
			mappedTown = guessTownNames(town);
		}
		
		return mappedTown;
	}

	public void save() {

		Model.bulkSave(logs);
		System.out.println("Saving location data...");

		System.out.println("\t" + regionsMap.size() + " regions...");
		Model.bulkSave(regionsMap.values());

		System.out.println("\t" + statesMap.size() + " states...");
		Model.bulkSave(statesMap.values());

		System.out.println("\t" + mesosMap.size() + " mesoregions...");
		Model.bulkSave(mesosMap.values());

		System.out.println("\t" + microsMap.size() + " microregions...");
		Model.bulkSave(microsMap.values());

		System.out.println("\t" + townsMap.size() + " towns...");
		Model.bulkSave(townsMap.values());

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

		locationParser.save();
	}
}
