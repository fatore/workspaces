package br.usp.sdext.parsers.account;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.candidature.Provider;
import br.usp.sdext.parsers.ModelParser;
import br.usp.sdext.util.Misc;

public class ProviderParser extends ModelParser {

	private HashMap<Model, Model> providerMap = new HashMap<>();

	public HashMap<Model, Model> getProviderMap() {return providerMap;}

	public Model parse(String[] pieces, int year) throws Exception {

		// Parse provider.
		Long providerCPF = null;
		String providerName = null;

		switch (year) {

		case 2006:
			providerCPF = Misc.parseLong(pieces[19]);
			providerName = Misc.parseStr(pieces[18]);
			break;

		case 2008:
			providerCPF = Misc.parseLong(pieces[23]);
			providerName = Misc.parseStr(pieces[22]);
			break;

		case 2010:
			providerCPF = Misc.parseLong(pieces[10]);
			providerName = Misc.parseStr(pieces[11]);
			break;

		default:
			break;
		}

		Provider provider = new Provider(providerName, providerCPF);

		provider = (Provider) Model.fetch(provider, providerMap);

		return provider;
	}

	@Override
	public void save() {

		System.out.println("\tSaving providers...");
		Model.bulkSave(providerMap.values());

	}
}
