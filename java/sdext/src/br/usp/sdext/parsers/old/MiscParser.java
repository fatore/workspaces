package br.usp.sdext.parsers.old;

import java.util.HashMap;

import br.usp.sdext.core.Model;

public class MiscParser {
	
	private int year;
	
	private HashMap<Model, Model> statesMap = new HashMap<>();
	private HashMap<Model, Model> townsMap = new HashMap<>();
	
	public void setYear(int year) {this.year = year;}
	public int getYear() {return year;}
	
	public HashMap<Model, Model> getStatesMap() {return statesMap;}
	public HashMap<Model, Model> getTownsMap() {return townsMap;}
	
	public void save() {

		Model.bulkSave(statesMap.values());
		System.out.println("\tSaving states...");
		
		System.out.println("\tSaving towns...");
		Model.bulkSave(townsMap.values());
	}
}
