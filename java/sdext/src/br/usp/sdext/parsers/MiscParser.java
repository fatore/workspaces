package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;

public class MiscParser extends ModelParser {
	
	private int year;
	
	private HashMap<Model, Model> statesMap = new HashMap<>();
	private HashMap<Model, Model> townsMap = new HashMap<>();
	
	public void setYear(int year) {this.year = year;}
	public int getYear() {return year;}
	
	public HashMap<Model, Model> getStatesMap() {return statesMap;}
	public HashMap<Model, Model> getTownsMap() {return townsMap;}
	
	@Override
	public Model parse(String[] pieces) throws Exception {
		return null;
	}
	
	@Override
	public void save() {

		Model.bulkSave(statesMap.values());
		Model.bulkSave(townsMap.values());
	}
}
