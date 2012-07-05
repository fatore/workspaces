package br.usp.pf.projection;

import distance.DistanceMatrix;
import distance.dissimilarity.AbstractDissimilarity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import br.usp.pf.projection.InitialProjection.Layout;

import matrix.AbstractMatrix;
import matrix.dense.DenseMatrix;
import matrix.dense.DenseVector;
import projection.model.ProjectionModelComp;
import projection.technique.Projection;
import projection.view.ProjectionFrameComp;

public abstract class AbstractForceScheme implements Projection {
	
	protected static Random rand = new Random(10);

	protected int[] index;
	protected int printInterval;
	protected static final float EPSILON = 0.00001f;
	protected static final float ACCEPTABLE_ERROR = 0.001f;
	protected int counter;

	ProjectionModelComp model;
	ProjectionFrameComp frame;
	AbstractMatrix finalProjection;

	public AbstractForceScheme() {
		model = new ProjectionModelComp();
		frame = new ProjectionFrameComp();
	}

	public void setPrintInterval(int printInterval) {
		this.printInterval = printInterval;
	}

	@Override
	public AbstractMatrix project(DistanceMatrix dmat) throws IOException {
		// Create the indexes and shuffle them
		index = createIndex(dmat.getElementCount());
		
		dmat.setClassData(fixCdata(dmat));
		
		// create the initial projection
		float[][] initial_proj = new InitialProjection(dmat, Layout.RADIAL).getProjection();
		
		finalProjection = createFinalProjection(initial_proj,dmat);

		model.input(finalProjection);
		model.execute();

		frame.setTitle("iteration " + counter);
		frame.input(model.output());
		frame.execute();
		
		int compsize = (int) Math.sqrt(dmat.getElementCount());
		float decfactor = 1.0f;
		
		int[] closeToCenter = createIndex(compsize/5);
		
		decfactor = (float) (Math.pow((1 + (counter * 2 + 1)), (1.0f / (counter * 2 + 1))) - 1);
		double prevDelta = iteration(dmat, initial_proj, compsize, decfactor, closeToCenter);

		for (counter = 1;; counter++) {
			// System.out.println("iteration: " + i);
			decfactor = (float) (Math.pow((1 + (counter * 2 + 1)), (1.0f / (counter * 2 + 1))) - 1);
//		    decfactor = 1/8f;
			
			double newDelta = iteration(dmat, initial_proj, compsize, decfactor, closeToCenter);
			System.out.println("iteration: " + counter + " delta: " + (prevDelta - newDelta));
//			if (iteration(dmat, initial_proj, compsize, decfactor, closeToCenter) < ACCEPTABLE_ERROR) break;
			if (Math.abs(prevDelta - newDelta) < ACCEPTABLE_ERROR) break;
			prevDelta = newDelta;

			if (counter % printInterval == 0) {
				finalProjection = createFinalProjection(initial_proj,dmat);

				model.input(finalProjection);
				model.execute();

				frame.setTitle("iteration " + counter);
				frame.input(model.output());
				frame.execute();
			}
		}

		finalProjection = createFinalProjection(initial_proj,dmat);
		
		return finalProjection;
	}

	private float[] fixCdata(DistanceMatrix dmat) {
		
		float[] cdata = new float[dmat.getElementCount()]; 
		float[] cdata_aux = dmat.getClassData();
		
		float max = cdata_aux[0];
		float min = cdata_aux[0];
		
		for (int i = 0; i < cdata.length; i++) {
			float energy = cdata_aux[i];
			max = (energy >  max) ? energy : max;
			min = (energy < min) ? energy : min;
		} 

		int i = 0;
		for (int id : dmat.getIds()) {
			cdata[id] = cdata_aux[i++];
		}
		return cdata;
	}

	protected abstract double iteration(DistanceMatrix dmat, float[][] aux_proj,
			int compsize, float decfactor, int[] closeToCenter);

	
	protected int[] createIndex(int size) {
		// Create the indexes and shuffle them
		ArrayList<Integer> index_aux = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			index_aux.add(i);
		}

		int[] index_local = new int[size];
		for (int ind = 0, j = 0; j < index_local.length; ind += index_aux
				.size() / 10, j++) {
			if (ind >= index_aux.size()) {
				ind = 0;
			}

			index_local[j] = index_aux.get(ind);
			index_aux.remove(ind);
		}

		return index_local;
	}
	
	protected AbstractMatrix createFinalProjection(float[][] proj, DistanceMatrix dmat) {
		AbstractMatrix projection = new DenseMatrix();

		for (int i = 0; i < proj.length; i++) {
			int id = dmat.getIds().get(i);
			if (dmat.getLabels().isEmpty()) {
				projection.addRow(
					new DenseVector(proj[id], id, dmat.getClassData()[id]));
			} else {
				projection.addRow(
					new DenseVector(proj[id], id, dmat.getClassData()[id]),
					dmat.getLabels().get(id));
			}
		}
		return projection;
	}

	public AbstractMatrix project(AbstractMatrix arg0,
			AbstractDissimilarity arg1) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
