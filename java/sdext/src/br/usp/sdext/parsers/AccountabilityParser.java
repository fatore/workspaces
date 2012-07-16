package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Log;
import br.usp.sdext.models.State;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.candidature.Donor;
import br.usp.sdext.models.candidature.Expense;
import br.usp.sdext.models.candidature.Income;
import br.usp.sdext.models.candidature.Provider;
import br.usp.sdext.util.Misc;


public class AccountabilityParser extends AbstractParser {

	private HashMap<Model, Model> donorsMap;
	private HashMap<Model, Model> providersMap;
	private ArrayList<Model> incomesList;
	private ArrayList<Model> expensesList;

	private ArrayList<IncomeBinding> incBindingsList;
	private ArrayList<ExpenseBinding> expBindingsList;
	
	private HashMap<Model, Model> statesMap;
	
	private int year;
	private int action;
	private int incompleteEntries = 0;
	
	private int numGhosts;

	private boolean csv;

	public AccountabilityParser() {

		donorsMap = new HashMap<>();
		providersMap = new HashMap<>();
		
		incomesList = new ArrayList<>();
		expensesList = new ArrayList<>();
		
		incBindingsList = new ArrayList<>();
		expBindingsList = new ArrayList<>();
		
		statesMap = Model.findAllMap(State.class);
	}

	protected void loadFile(File file) throws Exception {	

		if (file.getName().matches(".*(?iu)csv")) {	
			csv = true;
		}
		if (file.getName().matches(".*(?iu)txt")) {
			csv = false;
		}

		year = Integer.parseInt(file.getParentFile().getParentFile().
				getParentFile().getName());

		if (csv) {

			if (file.getParentFile().getParentFile().getName().matches("(?iu)candidato")) {

				if (file.getParentFile().getName().matches("(?iu)receita")) {
					action = 0;
					parseFile(file);
				}  
				if (file.getParentFile().getName().matches("(?iu)despesa")) {
					action = 1;
					parseFile(file);
				}
			}
		} 
		else {

			if (file.getParentFile().getParentFile().getName().matches("(?iu)candidato")) {

				if (file.getName().matches("(?iu)receitas(.)*(\\.(?i)txt)")) {
					action = 0;
					parseFile(file);
				}  
				if (file.getName().matches("(?iu)despesas(.)*(\\.(?i)txt)")) {
					action = 1;
					parseFile(file);
				} 
			}
		}
	}

	private void parseFile(File file) throws Exception {

		BufferedReader in;

		if (csv) {

			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "ISO-8859-1"));
		} 
		else {

			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		}

		String line = null;

		if (csv) {

			System.out.println("Parsing candidates " +
			((action == 0) ? "incomes" : "expenses") + " of year " + year + ".");

		} else {

			System.out.println("Parsing candidates " + 
			((action == 0) ? "incomes" : "expenses") + " of year " 
			+ year +  " for " + file.getParentFile().getName() + ".");
		}

		// ignore first line
		line = in.readLine();

		while ((line = in.readLine()) != null) {

			try {
				if (action == 0) {
					parseIncomeLine(line);
				} else {
					parseExpenseLine(line);
				}
			} 
			catch (Exception e) {
				
				String exceptionClass = null;
				String exceptionMethod = null;

				for (StackTraceElement element : e.getStackTrace()) {

					if (element.getClassName().contains("br.usp.sdext.models")) {
						exceptionClass = element.getClassName();
						exceptionMethod = element.getMethodName();
						break;
					}
				}
				Log log = new Log(line,"CAUSED BY: " + exceptionMethod 
						+ " IN CLASS: " + exceptionClass, e.getMessage());
				log.save();
				incompleteEntries++;
			}
		}
		if (in != null) {
			in.close();
		}
	}

	private void parseIncomeLine(String line) throws Exception {

		// Break line where finds ";"
		String pieces[] = line.split("\";\"");	

		// Parse donor.
		Long donorCPF = null;
		String donorUF = null;
		String donorName = null;
		
		switch (year) {
		
		case 2006:
			donorCPF = Misc.parseLong(pieces[16]);
			donorUF = Misc.parseStr(pieces[17]);
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
		donorState = (State) State.fetchAndSave(donorState, statesMap);
		donor.setState(donorState);
		
		donor = (Donor) Model.fetch(donor, donorsMap);

		// Parse income.
		Date date = null;
		Float value = null;
		String type = null;
		
		switch (year) {
		
		case 2006:
			date = Misc.parseDate(pieces[10]);
			value = Misc.parseFloat(pieces[9]);
			try {
				type = Misc.parseStr(pieces[11]);
			} catch (Exception e) {}
			break;
		
		case 2008:
			date = Misc.parseDate(pieces[14]);
			value = Misc.parseFloat(pieces[13]);
			try {
				type = Misc.parseStr(pieces[15]);
			} catch (Exception e) {}
			break;
			
		case 2010:
			date = Misc.parseDate(pieces[12]);
			value = Misc.parseFloat(pieces[13]);
			type = Misc.parseStr(pieces[16]);
			break;
			
		default:
			break;
		}
		
		Income income = new Income(value, type, date);

		// Set income donor's.
		income.setDonor(donor);

		// Set the ID for the new object ...
		income.setId(new Long(incomesList.size()));

		// Add to the list.
		incomesList.add(income);

		// Create binding
		IncomeBinding binding = new IncomeBinding(income, pieces, year);
		incBindingsList.add(binding);
	}
	
	private void parseExpenseLine(String line) throws Exception {

		// Break line where finds ";"
		String pieces[] = line.split("\";\"");	

		// .Parse provider.
		Long providerCPF = null;
		String providerName = null;
		
		switch (year) {
		
		case 2006:
			providerCPF = Misc.parseLong(pieces[19]);
			providerName = Misc.parseStr(pieces[18]);
			break;
		
		case 2008:
			providerCPF = Misc.parseLong(pieces[20]);
			providerName = Misc.parseStr(pieces[19]);
			break;
			
		case 2010:
			providerCPF = Misc.parseLong(pieces[10]);
			providerName = Misc.parseStr(pieces[11]);
			break;
			
		default:
			break;
		}
		
		Provider provider = new Provider(providerName, providerCPF);
		
		provider = (Provider) Model.fetch(provider, providersMap);

		// Parse income.
		Date date = null;
		Float value = null;
		String type = null;
		
		switch (year) {
		
		case 2006:
			date = Misc.parseDate(pieces[10]);
			value = Misc.parseFloat(pieces[9]);
			try {
				type = Misc.parseStr(pieces[11]);
			} catch (Exception e) {}
			break;
		
		case 2008:
			date = Misc.parseDate(pieces[11]);
			value = Misc.parseFloat(pieces[10]);
			try {
				type = Misc.parseStr(pieces[12]);
			} catch (Exception e) {}
			break;
			
		case 2010:
			date = Misc.parseDate(pieces[12]);
			value = Misc.parseFloat(pieces[13]);
			type = Misc.parseStr(pieces[16]);
			break;
			
		default:
			break;
		}
		
		Expense expense = new Expense(value, type, date);

		// Set expense provider.
		expense.setProvider(provider);

		// Set the ID for the new object ...
		expense.setId(new Long(expensesList.size()));

		// Add to the list.
		expensesList.add(expense);

		// Create binding
		ExpenseBinding binding = new ExpenseBinding(expense, pieces, year);
		expBindingsList.add(binding);
	}

	protected void save() {

		long start = System.currentTimeMillis();
		
		System.out.println("\nTotal objects loaded");
		System.out.println("\tDonors: " + donorsMap.size());
		System.out.println("\tIncomes: " + incomesList.size());
		System.out.println("\tProviders: " + providersMap.size());
		System.out.println("\tExpenses: " + expensesList.size());
		System.out.println("\tIncomplete Entries: " + incompleteEntries);

		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");

		System.out.println("\tSaving donors...");
		Model.bulkSave(donorsMap.values());

		System.out.println("\tSaving incomes...");
		Model.bulkSave(incomesList);

		System.out.println("\tBinding objects...");
		IncomeBinding current, next = null; 
		int i;
		for (i = 0; i < incBindingsList.size() - 1; i++) {
			
			ArrayList<IncomeBinding> same = new ArrayList<>();
			
			current = incBindingsList.get(i);
			same.add(current);
			
			while ((i < incBindingsList.size() - 1) &&
					((next = incBindingsList.get(i + 1)).
					hasSameCandidature(current)) ) {
				
				current = next;
				i++;
				same.add(current);
			}
		
			try {
				if (!Candidature.addIncomes(same)) numGhosts++;
			} catch (Exception e) {
				Log log = new Log(same.get(0).toString(), "addIncomes", e.getMessage());
				log.save();
			}
		}
		try {
			if (i < incBindingsList.size()) {
				if (!Candidature.addIncome(next)) numGhosts++;
			}
		} catch (Exception e) {
			Log log = new Log(next.toString(), "addIncomes", e.getMessage());
			log.save();
		}
		
		System.out.println("\tSaving providers...");
		Model.bulkSave(providersMap.values());

		System.out.println("\tSaving expenses...");
		Model.bulkSave(expensesList);

		System.out.println("\tBinding objects...");
		ExpenseBinding ecurrent, enext = null; 
		for (i = 0; i < expBindingsList.size() - 1; i++) {
			
			ArrayList<ExpenseBinding> same = new ArrayList<>();
			
			ecurrent = expBindingsList.get(i);
			same.add(ecurrent);
			
			while ((i < expBindingsList.size() - 1) &&
					((enext = expBindingsList.get(i + 1)).
					hasSameCandidature(ecurrent)) ) {
				
				ecurrent = enext;
				i++;
				same.add(ecurrent);
			}
		
			try {
				if (!Candidature.addExpenses(same)) numGhosts++;
			} catch (Exception e) {
				Log log = new Log(same.get(0).toString(), "addIncomes", e.getMessage());
				log.save();
			}
		}
		
		try {
			if (i < incBindingsList.size()) {
				if (!Candidature.addExpense(enext)) numGhosts++;
			}
		} catch (Exception e) {
			Log log = new Log(next.toString(), "addIncomes", e.getMessage());
			log.save();
		}
		
		System.out.println("\tGhost Candidates: " + numGhosts);
		
		long elapsedTime = System.currentTimeMillis() - start;

		System.out.printf("Finished saving after %d mins and %d secs\n",
				+  (int) (elapsedTime / 60000),(int) (elapsedTime % 60000) / 1000);
	}
	
}




