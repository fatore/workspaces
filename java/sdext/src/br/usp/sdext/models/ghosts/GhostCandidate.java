package br.usp.sdext.models.ghosts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidature.Expense;
import br.usp.sdext.models.candidature.Income;

@Entity
@Table(name="ghost_candidate")
public class GhostCandidate extends Model {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	private Integer ballotNo;
	
	@OneToOne
	private Income income;
	
	@OneToOne
	private Expense expense;

	public GhostCandidate() {}
	
	public GhostCandidate(String name, Integer ballotNo, Income income, Expense expense) {
		
		this.name = name;
		this.ballotNo = ballotNo;
		this.income = income;
		this.expense = expense;
	}
	
	public Long getId() {return id;}
	public String getName() {return name;}
	public Integer getBallotNo() {return ballotNo;}
	public Income getIncome() {return income;}
	public Expense getExpense() {return expense;}
	
	@Override
	public void setId(Long id) {}
}
