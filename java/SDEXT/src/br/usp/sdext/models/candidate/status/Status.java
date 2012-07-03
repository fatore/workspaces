package br.usp.sdext.models.candidate.status;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.util.Misc;

@Entity
public class Status extends Model implements Serializable {

	private static final long serialVersionUID = 2324783032637391486L;

	@Id
	@ManyToOne
	private Candidate candidate;
	
	private Integer year;
	
	private Integer age;
	
	@Column(name="tse_id")
	private Long tseID;
	
	@ManyToOne
	private Job job;
	
	@ManyToOne
	private Schooling schooling;
	
	@ManyToOne
	private MaritalStatus maritalStatus;
	
	// private Estate[] candidateEstate;
	
	public Status() {}
	
	public Status(Integer year, Integer age, Long tseID) {
		
		this.year = year;
		this.age = age;
		this.tseID = tseID;
	}	
	
	// getters
	public Job getJob() {return job;}
	public Integer getAge() {return age;}
	public Schooling getSchooling() {return schooling;	}
	public MaritalStatus getMaritalStatus() {return maritalStatus;}
	public Candidate getCandidate() {return candidate;}
	public Long getTseID() {return tseID;}

	// setters
	public void setId(Long id) {}
	public void setJob(Job job) {this.job = job;}
	public void setAge(Integer age) {this.age = age;}
	public void setSchooling(Schooling schooling) {this.schooling = schooling;}
	public void setMaritalStatus(MaritalStatus maritalStatus) {this.maritalStatus = maritalStatus;}
	public void setCandidate(Candidate candidate) {this.candidate = candidate;}
	public void setTseID(Long tseID) {this.tseID = tseID;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((candidate == null) ? 0 : candidate.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		Status other = (Status) obj;
		if (candidate == null) {
			if (other.candidate != null)
				return false;
		} else if (!candidate.equals(other.candidate))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	@Override
	public String toString() {
		
		return year + ", "	+ age + ", " + job.toString() + ", " + schooling.toString() 
				+ ", " + maritalStatus.toString() + ", " + tseID ;
	}

	public static Status parse(String[] pieces, Integer year) throws Exception {

		Integer age = Misc.parseInt(pieces[27]);
		
		if (age == null) {
			Date birthDate = Misc.parseDate(pieces[25]);
			age = Misc.getAge(birthDate);
		}
		
		Long tseID = Misc.parseLong(pieces[11]);
		
		return new Status(year, age, tseID);
	}
}








