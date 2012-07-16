package br.usp.sdext.models.candidate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Town;
import br.usp.sdext.util.Misc;


@Entity
public class Candidate extends Model implements  Serializable {
	
	private static final long serialVersionUID = 4220422090844941540L;

	@Id
	private Long id;
	
	@Column(nullable=false) 
	private String name;
	
	@Column(name="voter_id")
	private Long voterID;
	
	@Column(name="birth_date")
	private Date birthDate;
	
	@ManyToOne
	private Sex sex;
	
	@ManyToOne
	private Town birthTown;
	
	@ManyToOne
	private Citizenship citizenship;
	
	private boolean dupper;
	
	public Candidate() {}

	public Candidate(Long voterID, String name, Date birthDate) throws Exception {
		
		if (name == null || birthDate == null || voterID == null) {
			throw new Exception();
		}		
		
		this.id = null;
		
		this.voterID = voterID;
		this.name = name;
		this.birthDate = birthDate;
		this.dupper = false;
	}

	public Candidate(String[] pieces) throws Exception {

		this(Misc.parseLong(pieces[26]), // voterID
				Misc.parseStr(pieces[10]), // name
				Misc.parseDate(pieces[25])); // birth date
	}

	// getters
	public Long getID() {return id;}
	public Sex getSex() {return sex;}
	public Date getBirthDate() {return birthDate;}
	public Town getBirthTown() {return birthTown;}
	public Long getVoterID() {return voterID;}
	public String getName() {return name;}
	public Citizenship getCitizenship() {return citizenship;}
	public boolean getDupper() {return dupper;}

	// setters
	public void setId(Long id) {this.id = id;}
	public void setSex(Sex sex) {this.sex = sex;}
	public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}
	public void setBirthTown(Town birthTown) {this.birthTown = birthTown;}
	public void setVoterID(Long voterID) {this.voterID = voterID;}
	public void setName(String name) {this.name = name;}
	public void setCitizenship(Citizenship citizenship) {this.citizenship = citizenship;}
	public void setDupper(boolean dupper) {this.dupper = dupper;}
	
	@Override
	public String toString() {
		return "Candidate [id=" + id + ", name=" + name + ", voterID="
				+ voterID + ", birthDate=" + birthDate + ", sex=" + sex
				+ ", birthTown=" + birthTown
				+ ", citizenship=" + citizenship + ", dupper=" + dupper + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((voterID == null) ? 0 : voterID.hashCode());
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
		Candidate other = (Candidate) obj;
		if (voterID == null) {
			if (other.voterID != null)
				return false;
		} else if (!voterID.equals(other.voterID))
			return false;
		return true;
	}

	public boolean similar(Object obj) {
		
		Candidate other = (Candidate) obj;

		if (name != null && other.name != null) {
			if (name.equals(other.name)) {
				return true;
			}
		} 
		return false;
	}
}
