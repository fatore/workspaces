package trash;


public class AccountabilityBinding {
	
	private String post;
	private State electionState; 
	private Town electionTown;
	private Integer electionYear;
	private Integer ballotNo;
	
	public AccountabilityBinding(String post, State electionState, Town electionTown,
			Integer electionYear, Integer ballotNo) {
		
		this.post = post;
		this.electionState = electionState;
		this.electionTown = electionTown;
		this.electionYear = electionYear;
		this.ballotNo = ballotNo;
	}

	public AccountabilityBinding(Candidature candidature) {
		
		this(candidature.getElection().getPost(),
				candidature.getElection().getState(),
				candidature.getElection().getTown(),
				candidature.getElection().getYear(),
				candidature.getBallotNo());
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ballotNo == null) ? 0 : ballotNo.hashCode());
		result = prime * result + ((electionState == null) ? 0 : electionState.hashCode());
		result = prime * result + ((electionTown == null) ? 0 : electionTown.hashCode());
		result = prime * result + ((electionYear == null) ? 0 : electionYear.hashCode());
		result = prime * result + ((post == null) ? 0 : post.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AccountabilityBinding other = (AccountabilityBinding) obj;
		if (ballotNo == null) {
			if (other.ballotNo != null) {
				return false;
			}
		} else if (!ballotNo.equals(other.ballotNo)) {
			return false;
		}
		if (electionState == null) {
			if (other.electionState != null) {
				return false;
			}
		} else if (!electionState.equals(other.electionState)) {
			return false;
		}
		if (electionTown == null) {
			if (other.electionTown != null) {
				return false;
			}
		} else if (!electionTown.equals(other.electionTown)) {
			return false;
		}
		if (electionYear == null) {
			if (other.electionYear != null) {
				return false;
			}
		} else if (!electionYear.equals(other.electionYear)) {
			return false;
		}
		if (post == null) {
			if (other.post != null) {
				return false;
			}
		} else if (!post.equals(other.post)) {
			return false;
		}
		return true;
	}
	
	
}
