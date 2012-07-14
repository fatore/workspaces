package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.usp.sdext.core.Model;

@Entity
public class Election extends Model implements Serializable {
	
	private static final long serialVersionUID = 5869393024124951125L;
	public static final int FIRST_MAIN_ELECTION = 1994;

	@Id
	private Long id;

	@Column(nullable=false)
	private Integer year;
	private String description;	
	
	@Column(nullable=false)
	private Integer round;	
	
	@ManyToOne
	private State state;
	
	@ManyToOne
	private Town town;
	
	@Column(nullable=false)
	private Long postID;
	private String post;
	
	public Election() {}
	
	public Election(Integer year, Integer round, Long postID, 
			String post, String description) throws Exception {
		
		if (year == null || round == null || postID == null) {
			throw new Exception();
		}
		
		this.year = year;
		this.round = round;
		this.postID = postID;
		this.description = description;
		this.post = post;
	}

	// getters
	public Long getID() {return id;}
	public Integer getYear() {return year;}
	public Integer getRound() {return round;}
	public Long getPostID() {return postID;}
	public String getDescription() {return description;}
	public String getPost() {return post;}
	public State getState() {return state;}
	public Town getTown() {return town;}
	
	// setters
	public void setId(Long id) {this.id = id;}
	public void setYear(Integer year) {this.year = year;}
	public void setRound(Integer round) {this.round = round;}
	public void setPostID(Long postID) {this.postID = postID;}
	public void setDescription(String description) {this.description = description;}
	public void setState(State state) {this.state = state;}
	public void setTown(Town town) {this.town = town;}
	public void setPost(String post) {this.post = post;}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postID == null) ? 0 : postID.hashCode());
		result = prime * result + ((round == null) ? 0 : round.hashCode());
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
		if (postID == null) {
			if (other.postID != null)
				return false;
		} else if (!postID.equals(other.postID))
			return false;
		if (round == null) {
			if (other.round != null)
				return false;
		} else if (!round.equals(other.round))
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
				+ description + ", round=" + round + ", state=" + state
				+ ", town=" + town + ", postID=" + postID + ", post=" + post
				+ "]";
	}
}
