package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Town extends Model implements Serializable {

	private static final long serialVersionUID = -1778986467262553206L;

	@Id
	private Long id;
	
	private Integer tseCode;
	private Integer ueCode;
	
	private Integer ibgeCode;
	private Integer ibgeCodeVD;
	
	private String status;
	
	private Integer sinpasCode;
	private Integer siafiCode;
	
	private String name;
	
	@Column(nullable=false)
	private String namex;
	
	private String obs;
	
	private String altCode;
	private String altCodeVD;
	
	private Boolean legalAmazon;
	private Boolean border;
	private Boolean capital;
	
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
	
	public Town() {}
	
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

	public Town(String namex, State state) {
		
		this.namex = namex;
		this.state = state;
	}

	public Town(Integer tseCode) {
		
		this.tseCode = tseCode;
	}

	public Long getId() {return id;}
	public Integer getTseCode() {return tseCode;}
	public Integer getUeCode() {return ueCode;}
	public Integer getIbgeCode() {return ibgeCode;}
	public Integer getIbgeCodeVD() {return ibgeCodeVD;}
	public String getStatus() {return status;}
	public Integer getSinpasCode() {return sinpasCode;}
	public Integer getSiafiCode() {return siafiCode;}
	public String getName() {return name;}
	public String getNamex() {return namex;}
	public String getObs() {return obs;}
	public String getAltCode() {return altCode;}
	public String getAltCodeVD() {return altCodeVD;}
	public Boolean getLegalAmazon() {return legalAmazon;}
	public Boolean getBorder() {return border;}
	public Boolean getCapital() {return capital;}
	public State getState() {return state;}
	public MesoRegion getMesoRegion() {return mesoRegion;}
	public MicroRegion getMicroRegion() {return microRegion;}
	public Float getLatitude() {return latitude;}
	public Float getLongetude() {return longetude;}
	public Float getAltitude() {return altitude;}
	public Float getArea() {return area;}

	public void setId(Long id) {this.id = id;}
	public void setTseCode(Integer tseCode) {this.tseCode = tseCode;}
	public void setUeCode(Integer ueCode) {this.ueCode = ueCode;}
	public void setIbgeCode(Integer ibgeCode) {this.ibgeCode = ibgeCode;}
	public void setIbgeCodeVD(Integer ibgeCodeVD) {this.ibgeCodeVD = ibgeCodeVD;}
	public void setStatus(String status) {this.status = status;}
	public void setSinpasCode(Integer sinpasCode) {this.sinpasCode = sinpasCode;}
	public void setSiafiCode(Integer siafiCode) {this.siafiCode = siafiCode;}
	public void setName(String name) {this.name = name;}
	public void setNamex(String namex) {this.namex = namex;}
	public void setObs(String obs) {this.obs = obs;}
	public void setAltCode(String altCode) {this.altCode = altCode;}
	public void setAltCodeVD(String altCodeVD) {this.altCodeVD = altCodeVD;}
	public void setLegalAmazon(Boolean legalAmazon) {this.legalAmazon = legalAmazon;}
	public void setBorder(Boolean border) {this.border = border;}
	public void setCapital(Boolean capital) {this.capital = capital;}
	public void setState(State state) {this.state = state;}
	public void setMesoRegion(MesoRegion mesoRegion) {this.mesoRegion = mesoRegion;}
	public void setMicroRegion(MicroRegion microRegion) {this.microRegion = microRegion;}
	public void setLatitude(Float latitude) {this.latitude = latitude;}
	public void setLongetude(Float longetude) {this.longetude = longetude;}
	public void setAltitude(Float altitude) {this.altitude = altitude;}
	public void setArea(Float area) {this.area = area;}

	@Override
	public String toString() {
		return "Town [name=" + name + ", namex=" + namex + ", tseCode="
				+ tseCode + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((namex == null) ? 0 : namex.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		Town other = (Town) obj;
		if (namex == null) {
			if (other.namex != null)
				return false;
		} else if (!namex.equals(other.namex))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
}
