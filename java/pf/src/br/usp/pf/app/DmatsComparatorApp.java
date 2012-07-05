package br.usp.pf.app;


import java.util.ArrayList;

import br.usp.pf.util.DmatsComparator;

public class DmatsComparatorApp {
	
	public static void createMatrix(String file1, String file2) throws Exception {
		
		DmatsComparator dmc = new DmatsComparator();

		dmc.compareDmats(file1, file2);
		
    }

	public static void main(String[] args) throws Exception {
		
		ArrayList<String> sequences = new ArrayList<String>();
		sequences.add("0012");
		sequences.add("0012-frust");
		sequences.add("2221");
		sequences.add("43157");
		sequences.add("45568D");
		
		int gaps = 1000;
		int cut = 0;
		
		String cutString;
		if (cut > 0) {
			cutString = "cut" + cut;
		} else {
			cutString = "full";
		}
		String folder;
		
		DmatsComparator dmc = new DmatsComparator();		
		String path = "/home/fatore/workspace/pf/data/";
		
		// compare
		for (int i = 0; i < sequences.size(); i++) {
			folder = path + sequences.get(i) + "/" + gaps + "/" + cutString + "/dmats/";
			dmc.compareDmats(folder + "static.dmat", folder + "dynamic.dmat");
		}
		
		// synchronize
		for (int i = 0; i < sequences.size(); i++) {
			folder = path + sequences.get(i) + "/" + gaps + "/" + cutString + "/dmats/";
			dmc.synchronizeDistance(folder + "dmats-comparation.dmat");
		}
		
		System.out.println("Done!");
	}
}
