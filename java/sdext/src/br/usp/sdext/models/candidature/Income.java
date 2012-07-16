package br.usp.sdext.models.candidature;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Income extends Model implements Serializable {

	private static final long serialVersionUID = 2835344125199701470L;

	@Id
	private Long id;
	
	@ManyToOne
	private Donor donor;
	
	private Float value;
	private String type;
	private Date date;
	
	public Income() {}
	
	public Income(Float value, String type, Date date) throws Exception {
		
		if (value == null) {
			throw new Exception();
		}
		
		this.value = value;
		this.type = type;
		this.date = date;
	}
	
	// getters
	public Long getID() {return id;}
	public Donor getDonor() {return donor;	}
	public Float getValue() {return value;}
	public String getType() {return type;}
	public Date getDate() {return date;}
	
	// setters
	public void setId(Long id) {this.id = id;}
	public void setDonor(Donor donor) {this.donor = donor;}
	public void setValue(Float value) {this.value = value;}
	public void setType(String type) {this.type = type;}
	public void setDate(Date date) {this.date = date;}
	
	@Override
	public String toString() {
		return "Income [id=" + id + ", donor=" + donor + ", value=" + value
				+ ", type=" + type + ", date=" + date + "]";
	}
}


















