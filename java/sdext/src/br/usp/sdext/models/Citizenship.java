package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Citizenship extends Model implements Serializable {

	private static final long serialVersionUID = 5806672468949968195L;

	@Id
	private Long id;
	
	private String label;
	
	private Long tseCode;
	
	public Citizenship() {}
	
	public Citizenship(Long tseId, String label) {
		
		this.tseCode = tseId;
		this.label = label;
	}
	
	public Long getId() {return id;}
	public Long getTseCode() {return tseCode;}
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
	public void setTseCode(Long tseId) {this.tseCode = tseId;}
	public void setLabel(String label) {this.label = label;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tseCode == null) ? 0 : tseCode.hashCode());
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
		Citizenship other = (Citizenship) obj;
		if (tseCode == null) {
			if (other.tseCode != null)
				return false;
		} else if (!tseCode.equals(other.tseCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Citizenship [id=" + id + ", label=" + label + ", tseId="
				+ tseCode + "]";
	}
}
