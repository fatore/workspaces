package br.usp.sdext.models.candidate.status;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class MaritalStatus extends Model implements Serializable {
	
	private static final long serialVersionUID = -8330825712596122024L;

	@Id
	private Long id;
	
	private Long tseId;
	
	private String label;
	
	public MaritalStatus() {}
	
	public MaritalStatus(Long tseId, String label) {
		
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
		MaritalStatus other = (MaritalStatus) obj;
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

	public static MaritalStatus parse(String[] pieces) throws Exception {
		
		Long maritalID = Misc.parseLong(pieces[32]);
		String marital = Misc.parseStr(pieces[33]);
		
		if (maritalID == null) {
			throw new Exception();
		}
		else return new MaritalStatus(maritalID, marital);
	}
}
