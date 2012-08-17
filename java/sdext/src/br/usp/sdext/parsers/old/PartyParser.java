package br.usp.sdext.parsers.old;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Party;
import br.usp.sdext.util.Misc;

public class PartyParser extends ModelParser {

	private HashMap<Model, Model> partiesMap = new HashMap<>();

	public HashMap<Model, Model> getPartiesMap() {return partiesMap;}
	
	public Model parse(String[] pieces) throws Exception {
		
		Integer no = Misc.parseInt(pieces[16]); // no 
		String acronym = Misc.parseStr(pieces[17]);  // acronym
		String name = Misc.parseStr(pieces[18]); // name
		if (no == null) {throw new Exception("Party number is invalid: " + pieces[16]);}
		Party party = new Party(no, acronym, name);
		party = (Party) Model.persist(party, partiesMap);
		
		return party;
	}

	@Override
	public void save() {

		System.out.println("\tSaving parties...");
		Model.bulkSave(partiesMap.values());
	}
}
