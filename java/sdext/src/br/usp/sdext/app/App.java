package br.usp.sdext.app;

import br.usp.sdext.parsers.AccountabilityParser;
import br.usp.sdext.parsers.CandidatureParser;
import br.usp.sdext.parsers.MiscParser;

public class App {
	
	public void readData() throws Exception {
		
		String baseDir;
		boolean test = false;
		
		int year = 2008;
		
		MiscParser miscParser = new MiscParser();
		
		CandidatureParser candidatureParser = new CandidatureParser(miscParser);
		AccountabilityParser accountabilityParser = 
				new AccountabilityParser(miscParser, candidatureParser.getCandidaturesBindings());
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + year;
		candidatureParser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"prestacao_contas/" + year + "/candidato";
		accountabilityParser.parse(baseDir);
		
		miscParser.save();
		accountabilityParser.save();
		candidatureParser.save();
	}
	
	public static void main(String[] args) throws Exception {
		
		App app = new App();
		app.readData();
	}
}
