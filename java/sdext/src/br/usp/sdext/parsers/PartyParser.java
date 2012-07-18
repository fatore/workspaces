package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Party;

public class PartyParser extends ModelParser {

	private HashMap<Model, Model> partiesMap = new HashMap<>();

	public HashMap<Model, Model> getPartiesMap() {return partiesMap;}
	
	@Override
	public Model parse(String[] pieces) throws Exception {
		
		Party party =  Party.parse(pieces);
		party = (Party) Model.fetch(party, partiesMap);
		return party;
	}

	@Override
	public void save() {

		System.out.println("\tSaving parties...");
		Model.bulkSave(partiesMap.values());
	}
}
