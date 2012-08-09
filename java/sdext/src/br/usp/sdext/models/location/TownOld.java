package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class TownOld extends Model implements Serializable {
	
	private static final long serialVersionUID = -1828441396997644793L;

	@Id
	private Long id;
	
	@ManyToOne
	private StateOld state;
	
	private Long tseId;
	
	private Long ueID;
	
	private String label;
	
	public TownOld() {}
	
	public TownOld(Long tseId, String label) {
		
		this.id = null;
		this.tseId = tseId;
		this.label = label;
	}
	
	public Long getId() {return id;}
	public StateOld getState() {return state;}
	public Long getTseId() {return tseId;} 
	public Long getUeId() {return ueID;} 
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
	public void setState(StateOld state) {this.state = state;}
	public void setTseId(Long tseId) {this.tseId = tseId;}
	public void setUeId(Long ueId) {this.ueID = ueId;}
	public void setLabel(String label) {this.label = label;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		TownOld other = (TownOld) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Town [id=" + id + ", state=" + state + ", tseId=" + tseId
				+ ", ueID=" + ueID + ", label=" + label + "]";
	}
}
