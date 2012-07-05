package br.usp.pf.app;

import br.usp.pf.gl.CallBacks;

import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

public class GLApp {

    public static void visualize(String file) throws Exception {

        JFrame frame = new JFrame("Protein Folding Visualization Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        //creating the projection panel
        GLCapabilities glcaps = new GLCapabilities();
        glcaps.setAccumBlueBits(16);
        glcaps.setAccumGreenBits(16);
        glcaps.setAccumRedBits(16);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        String filename = file;
        
        CallBacks viewer = new CallBacks(glcaps, filename,GL.GL_SMOOTH);

        viewer.setOpaque(true);
        frame.getContentPane().add(viewer);
        frame.setVisible(true);

    }
    
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
		
//		visualize(folder + "dmats-comparation.data");
//		visualize(folder + "static.data");
		visualize(folder + "dynamic.prj");
//		visualize(folder + "projection.data");
	}
}







