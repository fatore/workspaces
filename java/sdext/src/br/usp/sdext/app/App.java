package br.usp.sdext.app;

import br.usp.sdext.parsers.AbstractParser;
import br.usp.sdext.parsers.AccountabilityParser;
import br.usp.sdext.parsers.CandidaturesParser;

public class App {
	
	public void readData() throws Exception {
		
		AbstractParser parser;
		String baseDir;
		
		int year = 2010;
		
		boolean test = false;
		
		parser = new CandidaturesParser();
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes" : "") + 
				"/candidatos/candidaturas/" + year;
		parser.parseAndSave(baseDir);
		
		parser = new AccountabilityParser();
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes" : "") + 
				"/prestacao_contas/" + year;
		parser.parseAndSave(baseDir);
	}
	
	public static void main(String[] args) throws Exception {
		
		App app = new App();
		app.readData();
	}
}
