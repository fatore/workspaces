package br.usp.sdext.models.candidature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.Session;

import br.usp.sdext.core.Model;
import br.usp.sdext.db.HibernateUtil;
import br.usp.sdext.models.Coalition;
import br.usp.sdext.models.Election;
import br.usp.sdext.models.Party;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.ghosts.GhostCandidate;
import br.usp.sdext.parsers.Binding;
import br.usp.sdext.util.Misc;

@Entity
public class Candidature extends Model implements Serializable {

	private static final long serialVersionUID = 2324783032637391486L;

	@Id
	@ManyToOne
	private Candidate candidate;
	
	@Id
	@ManyToOne
	private Election election;
	
	@ManyToOne
	private Party party;
	
	@ManyToOne
	private Coalition coalition;
	
	private String ballotName;
	
	private Integer ballotNo;
	
	private Long situationID;
	private String situation;
	
	@Column(name="max_expenses")
	private Float maxExpenses;
	
	@Column(name="result_id")
	private Long resultID;
	private String result;
	
	@OneToMany
	private List<Income> incomes = new ArrayList<Income>(); 
	
	// private Expenses[] expenses
	
	public Candidature() {}
	
	public Candidature(String ballotName, Integer ballotNo,  
			Long situationID, String situation, Float maxExpenses,
			Long resultID, String result) {
		
		this.ballotName = ballotName;
		this.ballotNo = ballotNo;
		this.situationID = situationID;
		this.situation = situation;
		this.maxExpenses = maxExpenses;
		this.resultID = resultID;
		this.result = result;
	}	
	
	// getters
	public void setId(Long id) {}
	public Election getElection() {return election;}
	public Candidate getCandidate() {return candidate;}
	public Coalition getCoalition() {return coalition;}
	public String getBallotName() {return ballotName;}
	public Party getParty() {return party;}
	public Integer getBallotNo() {return ballotNo;}
	public Long getSituationID() {return situationID;}
	public String getSituation() {return situation;}
	public Float getMaxExpenses() {return maxExpenses;}
	public List<Income> getIncomes() {return incomes;}
	public Long getResultID() {return resultID;}
	public String getResult() {return result;}

	// setters
	public void setElection(Election election) {this.election = election;}
	public void setCandidate(Candidate candidate) {this.candidate = candidate;}
	public void setCoalition(Coalition coalition) {this.coalition = coalition;}
	public void setParty(Party party) {this.party = party;}
	public void setBallotName(String ballotName) {this.ballotName = ballotName;}
	public void setBallotNo(Integer ballotNo) {this.ballotNo = ballotNo;}
	public void setSituationID(Long situationID) {this.situationID = situationID;}
	public void setSituation(String situation) {this.situation = situation;}
	public void setMaxExpenses(Float maxExpenses) {this.maxExpenses = maxExpenses;}
	public void setResultID(Long resultID) {this.resultID = resultID;}
	public void setResult(String result) {this.result = result;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((candidate == null) ? 0 : candidate.getID().hashCode());
		result = prime * result	+ ((election == null) ? 0 : election.getID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Candidature other = (Candidature) obj;
		if (candidate == null) {
			if (other.candidate != null)
				return false;
		} else if (!candidate.getID().equals(other.candidate.getID()))
			return false;
		if (election == null) {
			if (other.election != null)
				return false;
		} else if (!election.getID().equals(other.election.getID()))
			return false;
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

	public static Candidature parse(String[] pieces) {
		
		Integer ballotNo = Misc.parseInt(pieces[12]);
		String ballotName = Misc.parseStr(pieces[13]);
		Long situationID = Misc.parseLong(pieces[14]);
		String situation = Misc.parseStr(pieces[15]);
		Float maxExpenses = Misc.parseFloat(pieces[39]);
		Long resultID = Misc.parseLong(pieces[40]);
		String result = Misc.parseStr(pieces[41]);
		
		return new Candidature(ballotName, ballotNo,  situationID, situation, 
				maxExpenses, resultID, result);
	}
	
	public static void addIncome(Binding binding ) {
		
		addIncome(binding.income, binding.pieces, binding.year, binding.old);
	}
	
	@SuppressWarnings("unchecked")
	public static void addIncome(Income income, String[] pieces, Integer year, boolean old) {
		
		String post = null;
		String candidateName = null;
		Integer ballotNo = null;
		
		if (old) {
			
			post = Misc.parseStr(pieces[2]);
			candidateName = Misc.parseStr(pieces[3]);
			ballotNo = Misc.parseInt(pieces[4]);
		} 
		else {
			
			post = Misc.parseStr(pieces[4]);
			candidateName = Misc.parseStr(pieces[5]);
			ballotNo = Misc.parseInt(pieces[3]);
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		List<Candidature> candidatures;
		
		String sql = "select CR from Candidature as CR where CR.candidate.name = :name " +
				"and CR.ballotNo = :ballotNo and CR.election.year = :year " +
				"and CR.election.round = 1 and CR.election.post = :post";
		
		candidatures =  (List<Candidature>) session.createQuery(sql).
				setParameter("name", candidateName).
				setParameter("ballotNo", ballotNo).
				setParameter("year", year).
				setParameter("post", post).
				list();
		
		
		if (candidatures.size() == 1) {
			
			candidatures.get(0).getIncomes().add(income);
		} 
		else {
			session.save(new GhostCandidate(candidateName, ballotNo, income));
		}
		if (candidatures.size() > 1){
			System.err.println("maior que 1");
		}
		
		session.getTransaction().commit();
	}
}












