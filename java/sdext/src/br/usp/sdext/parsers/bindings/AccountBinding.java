package br.usp.sdext.parsers.bindings;

import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.election.Election;

public class AccountBinding {
	
	private Integer ballotNo;
	private Election election;
	
	public AccountBinding(Integer ballotNo, Election election) {
		
		this.ballotNo = ballotNo;
		this.election = election;
	}

	public AccountBinding(Candidature candidature) {
		
		this(candidature.getBallotNo(), candidature.getElection());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ballotNo == null) ? 0 : ballotNo.hashCode());
		result = prime * result + ((election == null) ? 0 : election.hashCode());
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
		AccountBinding other = (AccountBinding) obj;
		if (ballotNo == null) {
			if (other.ballotNo != null)
				return false;
		} else if (!ballotNo.equals(other.ballotNo))
			return false;
		if (election == null) {
			if (other.election != null)
				return false;
		} else if (!election.equals(other.election))
			return false;
		return true;
	}
	
	
}
