package br.usp.pf.gl;

import graph.model.Edge;
import graph.util.Delaunay;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.usp.pf.gl.core.Triangle;
import br.usp.pf.gl.core.Vertex;
import br.usp.pf.util.colors.scales.ColorScale;
import br.usp.pf.util.colors.scales.PseudoRainbowScale;
import datamining.neighbors.Pair;

/**
 *
 * @author Fatore
 */
public class ProjectionLoader {

    private Vertex[] vertices;
    private int verticesCounter;

    public int loadVertices(String inputFile) throws Exception {

        BufferedReader in = null;

        in = new BufferedReader(new FileReader(inputFile));

        String curLine = null;

        StringTokenizer token = null;

        // ignore first line, DY
        in.readLine();

        // get number of states (vertices)
        curLine = in.readLine();
        token = new StringTokenizer(curLine);
        this.verticesCounter = Integer.parseInt(token.nextToken());

        // ignore next 2 lines
        in.readLine(); in.readLine();

        // read vertices
        vertices = new Vertex[verticesCounter];
        int i = 0;
        while ((curLine = in.readLine()) != null) {
            token = new StringTokenizer(curLine, ";");

            Vertex v = new Vertex(
                    Integer.parseInt(token.nextToken()),
                    Float.parseFloat(token.nextToken()),
                    Float.parseFloat(token.nextToken()),
                    Float.parseFloat(token.nextToken()));
            vertices[i++] = v;
        }

        in.close();

        float[][] minsAndMaxs = calculateMinMax();
        
        for (Vertex v : vertices) {
            v.normalize(minsAndMaxs);
        }
        
        minsAndMaxs = calculateMinMax();
        calculateColors(minsAndMaxs[0][2], minsAndMaxs[1][2]);

        return verticesCounter;
    }
    
    public Triangle[] createTriangles(int mode) {

        float[][] points = new float[verticesCounter][];
        for (int i = 0; i < verticesCounter; i++) {
            points[i] = new float[2];
            points[i][0] = vertices[i].getX();
            points[i][1] = vertices[i].getY();
        }

        long start = System.currentTimeMillis();

        // creating the Delaunay triangulation
        float[] dtpoints = new float[points.length * 2];

        for (int i = 0; i < dtpoints.length; i += 2) {
            dtpoints[i] = points[i / 2][0];
            dtpoints[i + 1] = points[i / 2][1];
        }

        int[] ed = Delaunay.triangulate(dtpoints);

        Triangle[] triangles = new Triangle[(int) ((ed.length / 3))];

        int index = 0;
        for (int i = 0; i < ed.length; i++) {
            Triangle t = new Triangle(
                    this.vertices[(ed[i++] - 3)],
                    this.vertices[(ed[i++] - 3)],
                    this.vertices[(ed[i] - 3)]);

            t.calculateNormalVector();

            triangles[index++] = t;
        }

        //smooth mode
        if (mode == 1) {
            //repeat as many times as necessary
//            smoothVertices(points, ed);
            //calculate the normal vector for each vertice
            calculateVerticesNormals(triangles);
        }

        long finish = System.currentTimeMillis();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Delaunay time: {0}s", (finish - start) / 1000.0f);

        return triangles;
    }

    private void calculateVerticesNormals(Triangle[] triangles) {
        //run over all vertices searching for related triangles
        for (Vertex v: vertices) {
            ArrayList<float[]> vnvs = new ArrayList<float[]>();
            for (Triangle t : triangles) {
                for (Vertex tv : t.getVertices()) {
                    if (v.getId() == tv.getId()) {
                        vnvs.add(t.getNormalVector());
                    }
                }
            }
            float[][] verticeNormal = new float[vnvs.size()][];
            vnvs.toArray(verticeNormal);
            v.setNormalVector(verticeNormal);
        }
    }

    private void calculateColors(float min, float max) {
    	final ColorScale colorScale = new PseudoRainbowScale();        
        colorScale.setMinMax(min, max);
        colorScale.setReverse(false);
        
        for (Vertex v : vertices) {
            v.color = colorScale.getColor(v.getEnergy());
        }
    }

    private void smoothVertices(float[][] points,int[] ed) {
        ArrayList<ArrayList<Pair>> neigh_aux = new ArrayList<ArrayList<Pair>>();

        for (int i = 0; i < points.length; i++) {
            neigh_aux.add(new ArrayList<Pair>());
        }

        for (int i = 0; i < ed.length; i++) {
            long v1 = ed[i++] - 3;
            long v2 = ed[i++] - 3;
            long v3 = ed[i] - 3;

            neigh_aux.get((int) v1).add(new Pair((int) v2, Edge.NO_SIZE));
            neigh_aux.get((int) v1).add(new Pair((int) v3, Edge.NO_SIZE));
            neigh_aux.get((int) v2).add(new Pair((int) v3, Edge.NO_SIZE));
        }

        Pair[][] neighborhood = new Pair[points.length][];

        for (int i = 0; i < neigh_aux.size(); i++) {
            neighborhood[i] = new Pair[neigh_aux.get(i).size()];

            for (int j = 0; j < neigh_aux.get(i).size(); j++) {
                neighborhood[i][j] = neigh_aux.get(i).get(j);
            }
        }

        for (int i = 0; i < neighborhood.length; i++) {
            Vertex[] vts = new Vertex[neighborhood[i].length];
            for (int j = 0; j < neighborhood[i].length; j++) {
                vts[j] = this.vertices[neighborhood[i][j].index];
            }
            this.vertices[i].setSmoothVertex(vts);
        }

        for (Vertex v : vertices) {
            v.smooth();
        }
    }

    private float[][] calculateMinMax() {

        float min_x = vertices[0].getX();
        float min_y = vertices[0].getY();
        float min_e = vertices[0].getEnergy();

        float max_x = vertices[0].getX();
        float max_y = vertices[0].getY();
        float max_e = vertices[0].getEnergy();

        for (Vertex _v : vertices) {
            if (_v.getX() < min_x) {min_x = _v.getX();}
            if (_v.getY() < min_y) {min_y = _v.getY();}
            if (_v.getEnergy() < min_e) {min_e = _v.getEnergy();}

            if (_v.getX() > max_x) {max_x = _v.getX();}
            if (_v.getY() > max_y) {max_y = _v.getY();}
            if (_v.getEnergy() > max_e) {max_e = _v.getEnergy();}
        }

        float[] min = {min_x, min_y, min_e};
        float[] max = {max_x, max_y, max_e};

        float[][] r = {min, max};

        return r;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public int getVerticesCounter() {
        return verticesCounter;
    }

    public Vertex getVertex(int index) {
        return vertices[index];
    }

}