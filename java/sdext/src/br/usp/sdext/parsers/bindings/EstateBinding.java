package br.usp.sdext.parsers.bindings;

import br.usp.sdext.models.candidature.Candidature;
import br.usp.sdext.models.location.State;

public class EstateBinding {
	
	private Integer year;
	private State state;
	private Long tseCode;
	
	public EstateBinding(Integer year, State state, Long tseCode) {
		
		this.year = year;
		this.state = state;
		this.tseCode = tseCode;
	}

	public EstateBinding(Candidature candidature) {
		
		this(candidature.getElection().getYear(),
				candidature.getElection().getState(),
				candidature.getTseCode());
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((tseCode == null) ? 0 : tseCode.hashCode());
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
		EstateBinding other = (EstateBinding) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (tseCode == null) {
			if (other.tseCode != null)
				return false;
		} else if (!tseCode.equals(other.tseCode))
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
		
		return "EstateBinding [year=" + year + ", state=" + state.getAcronym()
				+ ", tseCode=" + tseCode + "]";
	}
	
	
}
