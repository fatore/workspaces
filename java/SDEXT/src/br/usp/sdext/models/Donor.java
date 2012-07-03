package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class Donor extends Model implements Serializable {
	
	private static final long serialVersionUID = 3082076563549869411L;

	@Id
	private Long id; 
	
	@Column(unique=true)
	private Long cpf; // or cnpj
	private String name;
	private String uf;
	
	public Donor(String name, Long cpf, String uf) {
		this.name = name;
		this.cpf = cpf;
		this.uf = uf;
	}
	
	public Donor() {}

	// getters
	public Long getID() {return id;}
	public String getName() {return name;}
	public Long getCpf() {return cpf;}
	public String getUf() {return uf;}

	// setters 
	public void setId(Long id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setCpf(Long cpf) {this.cpf = cpf;}
	public void setUf(String uf) {this.uf = uf;}

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

	public String toString() {
		return name + ", " + uf + ", " + cpf;
	}
	
	public static Donor parse(String[] pieces, boolean old) {

		Long donorCPF = null;
		String donorUF = null;
		String donorName = null;
		if (old) {
			donorCPF = Misc.parseLong(pieces[6]);
			donorUF = Misc.parseStr(pieces[7]);
			donorName = Misc.parseStr(pieces[8]);
		} else {
			donorCPF = Misc.parseLong(pieces[10]);
			donorUF = Misc.parseStr(pieces[1]);
			donorName = Misc.parseStr(pieces[11]);
		}
		
		return new Donor(donorName, donorCPF, donorUF);
	}
}
