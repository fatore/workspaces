package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class State extends Model implements Serializable {

	private static final long serialVersionUID = -2492705092435132356L;

	@Id
	private Long id;
	
	public Integer ibgeCode;
	
	public String acronym;
	
	public String name;
	public String namex;
	
	public Integer sinpasCode;
	
	@ManyToOne
	private Region region;
	
	public Float area;
	
	public String status;
	
	public State() {}

	public State(Integer ibgeCode, String acronym, String name, String namex,
			Integer sinpasCode, Region region, Float area, String status) {
		
		this.ibgeCode = ibgeCode;
		this.acronym = acronym;
		this.name = name;
		this.namex = namex;
		this.sinpasCode = sinpasCode;
		this.region = region;
		this.area = area;
		this.status = status;
	}
	
	public Long getId() {return id;}
	public Region getRegion() {return region;}
	
	public void setId(Long id) {this.id = id;}
	public void setRegion(Region region) {this.region = region;}

	@Override
	public String toString() {
		
		return "State [id=" + id + ", ibgeCode=" + ibgeCode + ", acronym="
				+ acronym + ", name=" + name + ", namex=" + namex
				+ ", sinpasCode=" + sinpasCode + ", region=" + region
				+ ", area=" + area + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acronym == null) ? 0 : acronym.hashCode());
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
		State other = (State) obj;
		if (acronym == null) {
			if (other.acronym != null)
				return false;
		} else if (!acronym.equals(other.acronym))
			return false;
		return true;
	}
}
