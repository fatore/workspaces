package br.usp.sdext.models;

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
	
	private Integer ibgeCode;
	
	private String name;
	private String namex;
	
	private String acronym;
	
	@ManyToOne
	private State state;
	
	private String status;
	
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
	public Integer getIbgeCode() {return ibgeCode;}
	public String getName() {return name;}
	public String getNamex() {return namex;}
	public String getAcronym() {return acronym;}
	public State getState() {return state;}
	public String getStatus() {return status;}
	
	public void setId(Long id) {this.id = id;}
	public void setIbgeCode(Integer ibgeCode) {this.ibgeCode = ibgeCode;}
	public void setName(String name) {this.name = name;}
	public void setNamex(String namex) {this.namex = namex;}
	public void setAcronym(String acronym) {this.acronym = acronym;}
	public void setState(State state) {this.state = state;}
	public void setStatus(String status) {this.status = status;}
	
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
