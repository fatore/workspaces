package br.usp.sdext.parsers;

import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.account.Expense;
import br.usp.sdext.models.account.Provider;
import br.usp.sdext.util.Misc;

public class ExpenseParser {

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

		Expense parsedExpense = new Expense(value, type, date);
		
		// Set expense provider.
		parsedExpense.setProvider(provider);

		Expense mappedExpense = (Expense) Model.persist(parsedExpense, expenseMap);
		
		return mappedExpense;
	}

	public void save() {

		System.out.print("\tSaving " + expenseMap.size() + " expenses...");
		Model.bulkSave(expenseMap.values());
		System.out.println(" Done!");
	}
}
