package br.usp.pf.gl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import br.usp.pf.gl.core.Triangle;
import br.usp.pf.gl.core.Vertex;

public class JsonWriter  {

    private ProjectionLoader loader;
    private Triangle[] triangles;

    public JsonWriter(String filename) throws Exception {

        loader = new ProjectionLoader();
        if (loader.loadVertices(filename) < 1) {
            System.exit(1);
        }

        this.triangles = loader.createTriangles(1);
    }

    public void write(String filename) throws FileNotFoundException {
    	
    	PrintWriter out = new PrintWriter(new File(filename));
    	
    	String positions = "\t\"vertexPositions\" : [";
    	
    	int i = 0;
    	int j = 1;
    	
    	Vertex vertex = triangles[0].getVertices()[0];
    	
    	positions += (vertex.getX() + ", " + vertex.getEnergy() + ", " + vertex.getY());
    	
    	for (; i < triangles.length; i++) {
    		Vertex[] vertices = triangles[i].getVertices();
    		for (; j < vertices.length; j++) {
    			
    			vertex = vertices[0];
    			positions += (", " + vertex.getX() + ", " + vertex.getEnergy() + ", " + vertex.getY());
    		}
    		j = 0;
    	}
    	positions += "],\n";
    	
    	String normals = "\t\"vertexNormals\" : [";
    	String textCoords = "\t\"vertexTextureCoords\" : [";
    	String indices = "\t\"indices\" : [";
    	
    	String output = "{\n" + positions + "}";
    	
    	out.print(output);
    	
    	
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
		
		String folder = "/home/fm/work/data/pf/" + sequence + "/" + gaps + "/" + cutString + "/projections/";
    	
    	JsonWriter jsonWriter = new JsonWriter(folder + "dynamic.prj");
    	jsonWriter.write(folder + "funnel.json");
    	
    	System.out.println("Finished.");
    	
    }
    
}




















