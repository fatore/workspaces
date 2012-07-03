package br.usp.sdext.models.ghosts;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.usp.sdext.models.candidature.Income;

@Entity
@Table(name="ghost_candidate")
public class GhostCandidate {
	
	@Id
	@GeneratedValue
	Long id;
	String name;
	Integer ballotNo;
	@OneToOne
	Income income;

	public GhostCandidate(String name, Integer ballotNo, Income income) {
		this.name = name;
		this.ballotNo = ballotNo;
		this.income = income;
	}
	
	public GhostCandidate() {}
}
