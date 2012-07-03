package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Donor;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.candidature.Income;


public class AccountabilityParser extends AbstractParser {
	
	private Map<Model, Model> donors;
	private ArrayList<Model> incomes;
	
	private ArrayList<Binding> bindings;
	
	private boolean old;
	
	public AccountabilityParser() {
		
		donors = new HashMap<>();
		incomes = new ArrayList<>();
		bindings = new ArrayList<>();
	}
	
	protected void loadFile(File file) throws Exception {	
		
		if (file.getName().matches(".*(?iu)csv")) {	
			old = true;
		}
		if (file.getName().matches(".*(?iu)txt")) {
			old = false;
		}
		parseFile(file);
	}
	
	private void parseFile(File file) throws Exception {
		
		int year = Integer.parseInt(file.getParentFile().getParentFile().
				getParentFile().getName());
		
		if (old) {
			
			if (file.getParentFile().getParentFile().getName().matches("(?iu)candidato")) {
				
				if (file.getParentFile().getName().matches("(?iu)receita")) {
					parseIncome(file, year);
				}  
				if (file.getParentFile().getName().matches("(?iu)despesa")) {
					parseExpense(file, year);
				} 
			}
		} 
		else {
			
			if (file.getParentFile().getParentFile().getName().matches("(?iu)candidato")) {

				if (file.getName().matches("(?iu)receitas(.)*(\\.(?i)txt)")) {
					parseIncome(file, year);
				}  
				if (file.getName().matches("(?iu)despesas(.)*(\\.(?i)txt)")) {
					parseExpense(file, year);
				} 
			}
		}
	}
	
	private void parseIncome(File file, int year) throws Exception {
		
		BufferedReader in;
		
		if (old) {
			
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "ISO-8859-1"));
		} 
		else {
			
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		}
		
		String line = null;
		
		if (old) {
			
			System.out.println("Parsing candidates incomes of year " + year + ".");
			
		} else {
			
			System.out.println("Parsing candidates incomes of year " + year + 
					" for " + file.getParentFile().getName() + ".");
		}
		
		// ignore first line
		line = in.readLine();
		
		while ((line = in.readLine()) != null) {
			
			// Break line where finds ";"
			String pieces[] = line.split("\";\"");	
			
			///////////////////////////////////////////
			// PARSING DONORS  /////////////////////
			//////////////////////////////////////////
			
			// Parse data.
			Donor donor = Donor.parse(pieces, old);
			
			// Look for the object in map ...
			Donor mapDonor = (Donor) donors.get(donor);
			
			// ... if didn't find anything.
			if (mapDonor == null) {
				
				// Set the ID for the new object ...
				donor.setId(new Long(donors.size()));
				
				// ... and put it in the map.
				donors.put(donor, donor);
			} 
			// If found ... 
			else {
				// ...  set as the mapped object.
				donor = mapDonor;
			}
			
			///////////////////////////////////////////
			// PARSING INCOMES  /////////////////////
			//////////////////////////////////////////
			
			// Parse data.
			Income income = Income.parse(pieces, old);

			// Set income donor's.
			income.setDonor(donor);
			
			// Set the ID for the new object ...
			income.setId(new Long(incomes.size()));
			
			// Add to the list.
			incomes.add(income);
			
			// Create binding
			Binding binding = new Binding(income, pieces, year, old);
			bindings.add(binding);
		}
	}
	
	private void parseExpense(File file, int year) throws Exception {}
	
	protected void save() {
		
		System.out.println("\nTotal objects loaded");
		System.out.println("\tDonors: " + donors.size());
		System.out.println("\tIncomes: " + incomes.size());
		
		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");
		
		System.out.println("\tSaving donors...");
		Model.bulkSave(donors.values());
		
		System.out.println("\tSaving incomes...");
		Model.bulkSave(incomes);
		
		System.out.println("\tBinding objects...");
		for (Binding binding : bindings) {
			
			Candidature.addIncome(binding);
		}
	}
}




