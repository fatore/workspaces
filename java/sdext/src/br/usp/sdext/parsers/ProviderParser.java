package br.usp.sdext.parsers;

import java.util.HashMap;

import br.usp.sdext.core.Model;
import br.usp.sdext.models.account.Provider;
import br.usp.sdext.util.Misc;

public class ProviderParser {

	private HashMap<Model, Model> providersMap = new HashMap<>();

	public HashMap<Model, Model> getProviderMap() {return providersMap;}

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

		Provider parsedProvider = new Provider(providerName, providerCPF);

		Provider mappedProvider = (Provider) Model.persist(parsedProvider, providersMap);

		return mappedProvider;
	}

	public void save() {

		System.out.print("\tSaving providers...");
		Model.bulkSave(providersMap.values());
		System.out.println(" Done!");
	}
}
