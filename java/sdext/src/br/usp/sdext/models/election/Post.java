package br.usp.sdext.models.election;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Post extends Model implements Serializable {
	
	private static final long serialVersionUID = 1711943306104280700L;

	@Id
	private Long id;
	
	private Long tseCode;
	
	private String label;
	
	public Post() {}
	
	public Post(Long tseId, String label) {
		
		this.tseCode = tseId;
		this.label = label;
	}
	
	public Post(String label) {
		
		this.label = label;
	}

	public Long getId() {return id;}
	public Long getTseCode() {return tseCode;}
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
	public void setTseCode(Long tseId) {this.tseCode = tseId;}
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
		Post other = (Post) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", tseCode=" + tseCode + ", label=" + label
				+ "]";
	}
}
