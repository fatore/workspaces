package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;
import br.usp.sdext.util.Misc;

@Entity
public class Election extends Model implements Serializable {
	
	private static final long serialVersionUID = 5869393024124951125L;

	@Id
	private Long id;

	@Column(nullable=false)
	private Integer year;
	private String description;	
	
	@Column(nullable=false)
	private Integer round;	
	
	@Column(nullable=false)
	private String uf;
	
	@Column(nullable=false)
	private String ueID;
	private String ue;
	
	@Column(nullable=false)
	private Long postID;
	private String post;
	
	public Election() {}
	
	public Election(Integer year, Integer round, String uf, String ueID, Long postID,
			String description, String ue, String post) {
		this.year = year;
		this.round = round;
		this.uf = uf;
		this.ueID = ueID;
		this.postID = postID;
		this.description = description;
		this.ue = ue;
		this.post = post;
	}

	// getters
	public Long getID() {return id;}
	public Integer getYear() {return year;}
	public Integer getRound() {return round;}
	public String getUf() {return uf;}
	public String getUeID() {return ueID;}
	public Long getPostID() {return postID;}
	public String getDescription() {return description;}
	public String getUe() {return ue;}
	public String getPost() {return post;}
	
	// setters
	public void setId(Long id) {this.id = id;}
	public void setYear(Integer year) {this.year = year;}
	public void setRound(Integer round) {this.round = round;}
	public void setUf(String uf) {this.uf = uf;}
	public void setUeID(String ueID) {this.ueID = ueID;}
	public void setPostID(Long postID) {this.postID = postID;}
	public void setDescription(String description) {this.description = description;}
	public void setUe(String ue) {this.ue = ue;}
	public void setPost(String post) {this.post = post;}
	
	@Override
	public String toString() {
		
		return year + ", " + description + "," + uf + ", " + ue + ", " + post;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postID == null) ? 0 : postID.hashCode());
		result = prime * result + ((round == null) ? 0 : round.hashCode());
		result = prime * result + ((ueID == null) ? 0 : ueID.hashCode());
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
		if (ueID == null) {
			if (other.ueID != null)
				return false;
		} else if (!ueID.equals(other.ueID))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	
	public static Election parse(String[] pieces) {
		
		Integer year = Misc.parseInt(pieces[2]);
		Integer round = Misc.parseInt(pieces[3]);	
		String description = Misc.parseStr(pieces[4]);
		String uf = Misc.parseStr(pieces[5]);
		String ueID = Misc.parseStr(pieces[6]);
		String ue = Misc.parseStr(pieces[7]);
		Long postID = Misc.parseLong(pieces[8]);
		String post = Misc.parseStr(pieces[9]);
		
		return new Election(year, round, uf, ueID, postID, description, ue, post);
		
	}
}
