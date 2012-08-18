package br.usp.sdext.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.LevenshteinDistance;


@Entity
public class Candidate extends Model implements  Serializable {

	private static final long serialVersionUID = 4220422090844941540L;

	@Id
	private Long id;

	private String name;

	@Column(nullable=false) 
	private String namex;

	@Column(name="voter_id")
	private String voterID;

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

	public Candidate(String voterID, String namex, Date birthDate) {

		this.id = null;
		this.voterID = voterID;
		this.namex = namex;
		this.birthDate = birthDate;
		this.dupper = false;
	}

	// getters
	public Long getID() {return id;}
	public Sex getSex() {return sex;}
	public Date getBirthDate() {return birthDate;}
	public Town getBirthTown() {return birthTown;}
	public String getVoterID() {return voterID;}
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
	public void setVoterID(String voterID) {this.voterID = voterID;}
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
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((namex == null) ? 0 : namex.hashCode());
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
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (namex == null) {
			if (other.namex != null)
				return false;
		} else if (!namex.equals(other.namex))
			return false;
		return true;
	}

	public boolean hasSameNamex(Object obj) {

		Candidate other = (Candidate) obj;

		if (namex != null && other.namex != null) {
			if (namex.equals(other.namex)) {
				return true;
			}
		} 
		return false;
	}

	public boolean hasSimilarNamex(Candidate missCandidate) {

		int distance = LevenshteinDistance.computeLevenshteinDistance(
				missCandidate.getNamex(), this.getNamex());

		if (distance <= 3) {

			return true;
		}
		return false;
	}
}
