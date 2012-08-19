package trash;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Donor extends Model implements Serializable {
	
	private static final long serialVersionUID = 3082076563549869411L;

	@Id
	private Long id; 
	
	@Column(unique=true)
	private Long cpf; // or cnpj
	private String name;
	
	@ManyToOne
	private State state;
	
	public Donor() {}
	
	public Donor(String name, Long cpf) throws Exception {
		
		this.name = name;
		this.cpf = cpf;
	}
	
	// getters
	public Long getID() {return id;}
	public String getName() {return name;}
	public Long getCpf() {return cpf;}
	public State getState() {return state;}

	// setters 
	public void setId(Long id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setCpf(Long cpf) {this.cpf = cpf;}
	public void setState(State state) {this.state = state;}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
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
		Donor other = (Donor) obj;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Donor [id=" + id + ", cpf=" + cpf + ", name=" + name
				+ ", state=" + state + "]";
	}
}
