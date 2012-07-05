/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.jung.mst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fm
 */
public class GraphCreator {

    /**
	 */
    HashMap<Transition, Edge> transitions;
    /**
	 */
    int edgesCounter;

    public void readEdges(String minFile, String maxFile) throws IOException {

        transitions = new HashMap<Transition, Edge>();

        BufferedReader minReader = null;
        BufferedReader maxReader = null;

        String previousMinLine = null;
        String nextMinLine = null;
        String maxLine = null;

        try {

            minReader = new BufferedReader(new FileReader(minFile));
            maxReader = new BufferedReader(new FileReader(maxFile));

            previousMinLine = minReader.readLine();

            while ((nextMinLine = minReader.readLine()) != null) {

                maxLine = maxReader.readLine();

                Transition t = new Transition(Integer.parseInt(previousMinLine.split(" ")[0]), Integer.parseInt(nextMinLine.split(" ")[0]));

                int newWeight = Integer.parseInt(maxLine.split(" ")[0]);

                //se a transicao ja existe
                if (transitions.containsKey(t)) {

                    //compare o valor da energia, se o novo valor lido for menor, substitua
                    if (newWeight < transitions.get(t).weight) {

                        //novo valor atriui-se ao peso
                        transitions.get(t).weight = newWeight;
                    }
                    transitions.get(t).incidence++;

                } //senao adicione a primeira
                else {

                    Edge e = new Edge();
                    //no inicial equivale a conformacao inicial da transicao
                    e.source = t.source;
                    //no destino equivale a conformacao destino da transicao
                    e.target = t.target;
                    //o inicial peso da aresta
                    e.weight = newWeight;
                    //incidencia inicial da aresta é 1
                    e.incidence = 1;

                    transitions.put(t, e);

                    edgesCounter++;
                }

                previousMinLine = nextMinLine;

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (minReader != null) {
                minReader.close();
            }
            if (maxReader != null) {
                maxReader.close();
            }
        }

    }

    private static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            //For Append the file.
//      OutputStream out = new FileOutputStream(f2,true);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createJungGraph(String minFilePart1, String minFilePart2, String maxFile, String outputFile, int inc) throws IOException {

        readEdges(minFilePart2, maxFile);

        File minPart1 = new File(minFilePart1).getAbsoluteFile();
        File output = new File(outputFile).getAbsoluteFile();

        InputStream in = new FileInputStream(minPart1);
        OutputStream out = new FileOutputStream(output);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();

        PrintWriter pw = null;

        pw = new PrintWriter(new FileWriter(output, true));

        pw.println("*Edges");

        for (Edge e : transitions.values()) {
            if (e.incidence > inc) {
                //!verificar se a aresta aponta para o proprio no
                if (e.source != e.target) {
                    //pw.println("\t\t\t" + e.source + "\t\t\t" + e.target + "\t\t\t" + e.weight);
                    pw.println("    " + e.source + "    " + e.target + "    " + e.weight);
                }
            }
        }
        if (pw != null) {
            pw.close();
        }
    }

    public static void main(String args[]) throws IOException {

        GraphCreator gc = new GraphCreator();
        gc.createJungGraph(
                "../Data/trimmer/res_baixa_hidrofobicidade/min.part1.dat",
                "../Data/trimmer/res_baixa_hidrofobicidade/min.part2.dat",
                "../Data/trimmer/res_baixa_hidrofobicidade/max.dat",
                "../Data/graphs/jung/res_baixa_hidrofobicidade/c-5.gph",
                5);
    }
}
