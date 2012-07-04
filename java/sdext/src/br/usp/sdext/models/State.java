package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class State extends Model implements Serializable {
	
	private static final long serialVersionUID = 6358274013517612742L;

	@Id
	private Long id;
	
	private String label;
	
	public State() {}
	
	public State( String label) {
		
		this.id = null;
		this.label = label;
	}
	
	public Long getId() {return id;}
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
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
		State other = (State) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	public static State parse(String[] pieces) {
		
		String uf = Misc.parseStr(pieces[36]);
		
		return new State(uf);
	}
}
