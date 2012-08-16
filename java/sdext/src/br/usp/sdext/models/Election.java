package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Election extends Model implements Serializable {
	
	private static final long serialVersionUID = 776526731178895284L;
	public static final int FIRST_MAIN_ELECTION = 1994;

	@Id
	private Long id;

	@Column(nullable=false)
	private Integer year;
	
	private String description;	
	
	@ManyToOne
	private State state;
	
	@ManyToOne
	private Town town;
	
	@Column(nullable=false)
	private Integer postCode;
	
	@Column(nullable=false)
	private String post;
	
	@Column(nullable=false)
	private Integer noJobs;
	
	public Election() {}

	public Election(Integer year, String description, State state, Town town,
			Integer postCode, String post, Integer noJobs) {
		
		this.year = year;
		this.description = description;
		this.state = state;
		this.town = town;
		this.postCode = postCode;
		this.post = post;
		this.noJobs = noJobs;
	}

	public Election(Integer year, State state, Town town, Integer postCode) {
		
		this.year = year;
		this.state = state;
		this.town = town;
		this.postCode = postCode;
	}
	
	// getters
	public Long getID() {return id;}
	public Integer getYear() {return year;}
	public Integer getPostCode() {return postCode;}
	public String getDescription() {return description;}
	public String getPost() {return post;}
	public State getState() {return state;}
	public Town getTown() {return town;}	
	public Integer getNoJobs() {return noJobs;}


	// setters
	public void setId(Long id) {this.id = id;}
	public void setYear(Integer year) {this.year = year;}
	public void setPostCode(Integer postCode) {this.postCode = postCode;}
	public void setDescription(String description) {this.description = description;}
	public void setState(State state) {this.state = state;}
	public void setTown(Town town) {this.town = town;}
	public void setPost(String post) {this.post = post;}
	public void setNoJobs(Integer noJobs) {this.noJobs = noJobs;}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postCode == null) ? 0 : postCode.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((town == null) ? 0 : town.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		Election other = (Election) obj;
		if (postCode == null) {
			if (other.postCode != null)
				return false;
		} else if (!postCode.equals(other.postCode))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (town == null) {
			if (other.town != null)
				return false;
		} else if (!town.equals(other.town))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Election [id=" + id + ", year=" + year + ", description="
				+ description + ", state=" + state + ", town=" + town
				+ ", postID=" + postCode + ", post=" + post + ", noJobs="
				+ noJobs + "]";
	}
}
