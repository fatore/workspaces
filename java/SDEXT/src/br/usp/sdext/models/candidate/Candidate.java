package br.usp.sdext.models.candidate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
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
	private BirthTown birthTown;
	
	@ManyToOne
	private BirthState birthState;
	
	@ManyToOne
	private Citizenship citizenship;
	
	private boolean dupper;
	
	public Candidate() {}

	public Candidate(Long voterID, String name, Date birthDate) {
		
		this.id = null;
		
		this.voterID = voterID;
		this.name = name;
		this.birthDate = birthDate;
		this.dupper = false;
	}

	// getters
	public Long getID() {return id;}
	public Sex getSex() {return sex;}
	public Date getBirthDate() {return birthDate;}
	public BirthTown getBirthTown() {return birthTown;}
	public BirthState getBirthState() {return birthState;}	
	public Long getVoterID() {return voterID;}
	public String getName() {return name;}
	public Citizenship getCitizenship() {return citizenship;}
	public boolean getDupper() {return dupper;}

	// setters
	public void setId(Long id) {this.id = id;}
	public void setSex(Sex sex) {this.sex = sex;}
	public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}
	public void setBirthState(BirthState birthState) {this.birthState = birthState;}
	public void setBirthTown(BirthTown birthTown) {this.birthTown = birthTown;}
	public void setVoterID(Long voterID) {this.voterID = voterID;}
	public void setName(String name) {this.name = name;}
	public void setCitizenship(Citizenship citizenship) {this.citizenship = citizenship;}
	public void setDupper(boolean dupper) {this.dupper = dupper;}
	
	@Override
	public String toString() {
		
		return name + ", birthDate=" + birthDate + ", voterId=" + voterID;
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

	public int validate() {
		
		int count = 0;
		
		count += (voterID != null && !voterID.equals(-1) && !voterID.equals(0)) ? 100 : 0;
		count += (birthDate != null) ? 5 : 0;
		count += (birthState != null) ? 3 : 0;
		count += (sex != null) ? 1 : 0;
		count += (citizenship != null) ? 1 : 0;
		count += (birthTown != null) ? 3 : 0;
		
		count *= (name != null) ? 1 : 0;
		return count;
	}
	
	public void merge(Candidate candidate) {
		
		this.voterID = (candidate.voterID != null) ? candidate.voterID : voterID;
		this.name = (candidate.name != null) ? candidate.name : name;
		this.birthDate = (candidate.birthDate != null) ? candidate.birthDate : birthDate;
		this.birthState = (candidate.birthState != null) ? candidate.birthState : birthState;
		this.sex = (candidate.sex != null) ? candidate.sex : sex;
		this.citizenship = (candidate.citizenship != null) ? candidate.citizenship : citizenship;
		this.birthTown = (candidate.birthTown != null) ? candidate.birthTown : birthTown;
	}
	
	public static Candidate parse(String[] pieces) throws Exception {
		
		String name = Misc.parseStr(pieces[10]);
		Date birthDate = Misc.parseDate(pieces[25]);
		Long voterID = Misc.parseLong(pieces[26]);
		
		if (name == null) {
			throw new Exception();
		}
		return new Candidate(voterID, name, birthDate);
	}
}
