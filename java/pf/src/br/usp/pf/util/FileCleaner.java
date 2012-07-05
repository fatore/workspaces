package br.usp.pf.util;

import java.io.*;
import java.util.*;

/**
 *
 * @author Francisco Morgani Fatore
 */
public class FileCleaner {

    //file to be pre processed
    /**
	 */
    private String inputFile;
    
    public FileCleaner(String inputFile) {
        this.inputFile = inputFile;
    }

    private void removeLines() throws Exception {

        BufferedReader in = null;
        PrintWriter out = null;

        System.out.println("Opening file...");
        in = new BufferedReader(new FileReader(inputFile));
        out = new PrintWriter(new File(inputFile + "-c.data").getAbsoluteFile());

        String line;

        System.out.println("Cleaning");
        while ((line = in.readLine()) != null) {
        	if (!line.equals("\n") && !line.isEmpty() && !line.equals("")) {
        		out.println(line);
        	}
        }
        
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        System.out.println("Done");
    }

    public static void main(String args[]) throws Exception {

        String file = "23-09-11";

        String inputFile = "/d/ic/protein_folding/data/raw/"+ file + "/minimos.dat";

        FileCleaner fc = new FileCleaner(inputFile);
        fc.removeLines();
    }
}
