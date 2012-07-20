package br.usp.sdext.parsers.candidature;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class CoalitionParser extends ModelParser {

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
		
		coalition = (Coalition) Model.fetch(coalition, coalitionsMap);
		
		return coalition;
	}

	@Override
	public void save() {

		System.out.println("\tSaving coalitions...");
		Model.bulkSave(coalitionsMap.values());
	}
}
