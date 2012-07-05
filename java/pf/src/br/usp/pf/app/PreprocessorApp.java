package br.usp.pf.app;

import br.usp.pf.preprocess.Preprocessor;

public class PreprocessorApp {
	
	public static void preprocess(String input, String output, int cut, boolean build) throws Exception {

        Preprocessor pp = new Preprocessor(input, output, cut);
        pp.process(build);
    }
	
	public static void main(String[] args) throws Exception {
		String sequence = "0012";
		int gaps = 1000;
		int cut = 3;
		
		String folder = "../data/" + sequence + "/" + gaps + "/";
		Preprocessor pp = new Preprocessor(folder + "minimo.dat", folder, cut);
        pp.process(true);
        pp.processRuns(folder + "runs.dat");
	}
}
