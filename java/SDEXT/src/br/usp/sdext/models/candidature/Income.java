package br.usp.sdext.models.candidature;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.Donor;
import br.usp.sdext.util.Misc;

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
	
	public Income(Float value, String type, Date date) {
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
		return donor.getName() + ", " + value + ", " + type;
	}

	public static Income parse(String[] pieces, boolean old) throws Exception {

		Date date = null;
		Float value = null;
		String type = null;
		
		if (old){
			date = Misc.parseDate(pieces[5]);
			value = Misc.parseFloat(pieces[9]);
			try {
				type = Misc.parseStr(pieces[10]);
			} catch (Exception e) {}
		} else {
			date = Misc.parseDate(pieces[12]);
			value = Misc.parseFloat(pieces[13]);
			type = Misc.parseStr(pieces[16]);
		}
		
		return new Income(value, type, date);
	}
}


















