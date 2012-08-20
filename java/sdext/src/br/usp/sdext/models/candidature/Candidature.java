package br.usp.sdext.models.candidature;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.Session;

import br.usp.sdext.core.Model;
import br.usp.sdext.db.HibernateUtil;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.coalition.Coalition;
import br.usp.sdext.models.election.Election;
import br.usp.sdext.models.party.Party;

@Entity
public class Candidature extends Model implements Serializable {

	private static final long serialVersionUID = 2324783032637391486L;

	@Id
	private Long id;
	
	@ManyToOne
	private Candidate candidate;
	
	@ManyToOne
	private Election election;
	
	@ManyToOne
	private Party party;
	
	@ManyToOne
	private Coalition coalition;
	
	private String ballotName;
	
	private Integer ballotNo;
	
	@ManyToOne
	private Situation situation;
	
	@ManyToOne
	private Result result;
	
	@Column(name="max_expenses")
	private Float maxExpenses;
	
	@Column(name="tse_code")
	private Long candidateTseCode;
	
	public Candidature() {}
	
	public Candidature(Candidate candidate, Election election) {
		
		this.candidate = candidate;
		this.election = election;
	}

	public Candidature(Candidate candidate, Election election,
			Party party, Coalition coalition, String ballotName,
			Integer ballotNo, Situation situation, Result result,
			Float maxExpenses, Long candidateTseID) {
		
		this.candidate = candidate;
		this.election = election;
		this.party = party;
		this.coalition = coalition;
		this.ballotName = ballotName;
		this.ballotNo = ballotNo;
		this.situation = situation;
		this.result = result;
		this.maxExpenses = maxExpenses;
		this.candidateTseCode = candidateTseID;
	}

	// getters
	public Long getID() {return id;}
	public Election getElection() {return election;}
	public Candidate getCandidate() {return candidate;}
	public Coalition getCoalition() {return coalition;}
	public String getBallotName() {return ballotName;}
	public Party getParty() {return party;}
	public Integer getBallotNo() {return ballotNo;}
	public Situation getSituation() {return situation;}
	public Float getMaxExpenses() {return maxExpenses;}
	public Result getResult() {return result;}
	public Long getTseCode() {return candidateTseCode;}

	// setters
	public void setId(Long id) {this.id = id;}
	public void setElection(Election election) {this.election = election;}
	public void setCandidate(Candidate candidate) {this.candidate = candidate;}
	public void setCoalition(Coalition coalition) {this.coalition = coalition;}
	public void setParty(Party party) {this.party = party;}
	public void setBallotName(String ballotName) {this.ballotName = ballotName;}
	public void setBallotNo(Integer ballotNo) {this.ballotNo = ballotNo;}
	public void setSituation(Situation situation) {this.situation = situation;}
	public void setMaxExpenses(Float maxExpenses) {this.maxExpenses = maxExpenses;}
	public void setResult(Result result) {this.result = result;}
	public void setTseCode(Long tseID) {this.candidateTseCode = tseID;}
	
	@Override
	public String toString() {
		return "Candidature [ballotName=" + ballotName
				+ ", ballotNo=" + ballotNo
				+ ", situation=" + situation.getLabel() + ", maxExpenses=" + maxExpenses
				+ ", result=" + result.getLabel()
				+ ", candidateTseID=" + candidateTseCode + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((candidate == null) ? 0 : candidate.hashCode());
		result = prime * result + ((election == null) ? 0 : election.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Candidature other = (Candidature) obj;
		if (candidate == null) {
			if (other.candidate != null) {
				return false;
			}
		} else if (!candidate.equals(other.candidate)) {
			return false;
		}
		if (election == null) {
			if (other.election != null) {
				return false;
			}
		} else if (!election.equals(other.election)) {
			return false;
		}
		return true;
	}

	public static Candidature findByBasic(String candidateName, Integer ballotNo, 
			Integer year, String post) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Candidature candidature;
		
		String sql = "select CR from Candidature as CR where CR.candidate.name = :name " +
				"and CR.ballotNo = :ballotNo and CR.election.year = :year " +
				"and CR.election.round = 1 and CR.election.post = :post";
		
		candidature =  (Candidature) session.createQuery(sql).
				setParameter("name", candidateName).
				setParameter("ballotNo", ballotNo).
				setParameter("year", year).
				setParameter("post", post).
				uniqueResult();

		session.getTransaction().commit();

		return candidature;
	}
}












