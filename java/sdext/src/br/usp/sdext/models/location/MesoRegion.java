package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class MesoRegion extends Model implements Serializable {

	private static final long serialVersionUID = 4947044880637268178L;

	@Id
	private Long id;
	
	public Integer ibgeCode;
	
	public String name;
	public String namex;
	
	public String acronym;
	
	@ManyToOne
	private State state;
	
	public String status;
	
	public MesoRegion() {}
	
	public MesoRegion(Integer ibgeCode, String name, String namex,
			String acronym, State state, String status) {
		
		this.ibgeCode = ibgeCode;
		this.name = name;
		this.namex = namex;
		this.acronym = acronym;
		this.state = state;
		this.status = status;
	}
	
	public Long getId() {return id;}
	public State getState() {return state;}

	public void setId(Long id) {this.id = id;}
	public void setState(State state) {this.state = state;}
	@Override
	public String toString() {
		return "MesoRegion [id=" + id + ", ibgeCode=" + ibgeCode + ", name="
				+ name + ", namex=" + namex + ", acronym=" + acronym
				+ ", state=" + state + ", status=" + status + "]";
	}
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ibgeCode == null) ? 0 : ibgeCode.hashCode());
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
		MesoRegion other = (MesoRegion) obj;
		if (ibgeCode == null) {
			if (other.ibgeCode != null)
				return false;
		} else if (!ibgeCode.equals(other.ibgeCode))
			return false;
		return true;
	}
}
