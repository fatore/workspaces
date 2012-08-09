package br.usp.sdext.parsers.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.location.Region;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class LocationParser extends ModelParser {

	private MiscParser miscParser;
	
	private HashMap<Model, Model> regionsMap = new HashMap<>();
	
	public LocationParser(MiscParser miscParser) {
		
		this.miscParser = miscParser;
	}
	
	public HashMap<Model, Model> getRegionsMap() {return regionsMap;}
	
	public void parseRegion(String filename) throws Exception {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(filename))));
		
		String line = null;

		while ((line = in.readLine()) != null) {
			
			// Break line where finds ";"
			String pieces[] = line.split(",");

			// remove double quotes
			for (int i = 0; i < pieces.length; i++) {
				pieces[i] = pieces[i].replace("\"", "");
			}
			
			Long id = Long.parseLong(pieces[0]);
			
			String name = pieces[1];
			String namex = pieces[2];
			
			String acronym = pieces[3];
			String acronymx = pieces[4];
			
			String status = pieces[5];
			
			Region region = new Region(id, name, namex, acronym, acronymx, status);
			
			region = (Region) Model.fetch(region, regionsMap);
		}
		
		if (in != null) {
			in.close();
		}
	}

	@Override
	public void save() {
		
		System.out.println("\tSaving regions...");
		Model.bulkSave(regionsMap.values());
	}
	
	public static void main(String[] args) throws Exception {
		
		LocationParser locationParser = new LocationParser(new MiscParser());
		
		locationParser.parseRegion("/home/fm/work/data/sdext/datasus/ut/reg.csv");
		
		locationParser.save();
	}
}
