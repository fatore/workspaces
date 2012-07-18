package br.usp.sdext.app;

import br.usp.sdext.parsers.AbstractParser;
import br.usp.sdext.parsers.MainParser;

public class App {
	
	public void readData() throws Exception {
		
		AbstractParser parser;
		String baseDir;
		
		int year = 2012;
		
		boolean test = false;
		
		parser = new MainParser();
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2006;
//		parser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2010;
		parser.parse(baseDir);
		
		parser.save();
	}
	
	public static void main(String[] args) throws Exception {
		
		App app = new App();
		app.readData();
	}
}
