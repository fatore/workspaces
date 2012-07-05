package br.usp.pf.preprocess;

import br.usp.pf.core.Conformation;

public class State implements Comparable<State> {

    private int id;
	private int incidence;
    private int energy;
    private int noContacts;
    private Conformation conformation;
    
    public State(int id, int incidence, int energy, int contacts,
			Conformation conformation) {
		this.id = id;
		this.incidence = incidence;
		this.energy = energy;
		this.noContacts = contacts;
		this.conformation = conformation;
	}
    
	public int getId() {return id;}
    public int getIncidence() {return incidence;}
    public int getEnergy() {return energy;}
    public int getNoContacts() {return noContacts;}
    public Conformation getConformation() {return conformation;}
    
	public void setId(int key) {this.id = key;}
    public void setEnergy(int energy) {this.energy = energy;}
    public void setNoContacts(int contacts) {this.noContacts = contacts;}
    public void setIncidence(int incidence) {this.incidence = incidence;}
    public void setConformation(Conformation conformation) {this.conformation = conformation;}
    
    public void increaseIncidence() {
    	this.incidence++;
    }

	@Override
	public int compareTo(State o) {
		return energy - o.energy;
	}
}
