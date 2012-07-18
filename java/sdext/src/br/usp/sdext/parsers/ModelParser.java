package br.usp.sdext.parsers;

import br.usp.sdext.core.Model;

public abstract class ModelParser {
	
	abstract public Model parse(String[] pieces) throws Exception;
	abstract public void save();

}
