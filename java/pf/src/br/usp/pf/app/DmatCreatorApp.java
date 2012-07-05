package br.usp.pf.app;


import br.usp.pf.dmat.DmatCreator;

public class DmatCreatorApp {
	
	public static void createMatrix(String dyFile, String jumpsFile, String outputFolder) throws Exception {
		
		DmatCreator dmc = new DmatCreator();

		dmc.createDmat(dyFile, jumpsFile, outputFolder) ;
    }

	public static void main(String[] args) throws Exception {
		
		String sequence = "43157";
		int gaps = 30;
		int cut = 3;
		
		String cutString;
		if (cut > 0) {
			cutString = "cut" + cut;
		} else {
			cutString = "full";
		}
		
		DmatCreator dmc = new DmatCreator();
		String folder = "../data/" + sequence + "/" + gaps + "/" + cutString + "/";
		
		// static 
//		dmc.createDmat(folder + "dy_file.data", null, folder);
		
		// dynamic
		dmc.createDmat(folder + "dy_file.data", folder + "jumps_file.data", folder);
	}
}
