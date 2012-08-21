package br.usp.sdext.app;

import br.usp.sdext.parsers.AccountParser;
import br.usp.sdext.parsers.CandidateParser;
import br.usp.sdext.parsers.CandidatureParser;
import br.usp.sdext.parsers.ElectionParser;
import br.usp.sdext.parsers.EstateParser;
import br.usp.sdext.parsers.LocationParser;


public class App {
	
	public static void main(String[] args) throws Exception {

		LocationParser locationParser = new LocationParser();

		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/reg.csv", "region");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/uf.csv", "state");
		locationParser.addSpecialValues();
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/meso.csv", "meso");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/micro.csv", "micro");
		locationParser.parseFile("/home/fm/work/data/sdext/datasus/ut/mun.csv", "town");

		ElectionParser electionParser = new ElectionParser(locationParser);

		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2012");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2010");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2008");
		electionParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/vagas/2006");
		electionParser.addSpecialValues();

		CandidateParser candidateParser = new CandidateParser(locationParser);

		CandidatureParser candidatureParser = new CandidatureParser(locationParser, electionParser, 
				candidateParser);

		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2006");
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2008");
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2010");
		candidatureParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/candidaturas/2012");

		EstateParser estateParser = new EstateParser(locationParser, candidateParser, candidatureParser);

		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2006");
		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2008");
		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2010");
		estateParser.parse("/home/fm/work/data/sdext/eleitorais/candidatos/bens/2012");

		AccountParser accountParser = new AccountParser(locationParser, candidateParser,
				candidatureParser, electionParser);

		accountParser.parse("/home/fm/work/data/sdext/eleitorais/prestacao_contas/2006/candidato");
		accountParser.parse("/home/fm/work/data/sdext/eleitorais/prestacao_contas/2008/candidato");
		accountParser.parse("/home/fm/work/data/sdext/eleitorais/prestacao_contas/2010/candidato");

		locationParser.save();
		electionParser.save();
		accountParser.save();
		estateParser.save();
		candidateParser.save();
		candidatureParser.save();
	}
}
