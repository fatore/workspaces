package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Provider extends Model implements Serializable {
	
	private static final long serialVersionUID = 2959260910727370367L;

	@Id
	private Long id; 
	
	@Column(unique=true)
	private Long cpf; // or cnpj
	private String name;
	
	
	public Provider() {}
	
	public Provider(String name, Long cpf) throws Exception {
		
		this.name = name;
		this.cpf = cpf;
	}
	
	// getters
	public Long getID() {return id;}
	public String getName() {return name;}
	public Long getCpf() {return cpf;}

	// setters 
	public void setId(Long id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setCpf(Long cpf) {this.cpf = cpf;}

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
		Provider other = (Provider) obj;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Provider [id=" + id + ", cpf=" + cpf + ", name=" + name + "]";
	}
}
