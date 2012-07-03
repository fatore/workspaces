package br.usp.sdext.models.candidate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class BirthTown extends Model implements Serializable {
	
	private static final long serialVersionUID = -1828441396997644793L;

	@Id
	private Long id;
	
	private Long tseId;
	
	private String label;
	
	public BirthTown() {}
	
	public BirthTown(Long tseId, String label) {
		
		this.id = null;
		this.tseId = tseId;
		this.label = label;
	}
	
	public Long getId() {return id;}
	public Long getTseId() {return tseId;} 
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
	public void setTseId(Long tseId) {this.tseId = tseId;}
	public void setLabel(String label) {this.label = label;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		BirthTown other = (BirthTown) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	public static BirthTown parse(String[] pieces) throws Exception {
		
		Long birthTownID = Misc.parseLong(pieces[37]);
		String birthTown = Misc.parseStr(pieces[38]);
		
		if (birthTownID != null) {
			throw new Exception();
		}
		
		return new BirthTown(birthTownID, birthTown);
	}
}
