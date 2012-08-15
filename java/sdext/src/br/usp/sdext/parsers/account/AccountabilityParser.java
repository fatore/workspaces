package br.usp.sdext.parsers.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.candidature.Donor;
import br.usp.sdext.models.candidature.Expense;
import br.usp.sdext.models.candidature.Income;
import br.usp.sdext.models.candidature.Provider;
import br.usp.sdext.models.ghosts.GhostCandidate;
import br.usp.sdext.models.old.Election;
import br.usp.sdext.models.old.Log;
import br.usp.sdext.models.old.State;
import br.usp.sdext.models.old.Town;
import br.usp.sdext.parsers.AbstractParser;
import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.util.Misc;


public class AccountabilityParser extends AbstractParser {

	private MiscParser miscParser;

	private HashMap<AccountabilityBinding, Model> bindings;

	private DonorParser donorParser;
	private IncomeParser incomeParser;
	private ProviderParser providerParser;
	private ExpenseParser expenseParser;
	
	private ArrayList<Model> ghosts = new ArrayList<>();
	private ArrayList<Model> logs = new ArrayList<>();

	private int action;

	private boolean csv;

	public AccountabilityParser(MiscParser miscParser, HashMap<AccountabilityBinding, Model> bidnings) {

		this.miscParser = miscParser;

		this.bindings = bidnings;

		donorParser = new DonorParser(miscParser);
		incomeParser = new IncomeParser();
		providerParser = new ProviderParser();
		expenseParser = new ExpenseParser();
	}

	protected void loadFile(File file) throws Exception {	

		if (file.getName().matches(".*(?iu)csv")) {	
			csv = true;
		}
		if (file.getName().matches(".*(?iu)txt")) {
			csv = false;
		}

		miscParser.setYear(Integer.parseInt(file.getParentFile().getParentFile().
				getParentFile().getName()));

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
					((action == 0) ? "incomes" : "expenses") + " of year " + miscParser.getYear() + ".");

		} else {

			System.out.println("Parsing candidates " + 
					((action == 0) ? "incomes" : "expenses") + " of year " 
					+ miscParser.getYear() +  " for " + file.getParentFile().getName() + ".");
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
				logs.add(log);
			}
		}
		if (in != null) {
			in.close();
		}
	}

	private void parseIncomeLine(String line) throws Exception {

		// Break line where finds ";"
		String pieces[] = line.split("\";\"");	

		Donor donor = (Donor) donorParser.parse(pieces, miscParser.getYear());
		Income income = (Income) incomeParser.parse(pieces, miscParser.getYear(), donor);

		// Create binding
		String post = null;
		State electionState = null;
		Town electionTown = null;

		String candidateName = null;
		Integer ballotNo = null;

		switch (miscParser.getYear()) {

		case 2006:
			
			post = Misc.parseStr(pieces[1]);
			
			candidateName = Misc.parseStr(pieces[0]);
			
			ballotNo = Misc.parseInt(pieces[3]);
			
			electionState = new State(Misc.parseStr(pieces[4]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			break;

		case 2008:
			
			post = Misc.parseStr(pieces[2]);
			
			candidateName = Misc.parseStr(pieces[0]);
			
			ballotNo = Misc.parseInt(pieces[4]);
			
			electionState = new State(Misc.parseStr(pieces[5]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			electionTown = new Town(null, Misc.parseStr(pieces[6]));
			electionTown.setState(electionState);
			electionTown = (Town) Town.fetch(electionTown, miscParser.getTownsMap());
			
			break;

		case 2010:
			
			post = Misc.parseStr(pieces[4]);
			
			candidateName = Misc.parseStr(pieces[5]);
			
			ballotNo = Misc.parseInt(pieces[3]);
			
			electionState = new State(Misc.parseStr(pieces[1]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			break;

		default:
			break;
		}

		Election election = new Election();
		election.setPost(post);
		election.setState(electionState);
		election.setTown(electionTown);
		election.setYear(miscParser.getYear());

		Candidate candidate = new Candidate();
		candidate.setName(candidateName);

		Candidature candidature = new Candidature();
		candidature.setElection(election);
		candidature.setCandidate(candidate);
		candidature.setBallotNo(ballotNo);

		AccountabilityBinding binding = new AccountabilityBinding(candidature);

		Candidature mappedCandidature = (Candidature) bindings.get(binding);

		if (mappedCandidature == null) {

			ghosts.add(new GhostCandidate(candidateName, ballotNo, income, null));

		} else {
			mappedCandidature.addIncome(income);
		}
	}

	private void parseExpenseLine(String line) throws Exception {

		// Break line where finds ";"
		String pieces[] = line.split("\";\"");	

		Provider provider = (Provider) providerParser.parse(pieces, miscParser.getYear());

		Expense expense = (Expense) expenseParser.parse(pieces, miscParser.getYear(), provider);

		// Create binding
		String post = null;
		State electionState = null;
		Town electionTown = null;

		String candidateName = null;
		Integer ballotNo = null;

		switch (miscParser.getYear()) {

		case 2006:
			
			post = Misc.parseStr(pieces[1]);
			
			candidateName = Misc.parseStr(pieces[0]);
			
			ballotNo = Misc.parseInt(pieces[3]);
			
			electionState = new State(Misc.parseStr(pieces[4]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			break;

		case 2008:
			
			post = Misc.parseStr(pieces[2]);
			
			candidateName = Misc.parseStr(pieces[0]);
			
			ballotNo = Misc.parseInt(pieces[4]);
			
			electionState = new State(Misc.parseStr(pieces[5]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			electionTown = new Town(null, Misc.parseStr(pieces[6]));
			electionTown.setState(electionState);
			electionTown = (Town) Town.fetch(electionTown, miscParser.getTownsMap());
			
			break;

		case 2010:
			
			post = Misc.parseStr(pieces[4]);
			
			candidateName = Misc.parseStr(pieces[5]);
			
			ballotNo = Misc.parseInt(pieces[3]);
			
			electionState = new State(Misc.parseStr(pieces[1]));
			electionState = (State) State.fetch(electionState, miscParser.getStatesMap());
			
			break;

		default:
			break;
		}

		Election election = new Election();
		election.setPost(post);
		election.setState(electionState);
		election.setTown(electionTown);
		election.setYear(miscParser.getYear());

		Candidate candidate = new Candidate();
		candidate.setName(candidateName);

		Candidature candidature = new Candidature();
		candidature.setElection(election);
		candidature.setCandidate(candidate);
		candidature.setBallotNo(ballotNo);

		AccountabilityBinding binding = new AccountabilityBinding(candidature);
		
		Candidature mappedCandidature = (Candidature) bindings.get(binding);

		if (mappedCandidature == null) {

			ghosts.add(new GhostCandidate(candidateName, ballotNo, null, expense));
			
		} else {
			mappedCandidature.addExpense(expense);
		}
	}

	public void save() {

		long start = System.currentTimeMillis();

		System.out.println("\nSaving objects in the database, " +
				"this can take several minutes.");

		donorParser.save();
		incomeParser.save();
		providerParser.save();
		expenseParser.save();
		
		Model.bulkSave(ghosts);
		Model.bulkSave(logs);
		
		long elapsedTime = System.currentTimeMillis() - start;

		System.out.printf("Finished saving after %d mins and %d secs\n",
				+  (int) (elapsedTime / 60000),(int) (elapsedTime % 60000) / 1000);
	}

	protected void printResults() {
		
		System.out.println("\nTotal objects loaded");
		System.out.println("\tDonors: " + donorParser.getDonorMap().size());
		System.out.println("\tIncomes: " + incomeParser.getIncomesMap().size());
		System.out.println("\tProviders: " + providerParser.getProviderMap().size());
		System.out.println("\tExpenses: " + expenseParser.getExpenseMap().size());
		System.out.println("\tLog Entries: " + logs.size());
		System.out.println("\tInexistent Candidates: " + ghosts.size());
	}

}




