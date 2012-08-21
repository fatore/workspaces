package br.usp.sdext.parsers;

import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.account.Donor;
import br.usp.sdext.models.account.Income;
import br.usp.sdext.util.Misc;

public class IncomeParser {

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

		income = (Income) Model.persist(income, incomesMap);
		
		return income;
	}

	public void save() {

		System.out.print("\tSaving incomes...");
		Model.bulkSave(incomesMap.values());
		System.out.println(" Done!");
	}
}
