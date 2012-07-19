package br.usp.sdext.app;

import br.usp.sdext.parsers.AccountabilityParser;
import br.usp.sdext.parsers.CandidatureParser;
import br.usp.sdext.parsers.MiscParser;

public class App {
	
	public void readData() throws Exception {
		
		String baseDir;
		boolean test = true;
		
		int year = 2010;
		
		MiscParser miscParser = new MiscParser();
		
		CandidatureParser candidatureParser = new CandidatureParser(miscParser);
		AccountabilityParser accountabilityParser = 
				new AccountabilityParser(miscParser, candidatureParser.getCandidaturesBindings());
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + year;
		candidatureParser.parse(baseDir);
		miscParser.save();
		candidatureParser.save();
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + "prestacao_contas/" + year + "/candidato";
		accountabilityParser.parse(baseDir);
		
		accountabilityParser.save();
	}
	
	public static void main(String[] args) throws Exception {
		
		App app = new App();
		app.readData();
	}
}
