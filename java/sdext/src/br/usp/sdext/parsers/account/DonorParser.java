package br.usp.sdext.parsers.account;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidature.Donor;
import br.usp.sdext.models.location.State;
import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class DonorParser extends ModelParser {

	private MiscParser miscParser;

	private HashMap<Model, Model> donorsMap = new HashMap<>();

	public DonorParser(MiscParser miscParser) {

		this.miscParser = miscParser;
	}

	public HashMap<Model, Model> getDonorMap() {return donorsMap;}

	public Model parse(String[] pieces, int year) throws Exception {

		// Parse donor.
		Long donorCPF = null;
		String donorUF = null;
		String donorName = null;

		switch (year) {

		case 2006:
			donorCPF = Misc.parseLong(pieces[16]);
			donorUF = null;
			donorName = Misc.parseStr(pieces[15]);
			break;

		case 2008:
			donorCPF = Misc.parseLong(pieces[20]);
			donorUF = Misc.parseStr(pieces[21]);
			donorName = Misc.parseStr(pieces[19]);
			break;

		case 2010:
			donorCPF = Misc.parseLong(pieces[10]);
			donorUF = Misc.parseStr(pieces[1]);
			donorName = Misc.parseStr(pieces[11]);
			break;

		default:
			break;
		}

		Donor donor = new Donor(donorName, donorCPF);

		State donorState = new State(donorUF);
		donorState = (State) State.fetch(donorState, miscParser.getStatesMap());
		donor.setState(donorState);

		donor = (Donor) Model.fetch(donor, donorsMap);
		
		return donor;
	}

	@Override
	public void save() {

		System.out.println("\tSaving donors...");
		Model.bulkSave(donorsMap.values());

	}
}
