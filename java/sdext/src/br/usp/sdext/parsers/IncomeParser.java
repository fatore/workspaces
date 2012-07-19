package br.usp.sdext.parsers;

import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidature.Donor;
import br.usp.sdext.models.candidature.Income;
import br.usp.sdext.util.Misc;

public class IncomeParser extends ModelParser {

	private HashMap<Model, Model> incomesMap = new HashMap<>();

	public HashMap<Model, Model> getIncomesMap() {return incomesMap;}

	public Model parse(String[] pieces, int year, Donor donor) throws Exception {

		// Parse income.
		Date date = null;
		Float value = null;
		String type = null;

		switch (year) {

		case 2006:
			date = Misc.parseDate(pieces[10]);
			value = Misc.parseFloat(pieces[9]);
			type = Misc.parseStr(pieces[11]);
			break;

		case 2008:
			date = Misc.parseDate(pieces[14]);
			value = Misc.parseFloat(pieces[13]);
			type = Misc.parseStr(pieces[15]);
			break;

		case 2010:
			date = Misc.parseDate(pieces[12]);
			value = Misc.parseFloat(pieces[13]);
			type = Misc.parseStr(pieces[14]);
			break;

		default:
			break;
		}

		Income income = new Income(value, type, date);

		// Set income donor's.
		income.setDonor(donor);

		income = (Income) Model.fetch(income, incomesMap);
		
		return income;
	}

	@Override
	public void save() {

		System.out.println("\tSaving incomes...");
		Model.bulkSave(incomesMap.values());

	}
}
