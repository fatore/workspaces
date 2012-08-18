package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.util.Misc;

public class CoalitionParser {

	private HashMap<Model, Model> coalitionsMap = new HashMap<>();
	
	public HashMap<Model, Model> getCoalitionsMap() {
		return coalitionsMap;
	}

	public Model parse(String[] pieces) throws Exception {
		
		Coalition coalition =  new Coalition(
				Misc.parseLong(pieces[19]), // code
				Misc.parseStr(pieces[20]), // acronym
				Misc.parseStr(pieces[22]), // name
				Misc.parseStr(pieces[21])); // composition
		
		coalition = (Coalition) Model.persist(coalition, coalitionsMap);
		
		return coalition;
	}

	public void save() {

		System.out.print("Saving " + coalitionsMap.size() + " coalitions...");
		Model.bulkSave(coalitionsMap.values());
		System.out.println(" Done!");
	}
}
