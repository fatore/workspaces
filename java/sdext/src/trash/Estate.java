package trash;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class Estate extends Model {

	@Id
	private Long id;
	
	private Long tseId;
	private String label;
	private String detail;
	private Float value;
	private Date registryDate;
	
	public Estate(Long tseId, String label, String detail, Float value,
			Date registryDate) {
		
		this.tseId = tseId;
		this.label = label;
		this.detail = detail;
		this.value = value;
		this.registryDate = registryDate;
	}

	public Long getId() {return id;}
	public Long getTseId() {return tseId;}
	public String getLabel() {return label;}
	public String getDetail() {return detail;}
	public Float getValue() {return value;}
	public Date getRegistryDate() {return registryDate;}

	public void setId(Long id) {this.id = id;}
	public void setTseId(Long tseId) {this.tseId = tseId;}
	public void setLabel(String label) {this.label = label;}
	public void setDetail(String detail) {this.detail = detail;}
	public void setValue(Float value) {this.value = value;}
	public void setRegistryDate(Date registryDate) {this.registryDate = registryDate;}

	@Override
	public String toString() {
		return "Estate [id=" + id + ", tseId=" + tseId + ", label=" + label
				+ ", detail=" + detail + ", value=" + value + ", registryDate="
				+ registryDate + "]";
	}
}
