package br.usp.sdext.parsers.account;

import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidature.Expense;
import br.usp.sdext.models.candidature.Provider;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class ExpenseParser extends ModelParser {

	private HashMap<Model, Model> expenseMap = new HashMap<>();

	public HashMap<Model, Model> getExpenseMap() {return expenseMap;}

	public Model parse(String[] pieces, int year, Provider provider) throws Exception {

		// Parse expense.
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

		Expense expense = new Expense(value, type, date);
		
		// Set expense provider.
		expense.setProvider(provider);

		expense = (Expense) Model.fetch(expense, expenseMap);
		
		return expense;
	}

	@Override
	public void save() {

		System.out.println("\tSaving expenses...");
		Model.bulkSave(expenseMap.values());

	}
}
