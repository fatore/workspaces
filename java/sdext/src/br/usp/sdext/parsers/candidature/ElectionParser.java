package br.usp.sdext.parsers.candidature;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class ElectionParser extends ModelParser {

	private MiscParser miscParser;
	
	private HashMap<Model, Model> electionsMap = new HashMap<>();
	
	public ElectionParser(MiscParser miscParser) {
		
		this.miscParser = miscParser;
	}
	
	public HashMap<Model, Model> getElectionsMap() {return electionsMap;}
	
	public Model parse(String[] pieces) throws Exception {
		
		Integer year = Misc.parseInt(pieces[2]); // year
		if (year == null) {throw new Exception("Election year is invalid: " + pieces[2]);}
		
		Integer round = Misc.parseInt(pieces[3]); // round 
		if (round == null) {throw new Exception("Election round is invalid: " + pieces[3]);}
		
		Long postID = Misc.parseLong(pieces[8]); // posID
		if (postID == null) {throw new Exception("Election post id is invalid: " + pieces[8]);}
		
		String postLabel = Misc.parseStr(pieces[9]); // post
		if (postLabel == null) {throw new Exception("Election post label is invalid: " + pieces[9]);}
		
		String description = Misc.parseStr(pieces[4]); // description
		
		Election election = new Election(year, round, postID, postLabel, description);
		
		String stateLabel = Misc.parseStr(pieces[5]); // label
		if (stateLabel == null) {throw new Exception("State label is invalid: " + pieces[5]);}
		State electionState =  new State(stateLabel);
		electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
		election.setState(electionState);

		if ((election.getYear() - Election.FIRST_MAIN_ELECTION) % 4 != 0) {
			
			String townLabel = Misc.parseStr(pieces[7]);
			if (townLabel == null) {throw new Exception("Town label is invalid: " + pieces[7]);}

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
