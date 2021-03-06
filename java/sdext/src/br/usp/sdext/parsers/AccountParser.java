package br.usp.sdext.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import br.usp.sdext.core.Log;
import br.usp.sdext.core.Model;
import br.usp.sdext.models.account.Donor;
import br.usp.sdext.models.account.Expense;
import br.usp.sdext.models.account.GhostCandidate;
import br.usp.sdext.models.account.Income;
import br.usp.sdext.models.account.Provider;
import br.usp.sdext.models.candidate.status.Status;
import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.election.Election;
import br.usp.sdext.models.election.Post;
import br.usp.sdext.models.location.State;
import br.usp.sdext.models.location.Town;
import br.usp.sdext.parsers.bindings.AccountBinding;
import br.usp.sdext.util.Misc;
import br.usp.sdext.util.ParseException;

public class AccountParser extends AbstractParser {

	private LocationParser locationParser;
	private CandidateParser candidateParser;
	private ElectionParser electionParser;
	
	private DonorParser donorParser;
	private IncomeParser incomeParser;
	private ProviderParser providerParser;
	private ExpenseParser expenseParser;
	
	private HashMap<AccountBinding, Model> bindings;

	private ArrayList<Model> ghosts = new ArrayList<>();
	private HashSet<Model> logs = new HashSet<>();

	private int action;

	private int year;

	private boolean csv;

	private Long notFound = 0L;

	public AccountParser(LocationParser locationParser, CandidateParser candidateParser, 
			CandidatureParser candidatureParser, ElectionParser electionParser) {

		this.locationParser = locationParser;
		this.candidateParser = candidateParser;
		this.electionParser = electionParser;
		this.donorParser = new DonorParser(locationParser);
		this.incomeParser = new IncomeParser();
		this.providerParser = new ProviderParser();
		this.expenseParser = new ExpenseParser();
		this.bindings = candidatureParser.getAccountBindings();
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
					+ year+  " for " + file.getParentFile().getName() + ".");
		}

		// ignore first line
		line = in.readLine();

		while ((line = in.readLine()) != null) {

			try {

				// Break line where finds ";"
				String pieces[] = line.split("\";\"");

				if (action == 0) {
					parseIncomeLine(pieces);
				} else {
					parseExpenseLine(pieces);
				}

			} catch (ParseException e) {

				String exceptionClass = null;
				String exceptionMethod = null;

				for (StackTraceElement element : e.getStackTrace()) {

					if (element.getClassName().contains("br.usp.sdext.parsers")) {

						exceptionClass = element.getClassName();
						exceptionMethod = element.getMethodName();
						break;
					}
				}

				Log log = new Log(line,"CAUSED BY: " + exceptionMethod 
						+ " IN CLASS: " + exceptionClass, e.getMessage(), e.getDetail());
				logs.add(log);
				notFound++;
			}
		}

		if (in != null) {
			in.close();
		}
	}

	private void parseIncomeLine(String[] pieces) throws Exception {

		Donor donor = (Donor) donorParser.parse(pieces, year);
		Income income = (Income) incomeParser.parse(pieces, year, donor);

		// Create binding
		String postLabel = null;
		String stateLabel = null;
		String townLabel = null;

		String name = null;
		Integer ballotNo = null;

		switch (year) {

		case 2006:

			postLabel = Misc.parseStr(pieces[1]);

			name = Misc.parseStr(pieces[0]);

			ballotNo = Misc.parseInt(pieces[3]);

			stateLabel = Misc.parseStr(pieces[4]);

			break;

		case 2008:

			postLabel = Misc.parseStr(pieces[2]);

			name = Misc.parseStr(pieces[0]);

			ballotNo = Misc.parseInt(pieces[4]);

			stateLabel = Misc.parseStr(pieces[5]);

			townLabel = Misc.parseStr(pieces[6]);

			break;

		case 2010:

			postLabel = Misc.parseStr(pieces[4]);

			name = Misc.parseStr(pieces[5]);

			ballotNo = Misc.parseInt(pieces[3]);

			stateLabel = Misc.parseStr(pieces[1]);

			break;

		default:

			break;
		}

		State parsedState = new State(stateLabel);

		State mappedState = (State) locationParser.getStatesMap().get(parsedState);

		if (mappedState == null) {

			throw new ParseException("Election state not found in map" , parsedState.getAcronym());
		}

		Town mappedTown = null;

		if ((year - Election.FIRST_MAIN_ELECTION) % 4 != 0) {

			String townNamex = locationParser.parseTownNamex(townLabel);

			Town parsedTown = new Town(townNamex, mappedState);

			mappedTown = (Town) locationParser.getTownsMap().get(parsedTown);

			if (mappedTown == null) {

				// Check if town is not lost.
				if (!locationParser.getLostTownsMap().containsKey(parsedTown)) {

					// Try to disambiguate.
					mappedTown = locationParser.disambiguateTown(parsedTown);

					// If all failed then town is lost.
					if (mappedTown == null) {

						throw new ParseException("Election town not found", parsedTown.toString());
					}
				}
				else {

					mappedTown = (Town) locationParser.getLostTownsMap().get(parsedTown);
				}
			}
		} 

		Post parsedPost = new Post(postLabel);

		Post mappedPost = (Post) electionParser.getPostsMap().get(parsedPost);

		if (mappedPost == null) {

			throw new ParseException("Election post not found", pieces[8]);
		}

		Election parsedElection = new Election(year, mappedState, mappedTown, mappedPost);

		Election mappedElection = (Election) electionParser.getElectionsMap().get(parsedElection);

		if (mappedElection == null) {

			throw new ParseException("Election not found", parsedElection.toString());
		}

		AccountBinding binding = new AccountBinding(ballotNo, mappedElection);

		Candidature mappedCandidature = (Candidature) bindings.get(binding);

		if (mappedCandidature == null) {

			ghosts.add(new GhostCandidate(name, ballotNo, income, null));

		} else {

			Status parsedStatus = new Status(year, mappedCandidature.getCandidate());

			Status mappedStatus = (Status) candidateParser.getStatusMap().get(parsedStatus);

			if (mappedStatus == null) {

				throw new ParseException("Candidate status not found", parsedStatus.toString());
			}

			mappedStatus.addIncome(income);
		}
	}

	private void parseExpenseLine(String[] pieces) throws Exception {

		Provider provider = (Provider) providerParser.parse(pieces, year);

		Expense expense = (Expense) expenseParser.parse(pieces, year, provider);

		// Create binding
		String postLabel = null;
		String stateLabel = null;
		String townLabel = null;

		String name = null;
		Integer ballotNo = null;

		switch (year) {

		case 2006:

			postLabel = Misc.parseStr(pieces[1]);

			name = Misc.parseStr(pieces[0]);

			ballotNo = Misc.parseInt(pieces[3]);

			stateLabel = Misc.parseStr(pieces[4]);

			break;

		case 2008:

			postLabel = Misc.parseStr(pieces[2]);

			name = Misc.parseStr(pieces[0]);

			ballotNo = Misc.parseInt(pieces[4]);

			stateLabel = Misc.parseStr(pieces[5]);

			townLabel = Misc.parseStr(pieces[6]);

			break;

		case 2010:

			postLabel = Misc.parseStr(pieces[4]);

			name = Misc.parseStr(pieces[5]);

			ballotNo = Misc.parseInt(pieces[3]);

			stateLabel = Misc.parseStr(pieces[1]);

			break;

		default:
			break;
		}

		State parsedState = new State(stateLabel);

		State mappedState = (State) locationParser.getStatesMap().get(parsedState);

		if (mappedState == null) {

			throw new ParseException("Election state not found in map" , parsedState.getAcronym());
		}

		Town mappedTown = null;

		if ((year - Election.FIRST_MAIN_ELECTION) % 4 != 0) {

			String townNamex = locationParser.parseTownNamex(townLabel);

			Town parsedTown = new Town(townNamex, mappedState);

			mappedTown = (Town) locationParser.getTownsMap().get(parsedTown);

			if (mappedTown == null) {

				// Check if town is not lost.
				if (!locationParser.getLostTownsMap().containsKey(parsedTown)) {

					// Try to disambiguate.
					mappedTown = locationParser.disambiguateTown(parsedTown);

					// If all failed then town is lost.
					if (mappedTown == null) {

						throw new ParseException("Election town not found", parsedTown.toString());
					}
				}
				else {

					mappedTown = (Town) locationParser.getLostTownsMap().get(parsedTown);
				}
			}
		}  

		Post parsedPost = new Post(postLabel);

		Post mappedPost = (Post) electionParser.getPostsMap().get(parsedPost);

		if (mappedPost == null) {

			throw new ParseException("Election post not found", pieces[8]);
		}

		Election parsedElection = new Election(year, mappedState, mappedTown, mappedPost);

		Election mappedElection = (Election) electionParser.getElectionsMap().get(parsedElection);

		if (mappedElection == null) {

			throw new ParseException("Election not found", parsedElection.toString());
		}

		AccountBinding binding = new AccountBinding(ballotNo, mappedElection);

		Candidature mappedCandidature = (Candidature) bindings.get(binding);

		if (mappedCandidature == null) {

			ghosts.add(new GhostCandidate(name, ballotNo, null, expense));

		} else {

			Status parsedStatus = new Status(year, mappedCandidature.getCandidate());

			Status mappedStatus = (Status) candidateParser.getStatusMap().get(parsedStatus);

			if (mappedStatus == null) {

				throw new ParseException("Candidate status not found", parsedStatus.toString());
			}

			mappedStatus.addExpense(expense);
		}
	}

	public void save() {

		System.out.println("Saving transactions data...");
		System.out.println("\t" + ghosts.size() + " transactions for unknown candidates.");
		
		Model.bulkSave(logs);
		
		donorParser.save();
		incomeParser.save();
		providerParser.save();
		expenseParser.save();

		Model.bulkSave(ghosts);
		
		bindings.clear();
	}

	public static void main(String[] args) throws Exception {

		LocationParser locationParser = new LocationParser();

		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/reg.csv", "region");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/uf.csv", "state");
		locationParser.addSpecialValues();
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/meso.csv", "meso");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/micro.csv", "micro");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/mun.csv", "town");

		ElectionParser electionParser = new ElectionParser(locationParser);

		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2012");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2010");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2008");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2006");
		electionParser.addSpecialValues();

		CandidateParser candidateParser = new CandidateParser(locationParser);

		CandidatureParser candidatureParser = new CandidatureParser(locationParser, electionParser, 
				candidateParser);

		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2006");

		EstateParser estateParser = new EstateParser(locationParser, candidateParser, candidatureParser);

		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2006");

		AccountParser accountParser = new AccountParser(locationParser, candidateParser,
				candidatureParser, electionParser);

		accountParser.parse("/home/fm/work/data/sdext/eleitorais/prestacao_contas/2006/candidato");

		locationParser.save();
		electionParser.save();
		accountParser.save();
		estateParser.save();
		candidateParser.save();
		candidatureParser.save();
	}
}




