package br.usp.sdext.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;


@Entity
public class Candidate extends Model implements  Serializable {
	
	private static final long serialVersionUID = 4220422090844941540L;

	@Id
	private Long id;
	
	@Column(nullable=false) 
	private String name;

	private String namex;
	
	@Column(name="voter_id")
	private Long voterID;
	
	private Long cpf;
	
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

	public Candidate(Long voterID, String namex, Date birthDate) {
		
		this.id = null;
		this.voterID = voterID;
		this.namex = namex;
		this.birthDate = birthDate;
		this.dupper = false;
	}
	
	public Candidate(Long voterID) {
		
		this.voterID = voterID;
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
	public Long getCPF() {return cpf;}
	public String getNamex() {return namex;}
	public Long getCpf() {return cpf;}


	// setters
	public void setId(Long id) {this.id = id;}
	public void setNamex(String namex) {this.namex = namex;}
	public void setSex(Sex sex) {this.sex = sex;}
	public void setCpf(Long cpf) {this.cpf = cpf;}
	public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}
	public void setBirthTown(Town birthTown) {this.birthTown = birthTown;}
	public void setVoterID(Long voterID) {this.voterID = voterID;}
	public void setName(String name) {this.name = name;}
	public void setCitizenship(Citizenship citizenship) {this.citizenship = citizenship;}
	public void setDupper(boolean dupper) {this.dupper = dupper;}
	public void setCPF(Long cPF) {cpf = cPF;}
	
	@Override
	public String toString() {
		return "Candidate [namex=" + namex + ", voterID=" + voterID
				+ ", birthDate=" + birthDate + "]";
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

	public boolean hasSameName(Object obj) {
		
		Candidate other = (Candidate) obj;

		if (namex != null && other.namex != null) {
			if (namex.equals(other.namex)) {
				return true;
			}
		} 
		return false;
	}
}
