package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class State extends Model implements Serializable {

	private static final long serialVersionUID = -2413182824214875615L;

	@Id
	private Long id;
	
	private Integer ibgeCode;
	
	@Column(nullable=false)
	private String acronym;
	
	private String name;
	private String namex;
	
	private Integer sinpasCode;
	
	@ManyToOne
	private Region region;
	
	private Float area;
	
	private String status;
	
	public State() {}

	public State(Integer ibgeCode, String acronym, String name, String namex,
			Integer sinpasCode, Region region, Float area, String status) {
		
		this.ibgeCode = ibgeCode;
		this.acronym = acronym;
		this.name = name;
		this.namex = namex;
		this.sinpasCode = sinpasCode;
		this.region = region;
		this.area = area;
		this.status = status;
	}
	
	public State(String acronym) {
		
		this.acronym = acronym;
	}

	public Long getId() {return id;}
	public Integer getIbgeCode() {return ibgeCode;}
	public String getName() {return name;}
	public String getNamex() {return namex;}
	public String getAcronym() {return acronym;}
	public String getStatus() {return status;}
	public Integer getSinpasCode() {return sinpasCode;}
	public Region getRegion() {return region;}
	public void setAcronym(String acronym) {this.acronym = acronym;}
	
	public void setId(Long id) {this.id = id;}
	public void setIbgeCode(Integer ibgeCode) {this.ibgeCode = ibgeCode;}
	public void setName(String name) {this.name = name;}
	public void setNamex(String namex) {this.namex = namex;}
	public Float getArea() {return area;}
	public void setSinpasCode(Integer sinpasCode) {this.sinpasCode = sinpasCode;}
	public void setRegion(Region region) {this.region = region;}
	public void setArea(Float area) {this.area = area;}
	public void setStatus(String status) {this.status = status;}

	@Override
	public String toString() {
		return "State [acronym=" + acronym + ", name=" + name + ", namex="
				+ namex + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acronym == null) ? 0 : acronym.hashCode());
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
		if (acronym == null) {
			if (other.acronym != null)
				return false;
		} else if (!acronym.equals(other.acronym))
			return false;
		return true;
	}
}
