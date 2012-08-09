package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Region extends Model implements Serializable {

	private static final long serialVersionUID = 8755466597504860771L;

	@Id
	private Long id; // IBGE code
	
	private String name;
	private String namex;
	
	private String acronym;
	private String acronymx;
	
	private String status;
	
	public Region(Long id, String name, String namex, String acronym,
			String acronymx, String status) {
		
		this.id = id;
		this.name = name;
		this.namex = namex;
		this.acronym = acronym;
		this.acronymx = acronymx;
		this.status = status;
	}
	
	public Long getId() {return id;}
	public String getName() {return name;}
	public String getNamex() {return namex;}
	public String getAcronym() {return acronym;}
	public String getAcronymx() {return acronymx;}
	public String getStatus() {return status;}

	public void setId(Long id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setNamex(String namex) {this.namex = namex;}
	public void setAcronym(String acronym) {this.acronym = acronym;}
	public void setAcronymx(String acronymx) {this.acronymx = acronymx;}
	public void setStatus(String status) {this.status = status;}
	
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
