package br.usp.sdext.parsers;

import br.usp.sdext.models.candidature.Expense;
import br.usp.sdext.util.Misc;

public class ExpenseBinding extends Binding {

	private Expense expense;
	
	public ExpenseBinding(Expense expense, String[] pieces, Integer year) {

		String post = null;
		String candidateName = null;
		Integer ballotNo = null;
		
		switch (year) {
		
		case 2006:
			post = Misc.parseStr(pieces[1]);
			candidateName = Misc.parseStr(pieces[0]);
			ballotNo = Misc.parseInt(pieces[3]);
			break;
		
		case 2008:
			post = Misc.parseStr(pieces[1]);
			candidateName = Misc.parseStr(pieces[0]);
			ballotNo = Misc.parseInt(pieces[3]);
			break;
			
		case 2010:
			post = Misc.parseStr(pieces[4]);
			candidateName = Misc.parseStr(pieces[5]);
			ballotNo = Misc.parseInt(pieces[3]);
			break;

		default:
			break;
		}
		
		this.setPost(post);
		this.setCandidateName(candidateName);
		this.setBallotNo(ballotNo);
		
		this.expense = expense;
	}
	
	public Expense getExpense() {return expense;}
	public void setExpense(Expense expense) {this.expense = expense;}
}
