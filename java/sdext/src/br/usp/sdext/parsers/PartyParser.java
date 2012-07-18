package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Party;
import br.usp.sdext.util.Misc;

public class PartyParser extends ModelParser {

	private HashMap<Model, Model> partiesMap = new HashMap<>();

	public HashMap<Model, Model> getPartiesMap() {return partiesMap;}
	
	@Override
	public Model parse(String[] pieces) throws Exception {
		
		Party party =  new Party(
				Misc.parseInt(pieces[16]), // no 
				Misc.parseStr(pieces[17]),  // acronym
				Misc.parseStr(pieces[18])); // name
		
		party = (Party) Model.fetch(party, partiesMap);
		
		return party;
	}

	@Override
	public void save() {

		System.out.println("\tSaving parties...");
		Model.bulkSave(partiesMap.values());
	}
}
