package br.usp.pf.gl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import br.usp.pf.gl.core.Triangle;
import br.usp.pf.gl.core.Vertex;

public class JsonWriter  {

    private ProjectionLoader loader;
    private Triangle[] triangles;

    // load mode -> 1 = smooth, 0 = normal
    public JsonWriter(String filename) throws Exception {

        loader = new ProjectionLoader();
        if (loader.loadVertices(filename) < 1) {
            System.exit(1);
        }

        this.triangles = loader.createTriangles(1);
    }

    public void write(String filename) throws FileNotFoundException {
    	
    	PrintWriter out = new PrintWriter(new File(filename));
    	
    	out.println("{");
    	
    	out.print("\"vertexPositions\" : ");
    	out.print("[");
    	Triangle triangle;
    	int i;
    	for(i = 0; i < triangles.length - 1; i++) {
    		triangle = triangles[i];
            for (Vertex vertex : triangle.getVertices()) {
            	out.print(vertex.getX() + "," + vertex.getEnergy() + "," + vertex.getY() + ",");
            }
        }
    	triangle = triangles[i];
    	Vertex vertex;
        for (i = 0; i< triangle.getVertices().length - 1; i++ ){
        	vertex = triangle.getVertices()[i];
        	out.print(vertex.getX() + "," + vertex.getEnergy() + "," + vertex.getY() + ",");
        }
        vertex = triangle.getVertices()[i];
    	out.print(vertex.getX() + "," + vertex.getEnergy() + "," + vertex.getY());
    	
    	out.print("]");
    	out.println();
        
//    	out.println("\"vertexNormals\" :");
//    	out.print("[");
//    	for (Triangle t : triangles) {
//            for (Vertex v : t.getVertices()) {
//                float[] nv = v.getNormalVector();
//                gl.glNormal3f(-nv[0], -nv[1], -nv[2]);
//            }
//        }
//    	out.print("]");
//    	out.println();
        
//    	out.println("\"vertexMaterial\" :");
//    	out.print("[");
//    	for (Triangle t : triangles) {
//            gl.glBegin(GL.GL_TRIANGLES);
//            for (Vertex v : t.getVertices()) {
//                setMaterial(drawable, v);
//            }
//        }
//    	out.print("]");
//    	out.println();
    	
    	out.println("}");
    	
    	if (out != null) {
    		out.close();
    	}

    }
    
//    private void setMaterial(GLAutoDrawable glad, Vertex v) {
//        GL gl = glad.getGL();
//        
//        float[] mat = v.color.getColorComponents(null);
//        
//        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat, 0);
//        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat, 0);
//        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat, 0);
//    }

    public static void main(String[] args) throws Exception {
    	
    	String sequence = "43157";
		int gaps = 1000;
		int cut = 3;
		
		String cutString;
		if (cut > 0) {
			cutString = "cut" + cut;
		} else {
			cutString = "full";
		}
		
		String folder = "../data/" + sequence + "/" + gaps + "/" + cutString + "/projections/";
    	
    	JsonWriter jsonWriter = new JsonWriter(folder + "dynamic.prj");
    	jsonWriter.write(folder + "funnel.json");
    	
    	System.out.println("Finished.");
    	
    }
    
}




















