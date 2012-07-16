package br.usp.sdext.parsers;


public class Binding {

	private String post;
	private String candidateName;
	private Integer ballotNo;
	
	private Integer year;
	
	public Binding() {}
	
	public Binding(String post, String name, int ballotNo, Integer year) {

		this.post = post;
		this.candidateName = name;
		this.ballotNo = ballotNo;
		this.year = year;
	}
	
	public String getPost() {return post;}
	public String getCandidateName() {return candidateName;}
	public Integer getBallotNo() {return ballotNo;}
	public Integer getYear() {return year;}

	public void setPost(String post) {this.post = post;}
	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}
	public void setBallotNo(Integer ballotNo) {this.ballotNo = ballotNo;}
	public void setYear(Integer year) {this.year = year;}

	public boolean hasSameCandidature(Binding other) {

		if (!this.candidateName.equals(other.candidateName)) return false;
		if (!this.ballotNo.equals(other.ballotNo)) return false;
		if (!this.year.equals(other.year)) return false;
		if (!this.post.equals(other.post)) return false;
		
		return true;
	}
	@Override
	public String toString() {
		return "Binding [post=" + post + ", candidateName=" + candidateName
				+ ", ballotNo=" + ballotNo + ", year=" + year + "]";
	}
}
