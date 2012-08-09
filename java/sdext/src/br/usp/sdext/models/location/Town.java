package br.usp.sdext.models.location;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Town extends Model implements Serializable {

	private static final long serialVersionUID = -1778986467262553206L;

	@Id
	private Long id;
	
	public Integer ibgeCode;
	public Integer ibgeCodeVD;
	
	public String status;
	
	public Integer sinpasCode;
	public Integer siafiCode;
	
	public String name;
	public String namex;
	
	public String obs;
	
	public String altCode;
	public String altCodeVD;
	
	public Boolean legalAmazon;
	public Boolean border;
	public Boolean capital;
	
	@ManyToOne
	private State state;
	
	@ManyToOne
	private MesoRegion mesoRegion;
	
	@ManyToOne
	private MicroRegion microRegion;
	
	public Float latitude;
	public Float longetude;
	
	public Float altitude;
	public Float area;
	
	public Town(Integer ibgeCode, Integer ibgeCodeVD, String status,
			Integer sinpasCode, Integer siafiCode, String name, String namex,
			String obs, String altCode, String altCodeVD, Boolean legalAmazon,
			Boolean border, Boolean capital, State state, 
			MesoRegion mesoRegion, MicroRegion microRegion, Float latitude,
			Float longetude, Float altitude, Float area) {
		
		this.ibgeCode = ibgeCode;
		this.ibgeCodeVD = ibgeCodeVD;
		this.status = status;
		this.sinpasCode = sinpasCode;
		this.siafiCode = siafiCode;
		this.name = name;
		this.namex = namex;
		this.obs = obs;
		this.altCode = altCode;
		this.altCodeVD = altCodeVD;
		this.legalAmazon = legalAmazon;
		this.border = border;
		this.capital = capital;
		this.state = state;
		this.mesoRegion = mesoRegion;
		this.microRegion = microRegion;
		this.latitude = latitude;
		this.longetude = longetude;
		this.altitude = altitude;
		this.area = area;
	}

	public Long getId() {return id;}
	public State getState() {return state;}
	public MesoRegion getMesoRegion() {return mesoRegion;}
	public MicroRegion getMicroRegion() {return microRegion;}

	public void setId(Long id) {this.id = id;}
	public void setState(State state) {this.state = state;}
	public void setMesoRegion(MesoRegion mesoRegion) {this.mesoRegion = mesoRegion;}
	public void setMicroRegion(MicroRegion microRegion) {this.microRegion = microRegion;}

	@Override
	public String toString() {
		
		return "Town [id=" + id + ", ibgeCode=" + ibgeCode + ", ibgeCodeVD="
				+ ibgeCodeVD + ", status=" + status + ", sinpasCode="
				+ sinpasCode + ", siafiCode=" + siafiCode + ", name=" + name
				+ ", namex=" + namex + ", obs=" + obs + ", altCode=" + altCode
				+ ", altCodeVD=" + altCodeVD + ", legalAmazon=" + legalAmazon
				+ ", border=" + border + ", capital=" + capital + ", state="
				+ state + ", mesoRegion=" + mesoRegion + ", microRegion="
				+ microRegion + ", latitude=" + latitude + ", longetude="
				+ longetude + ", altitude=" + altitude + ", area=" + area + "]";
	}
}
