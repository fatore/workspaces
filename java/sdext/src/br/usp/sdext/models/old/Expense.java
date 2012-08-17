package br.usp.sdext.models.old;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Expense extends Model implements Serializable {

	private static final long serialVersionUID = 1569168020759931352L;

	@Id
	private Long id;
	
	@ManyToOne
	private Provider provider;
	
	private Float value;
	private String type;
	private Date date;
	
	public Expense() {}
	
	public Expense(Float value, String type, Date date) throws Exception {
		
		if (value == null) {
			throw new Exception();
		}
		
		this.value = value;
		this.type = type;
		this.date = date;
	}
	
	// getters
	public Long getID() {return id;}
	public Provider getProvider() {return provider;	}
	public Float getValue() {return value;}
	public String getType() {return type;}
	public Date getDate() {return date;}
	
	// setters
	public void setId(Long id) {this.id = id;}
	public void setProvider(Provider provider) {this.provider = provider;}
	public void setValue(Float value) {this.value = value;}
	public void setType(String type) {this.type = type;}
	public void setDate(Date date) {this.date = date;}
	
	@Override
	public String toString() {
		return "Expense [id=" + id + ", provider=" + provider + ", value="
				+ value + ", type=" + type + ", date=" + date + "]";
	}
}


















