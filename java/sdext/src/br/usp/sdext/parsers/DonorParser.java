package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.account.Donor;
import br.usp.sdext.models.location.State;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class DonorParser  {

	private HashMap<Model, Model> donorsMap = new HashMap<>();

	private LocationParser locationParser;

	public DonorParser(LocationParser locationParser) {

		this.locationParser = locationParser;
	}

	public HashMap<Model, Model> getDonorMap() {return donorsMap;}

	public Model parse(String[] pieces, int year) throws Exception {

		// Parse donor.
		Long donorCPF = null;
		String stateLabel = null;
		String donorName = null;

		switch (year) {

		case 2006:
			donorCPF = Misc.parseLong(pieces[16]);
			stateLabel = null;
			donorName = Misc.parseStr(pieces[15]);
			break;

		case 2008:
			donorCPF = Misc.parseLong(pieces[20]);
			stateLabel = Misc.parseStr(pieces[21]);
			donorName = Misc.parseStr(pieces[19]);
			break;

		case 2010:
			donorCPF = Misc.parseLong(pieces[10]);
			stateLabel = Misc.parseStr(pieces[1]);
			donorName = Misc.parseStr(pieces[11]);
			break;

		default:
			break;
		}

		Donor donor = new Donor(donorName, donorCPF);

		State mappedState = null;

		if (stateLabel != null) {
			
			State parsedState = new State(stateLabel);

			mappedState = (State) locationParser.getStatesMap().get(parsedState);

			if (mappedState == null) {

				throw new ParseException("Election state not found in map" , parsedState.getAcronym());
			}
		}

		donor.setState(mappedState);

		donor = (Donor) Model.persist(donor, donorsMap);

		return donor;
	}

	public void save() {

		System.out.print("\tSaving " + donorsMap.size() + " donors...");
		Model.bulkSave(donorsMap.values());
		System.out.println(" Done!");
	}
}
