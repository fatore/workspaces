/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulovich
 */
public class CountProteinConformation {

    /**
	 */
    private int count = 0;
    /**
	 */
    private HashMap<Protein, Integer> proteins = new HashMap<Protein, Integer>();

    public void read(String filename) throws IOException {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(filename));

            Protein p = null;

            while ((p = readProtein(in)) != null) {
                if (proteins.containsKey(p)) {
                    proteins.put(p, proteins.get(p) + 1);
                } else {
                    proteins.put(p, 1);
                }

                count++;
            }

            System.out.println("number proteins: " + count);
            System.out.println("number proteins conformations: " + proteins.size());

        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(CountProteinConformation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private Protein readProtein(BufferedReader in) throws IOException {
        String line = in.readLine();

        if (line != null) {
            Protein p = new Protein();

            while (line != null && !line.startsWith("-")) {
                try {
                    StringTokenizer token = new StringTokenizer(line, " ", false);

                    String ini = token.nextToken();
                    String end = token.nextToken();

                    int index = 27 * (Integer.parseInt(ini) - 1) + (Integer.parseInt(end) - 1);
                    p.protein[index] = true;

                    line = in.readLine();
                } catch (java.util.NoSuchElementException ex) {
                    System.out.println("number proteins: " + count);
                    System.out.println("number proteins conformations: " + proteins.size());
                    throw new IOException(ex);
                }
            }

            return p;
        }

        return null;
    }

    public class Protein {

        public Protein() {
            protein = new boolean[27 * 27];
            Arrays.fill(protein, false);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Arrays.hashCode(this.protein);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }

            final Protein other = (Protein) obj;
            for (int i = 0; i < protein.length; i++) {
                if (protein[i] != other.protein[i]) {
                    return false;
                }
            }

            return true;
        }
        public boolean[] protein;
    }

    public static void main(String[] args) throws IOException {
        CountProteinConformation cpc = new CountProteinConformation();
        cpc.read("/home/paulovich/Downloads/maximos.dat");
    }
}
