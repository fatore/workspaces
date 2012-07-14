package br.usp.sdext.models;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

@Entity
public class State extends Model implements Serializable {
	
	private static final long serialVersionUID = 6358274013517612742L;
	
	private static final String[] values = {

		"AC","AL","AM","AP","BA","CE","DF","ES","GO",
		"MA","MG","MS","MT","PA","PB","PE","PI","PR",
		"RJ","RN","RO","RR","RS","SC","SE","SP","TO",
		"BR", "VT", "ZZ"
	};
	
	@Id
	private Long id;
	
	private String label;
	
	public State() {}
	
	public State(String label) throws Exception {
		
		if (label == null) {
			throw new Exception();
		}
		this.id = null;
		this.label = label;
	}

	public static HashMap<Model, Model> init() {
		
		HashMap<Model, Model> map = new HashMap<>();

		if (Model.numElements(State.class) == 0) {

			for (String value : values) {	

				try {
					
					State state = new State(value);
					state.setId(new Long(map.size()));
					map.put(state, state);
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Model.bulkSave(map.values());

		} 
		else {
			
			Model.findAll(State.class, map);
		}
		
		return map;
	}
	
	public Long getId() {return id;}
	public String getLabel() {return label;}

	public void setId(Long id) {this.id = id;}
	public void setLabel(String label) {this.label = label;}
	
	@Override
	public String toString() {
		return "State [id=" + id + ", label=" + label + "]";
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		State other = (State) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}
