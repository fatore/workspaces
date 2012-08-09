package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Region extends Model implements Serializable {

	private static final long serialVersionUID = 8755466597504860771L;

	@Id
	private Long id;
	
	public Integer ibgeCode;
	
	public String name;
	public String namex;
	
	public String acronym;
	public String acronymx;
	
	public String status;
	
	public Region() {}
	
	public Region(Integer ibgeCode, String name, String namex, String acronym,
			String acronymx, String status) {
		
		this.ibgeCode = ibgeCode;
		this.name = name;
		this.namex = namex;
		this.acronym = acronym;
		this.acronymx = acronymx;
		this.status = status;
	}
	
	public Long getId() {return id;}
	public void setId(Long id) {this.id = id;}
	
	@Override
	public String toString() {
		
		return "Region [id=" + id + ", name=" + name + ", namex=" + namex
				+ ", acronym=" + acronym + ", acronymx=" + acronymx
				+ ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ibgeCode == null) ? 0 : ibgeCode.hashCode());
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
		Region other = (Region) obj;
		if (ibgeCode == null) {
			if (other.ibgeCode != null)
				return false;
		} else if (!ibgeCode.equals(other.ibgeCode))
			return false;
		return true;
	}
}
