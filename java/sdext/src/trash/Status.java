package trash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.usp.sdext.core.Model;

@Entity
public class Status extends Model implements Serializable {

	private static final long serialVersionUID = 2324783032637391486L;

	@Id
	private Long id;
	
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
	
	@OneToMany
	private List<Estate> estates = new ArrayList<>();
	public void addEstate(Estate estate) {this.estates.add(estate);}
	
	@ManyToOne
	private Candidate candidate;
	public void setCandidate(Candidate candidate) {this.candidate = candidate;}
	public Candidate getCandidate() {return candidate;}
	
	public Status() {}
	
	public Status(Integer year, Integer age, Long tseID) {
		
		this.year = year;
		this.age = age;
		this.tseID = tseID;
	}	
	
	// getters
	public Long getId() {return id;}
	public Job getJob() {return job;}
	public Integer getAge() {return age;}
	public Schooling getSchooling() {return schooling;	}
	public MaritalStatus getMaritalStatus() {return maritalStatus;}
	public Long getTseID() {return tseID;}
	public Integer getYear() {return year;}

	// setters
	public void setId(Long id) {this.id = id;}
	public void setJob(Job job) {this.job = job;}
	public void setAge(Integer age) {this.age = age;}
	public void setSchooling(Schooling schooling) {this.schooling = schooling;}
	public void setMaritalStatus(MaritalStatus maritalStatus) {this.maritalStatus = maritalStatus;}
	public void setTseID(Long tseID) {this.tseID = tseID;}
	public void setYear(Integer year) {this.year = year;}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((candidate == null) ? 0 : candidate.hashCode());
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
		return "Status [id=" + id + ", year=" + year + ", age=" + age
				+ ", tseID=" + tseID + ", job=" + job + ", schooling="
				+ schooling + ", maritalStatus=" + maritalStatus + ", estates="
				+ estates + "]";
	}
}



