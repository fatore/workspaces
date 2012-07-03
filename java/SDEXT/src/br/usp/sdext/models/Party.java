package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class Party extends Model implements Serializable {

	private static final long serialVersionUID = 7101407303061260986L;

	@Id
	private Long id;
	
	private Integer no;
	private String acronym;
	private String name;	
	
	public Party() {}
	
	public Party(Integer partyNo, String partyAcronym, String partyName) {
		
		this.no = partyNo;
		this.acronym = partyAcronym;
		this.name = partyName;
	}
	
	public Long getID() {return id;}
	public Integer getNo() {return no;}
	public String getAcronym() {return acronym;}
	public String getName() {return name;}
	
	public void setId(Long id) {this.id = id;}
	public void setNo(Integer partyNo) {this.no = partyNo;}
	public void setAcronym(String partyAcronym) {this.acronym = partyAcronym;}
	public void setName(String partyName) {this.name = partyName;}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((no == null) ? 0 : no.hashCode());
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
		Party other = (Party) obj;
		if (no == null) {
			if (other.no != null)
				return false;
		} else if (!no.equals(other.no))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		
		return no + ", " + acronym + ", " + name;
	}

	public static Party parse(String[] pieces) {

		Integer partyNo  = Misc.parseInt(pieces[16]);
		String partyAcronym = Misc.parseStr(pieces[17]);
		String partyName = Misc.parseStr(pieces[18]);
		
		return new Party(partyNo, partyAcronym, partyName);
	}
}
