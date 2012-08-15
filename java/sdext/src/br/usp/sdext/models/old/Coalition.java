package br.usp.sdext.models.old;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Coalition extends Model implements Serializable {

	private static final long serialVersionUID = -4136265037405380769L;
	
	@Id
	private Long id;
	
	private Long code;
	private String acronym;
	private String name;
	private String composition;
	
	public Coalition() {}
	
	public Coalition(Long code, String acronym, String name, 
			String composition) throws Exception {
		
		if (code == null) {
			throw new Exception();
		}
		
		this.code = code;
		this.acronym = acronym;
		this.name = name;
		this.composition = composition;
	}

	public Long getID() {return id;}
	public Long getCode() {return code;}
	public String getAcronym() {return acronym;}
	public String getName() {return name;}
	public String getComposition() {return composition;}

	public void setId(Long id) {this.id = id;}
	public void setCode(Long code) {this.code = code;}
	public void setAcronym(String acronym) {this.acronym = acronym;}
	public void setName(String name) {this.name = name;}
	public void setComposition(String composition) {this.composition = composition;}
	
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		Coalition other = (Coalition) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Coalition [id=" + id + ", code=" + code + ", acronym="
				+ acronym + ", name=" + name + ", composition=" + composition
				+ "]";
	}
}
