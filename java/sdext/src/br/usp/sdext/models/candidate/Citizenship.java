package br.usp.sdext.models.candidate;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class Citizenship extends Model implements Serializable {

	private static final long serialVersionUID = 5806672468949968195L;

	@Id
	private Long id;
	
	private String label;
	
	private Long tseId;
	
	public Citizenship() {}
	
	public Citizenship(Long tseId, String label) {
		
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
		result = prime * result + ((tseId == null) ? 0 : tseId.hashCode());
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
		if (tseId == null) {
			if (other.tseId != null)
				return false;
		} else if (!tseId.equals(other.tseId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + ", " + tseId + ", " + label;
	}

	public static Citizenship parse(String[] pieces) throws Exception {
		
		Long citizenshipID = Misc.parseLong(pieces[34]);
		String citizenship = Misc.parseStr(pieces[35]);
		
		if (citizenshipID == null) {
			throw new Exception();
		}
		return new Citizenship(citizenshipID, citizenship);
	}
}
