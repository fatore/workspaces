package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.State;
import br.usp.sdext.models.Town;
import br.usp.sdext.util.Misc;

public class ElectionParser extends ModelParser {

	private MiscParser miscParser;
	
	private HashMap<Model, Model> electionsMap = new HashMap<>();
	
	public ElectionParser(MiscParser miscParser) {
		
		this.miscParser = miscParser;
	}
	
	public HashMap<Model, Model> getElectionsMap() {return electionsMap;}
	
	@Override
	public Model parse(String[] pieces) throws Exception {
		
		Election election = Election.parse(pieces);
		
		State electionState = State.parse(pieces);
		electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
		
		election.setState(electionState);

		if ((election.getYear() - Election.FIRST_MAIN_ELECTION) % 4 != 0) {
			
			String townLabel = Misc.parseStr(pieces[7]);
			
			if (townLabel == null) {
				throw new Exception("Town label is invalid: " + pieces[7]);
			}

			Town electionTown = new Town(null, Misc.parseStr(pieces[7]));
			Long ueId = Misc.parseLong(pieces[6]);
			electionTown.setState(electionState);
			
			electionTown = (Town) Town.fetch(electionTown, miscParser.getTownsMap());

			if (electionTown.getUeId() == null) {
				electionTown.setUeId(ueId);
			} else {
				if (!electionTown.getUeId().equals(ueId)) {
					System.err.println("Same city different ueIds.");
				}
			}
			election.setTown(electionTown);
		} 
		else {

			election.setTown(null);
		}
		
		election = (Election) Model.fetch(election, electionsMap);
		
		return election;
	}

	@Override
	public void save() {
		
		System.out.println("\tSaving elections...");
		Model.bulkSave(electionsMap.values());
		
	}
}
