package br.usp.sdext.db;

import java.util.*;

import trash.Candidate;
import trash.Sex;

import br.usp.sdext.core.Model;


public class Queries {

	public static void main(String[] args) {
		
		HashMap<Model, Model> map = Model.findAllMap(Sex.class);
		
		Candidate c = new Candidate();
		c.setId(0L);
		c.setName("teste");
		
		Sex sex = new Sex();
		sex.setLabel("MASCULINO");
		
		Sex mapped = (Sex) map.get(sex);
		mapped.setTseId(1L);
		
		c.setSex(mapped);
		
		c.save();
		
	}
}





