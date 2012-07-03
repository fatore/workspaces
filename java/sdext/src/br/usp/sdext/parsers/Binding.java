package br.usp.sdext.parsers;

import br.usp.sdext.models.candidature.Income;

public class Binding {

	public Income income;
	public String[] pieces;
	public Integer year;
	public boolean old;
	
	public Binding(Income income, String[] pieces, Integer year, boolean old) {
		
		this.income = income;
		this.pieces = pieces;
		this.year = year;
		this.old = old;
	}
}
