package br.usp.pf.app;

public class App {

	public static void main(String[] args) {
		
		if (args.length < 2) {
			printUsage();
			System.exit(0);
		}
		String operation = args[0];
		
		try {
			if (operation.equals("--help")) {
				printUsage();
			}
			if (operation.equals("-pp")) {
				String rawFile = args[1];
				String output = args[2];
				int cut = Integer.parseInt(args[3]);
				boolean build = Boolean.parseBoolean(args[4]);
				PreprocessorApp.preprocess(rawFile, output, cut, build);
			}
			if (operation.equals("-dmat")) {
				String dyFile = args[1];
				String jumpsFile = null;
				String outputFolder = null;
				if (args.length > 3) {
					jumpsFile = args[2];
					outputFolder = args[3];
				} else {
					outputFolder = args[2];
				}
				DmatCreatorApp.createMatrix(dyFile, jumpsFile, outputFolder);
			}
			if (operation.equals("-pj")) {
				String file = args[1];
				int printInterval = Integer.parseInt(args[2]);
				boolean fast = Boolean.parseBoolean(args[3]);
				ProjectionApp.project(file, printInterval, fast);
			}
			if (operation.equals("-3d")) {
				String projFile = args[1];
				GLApp.visualize(projFile);
			}
			
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
			printUsage();
		}
	}
	
	public static void printUsage() {
		System.out.println("usage:");
		System.out.println("\t preprocess: -pp [min_file] [output_destination] [cut]:int " +
			"[build_graph]:true or false");
		System.out.println("\t create dmat: -dmat [dyFile] [jumpsFile]* [outputFolder]");
		System.out.println("\t project: -pj [dmat_file] [printInterval] [fast]:(true or false)");
		System.out.println("\t visualize: -3d [projection_file] ");
		System.out.println("\t this message: --help ");
	}
}
