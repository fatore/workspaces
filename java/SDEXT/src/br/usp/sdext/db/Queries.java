package br.usp.sdext.db;

import java.util.*;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidate.Candidate;
import br.usp.sdext.models.candidate.Sex;


public class Queries {

	public static void main(String[] args) {
		
		HashMap<Model, Model> map = new HashMap<>();
		Model.findAll(Sex.class, map);
		
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





