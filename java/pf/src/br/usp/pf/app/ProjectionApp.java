package br.usp.pf.app;

import java.io.File;
import java.io.IOException;

import matrix.AbstractMatrix;
import projection.model.ProjectionModelComp;
import projection.view.ProjectionFrameComp;
import br.usp.pf.projection.FastForceScheme;
import br.usp.pf.projection.ForceScheme;
import distance.DistanceMatrix;

public class ProjectionApp {

	public static void project(String dmatFile, int printInterval, boolean fast) throws IOException {
		
		DistanceMatrix dmat = new DistanceMatrix(dmatFile);
		AbstractMatrix projection = null;
		
		if (fast) {
			FastForceScheme ffs = new FastForceScheme();
			ffs.setPrintInterval(printInterval);
			projection = ffs.project(dmat);
		} else {
			ForceScheme fs = new ForceScheme();
			fs.setPrintInterval(printInterval);
			projection = fs.project(dmat);
		}
		
		File file = new File(dmatFile);
		String folder = file.getParentFile().getParentFile().getPath() + "/projections/";
		new File(folder).mkdirs();
		projection.save(folder + file.getName().split("\\.")[0] + ".prj");
		
		ProjectionModelComp model = new ProjectionModelComp();;
		ProjectionFrameComp frame = new ProjectionFrameComp();;
		
		model.input(projection);
		model.execute();

		frame.setTitle(dmatFile);
		frame.input(model.output());
		frame.execute();
		
	}

	public static void main(String[] args) throws IOException {
		String sequence = "43157";
		int gaps = 1000;
		int cut = 3;
		
		String cutString;
		if (cut > 0) {
			cutString = "cut" + cut;
		} else {
			cutString = "full";
		}
		
		String folder;
		
		// static
		folder = "../data/" + sequence + "/" + gaps + "/" + cutString + "/dmats/";
//		ProjectionApp.project(folder + "static.dmat", 1000, false);
		ProjectionApp.project(folder + "dynamic.dmat", 100000, false);
//		ProjectionApp.project(folder + "dmats-comparation.dmat", 1000000, false);
//		ProjectionApp.project(folder + "projections-comparation.dmat", 1000000, false);
		
		// dynamic
//		folder = "/work1/wokspace/pf/data/" + sequence + "/dinamico/" + gaps + "/" + cutString + "/";
		
	}
}
