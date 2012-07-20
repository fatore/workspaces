package br.usp.sdext.app;

import br.usp.sdext.parsers.MiscParser;
import br.usp.sdext.parsers.account.AccountabilityParser;
import br.usp.sdext.parsers.candidature.CandidatureParser;
import br.usp.sdext.parsers.estate.EstateParser;

public class App {
	
	public void readData() throws Exception {
		
		String baseDir;
		boolean test = false;
		
		int year = 2010;
		
		MiscParser miscParser = new MiscParser();
		
		CandidatureParser candidatureParser = new CandidatureParser(miscParser);
		EstateParser estateParser = new EstateParser(candidatureParser.getCandidateParser().getStatusMap());
		AccountabilityParser accountabilityParser = 
				new AccountabilityParser(miscParser, candidatureParser.getCandidaturesBindings());
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + year;
		candidatureParser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"bens_candidatos/" + year;
		estateParser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + "prestacao_contas/" + year + "/candidato";
		accountabilityParser.parse(baseDir);
		
		miscParser.save();
		accountabilityParser.save();
		estateParser.save();
		candidatureParser.save();
	}
	
	public void readAll() throws Exception {
	
		String baseDir;
		boolean test = false;
		
		MiscParser miscParser = new MiscParser();
		
		CandidatureParser candidatureParser = new CandidatureParser(miscParser);
		EstateParser estateParser = new EstateParser(candidatureParser.getCandidateParser().getStatusMap());
		AccountabilityParser accountabilityParser = 
				new AccountabilityParser(miscParser, candidatureParser.getCandidaturesBindings());
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2012;
		candidatureParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2010;
		candidatureParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2008;
		candidatureParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"candidatos/candidaturas/" + 2006;
		candidatureParser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"bens_candidatos/" + 2012;
		estateParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"bens_candidatos/" + 2010;
		estateParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"bens_candidatos/" + 2008;
		estateParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + ((test) ? "testes/" : "") + 
				"bens_candidatos/" + 2006;
		estateParser.parse(baseDir);
		
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + "prestacao_contas/" + 2010 + "/candidato";
		accountabilityParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + "prestacao_contas/" + 2008 + "/candidato";
		accountabilityParser.parse(baseDir);
		baseDir = "/home/fm/work/data/sdext/eleitorais/" + "prestacao_contas/" + 2006 + "/candidato";
		accountabilityParser.parse(baseDir);
		
		miscParser.save();
		accountabilityParser.save();
		estateParser.save();
		candidatureParser.save();
	}
	
	public static void main(String[] args) throws Exception {
		
		App app = new App();
		app.readAll();
	}
}
