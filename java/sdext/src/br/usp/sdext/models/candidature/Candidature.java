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
import br.usp.sdext.parsers.ExpenseBinding;
import br.usp.sdext.parsers.IncomeBinding;

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
	
	private Long situationID;
	private String situation;
	
	@Column(name="max_expenses")
	private Float maxExpenses;
	
	@Column(name="result_id")
	private Long resultID;
	private String result;
	
	@OneToMany
	private List<Income> incomes = new ArrayList<Income>();
	@Column(name="real_incomes")
	private Float realIncomes = new Float(0);
	
	@OneToMany
	private List<Expense> expenses = new ArrayList<Expense>(); 
	@Column(name="real_expenses")
	private Float realExpenses = new Float(0);
	
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
	public Long getID() {return id;}
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
	public List<Expense> getExpenses() {return expenses;}
	public Long getResultID() {return resultID;}
	public String getResult() {return result;}

	// setters
	public void setId(Long id) {this.id = id;}
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
	
	
	public void incRealIncomes(float value) {this.realIncomes += value;}
	public void incRealExpenses(float value) {this.realExpenses += value;}
	
	public void addIncome(Income income) {
		
		this.incomes.add(income);
		Float value = income.getValue();
		if (value != null) {
			incRealIncomes(value);
		}
	}
	
	public void addExpense(Expense expense) {
		
		this.expenses.add(expense);
		Float value = expense.getValue();
		if (value != null) {
			incRealExpenses(value);
		}
	}
	@Override
	public String toString() {
		return "Candidature [id=" + id + ", candidate=" + candidate
				+ ", election=" + election + ", party=" + party
				+ ", coalition=" + coalition + ", ballotName=" + ballotName
				+ ", ballotNo=" + ballotNo + ", situationID=" + situationID
				+ ", situation=" + situation + ", maxExpenses=" + maxExpenses
				+ ", resultID=" + resultID + ", result=" + result
				+ ", incomes=" + incomes + ", realIncomes=" + realIncomes
				+ ", expenses=" + expenses + ", realExpenses=" + realExpenses
				+ "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ballotNo == null) ? 0 : ballotNo.hashCode());
		result = prime * result + ((candidate.getName() == null) ? 0 : candidate.getName().hashCode());
		result = prime * result + ((election.getPost() == null) ? 0 : election.getPost().hashCode());
		result = prime * result + ((election.getYear() == null) ? 0 : election.getYear().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		
		if (ballotNo == null) {
			if (other.ballotNo != null) {
				return false;
			}
		} else if (!ballotNo.equals(other.ballotNo)) {
			return false;
		}
		
		if (candidate.getName() == null) {
			if (other.candidate.getName() != null) {
				return false;
			}
		} else if (!candidate.getName().equals(other.candidate.getName())) {
			return false;
		}
		
		if (election.getPost() == null) {
			if (other.election.getPost() != null) {
				return false;
			}
		} else if (!election.getPost().equals(other.election.getPost())) {
			return false;
		}
		
		if (election.getYear() == null) {
			if (other.election.getYear() != null) {
				return false;
			}
		} else if (!election.getYear().equals(other.election.getYear())) {
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

	@SuppressWarnings("unchecked")
	private static Candidature findByBinding(Session session, Binding binding) throws Exception {

		List<Candidature> candidatures;
		
		String post = binding.getPost();
		String candidateName = binding.getCandidateName();
		Integer ballotNo = binding.getBallotNo();
		int year = binding.getYear();

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

			return candidatures.get(0);
		} 
		if (candidatures.size() < 1){
			return null;
		}
		if (candidatures.size() > 1){
			throw new Exception("Binding resulted in more than one candidature.");
		}
		return null;
	}

	public static boolean addIncome(IncomeBinding binding) throws Exception {
		
		boolean sucess = true;
		
		String candidateName = binding.getCandidateName();
		Integer ballotNo = binding.getBallotNo();
		Income income = binding.getIncome();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Candidature candidature = findByBinding(session, binding);
		
		if (candidature != null) {
			
			candidature.addIncome(income);
		} 
		else {
			session.save(new GhostCandidate(candidateName, ballotNo, income, null));
			sucess = false;
		}
		
		session.getTransaction().commit();
		
		return sucess;
	}
	
	public static boolean addIncomes(ArrayList<IncomeBinding> bindings) throws Exception {
		
		boolean sucess = true;
		
		String candidateName = bindings.get(0).getCandidateName();
		Integer ballotNo = bindings.get(0).getBallotNo();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Candidature candidature = findByBinding(session, bindings.get(0));
		
		for (IncomeBinding binding : bindings) {
			
			if (candidature != null) {

				candidature.addIncome(binding.getIncome());
			} 
			else {
				session.save(new GhostCandidate(candidateName, ballotNo, binding.getIncome(), null));
				sucess = false;
			}
		}
		
		session.getTransaction().commit();
		
		return sucess;
	}
	
	public static boolean addExpense(ExpenseBinding binding) throws Exception {
		
		boolean sucess = true;
		
		String candidateName = binding.getCandidateName();
		Integer ballotNo = binding.getBallotNo();
		Expense expense = binding.getExpense();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Candidature candidature = findByBinding(session, binding);
		
		if (candidature != null) {
			
			candidature.addExpense(expense);
		} 
		else {
			session.save(new GhostCandidate(candidateName, ballotNo, null, binding.getExpense()));
			sucess = false;
		}
		
		session.getTransaction().commit();
		
		return sucess;
	}
	
	public static boolean addExpenses(ArrayList<ExpenseBinding> bindings) throws Exception {
		
		boolean sucess = true;
		
		String candidateName = bindings.get(0).getCandidateName();
		Integer ballotNo = bindings.get(0).getBallotNo();
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		Candidature candidature = findByBinding(session, bindings.get(0));
		
		for (ExpenseBinding binding : bindings) {
			
			if (candidature != null) {

				candidature.addExpense(binding.getExpense());
			} 
			else {
				session.save(new GhostCandidate(candidateName, ballotNo, null, binding.getExpense()));
				sucess = false;
			}
		}
		
		session.getTransaction().commit();
		
		return sucess;
	}
}












