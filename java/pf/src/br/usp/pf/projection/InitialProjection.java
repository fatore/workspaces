package br.usp.pf.projection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import distance.DistanceMatrix;

public class InitialProjection {
	
	private float[][] projection;
	private static Random rand = new Random();
	
	public enum Layout {
		RANDOM,
		RADIAL,
		RADIAL_PIECES
	}
	
	public InitialProjection(DistanceMatrix dmat, Layout layout) {
		if (layout.equals(Layout.RADIAL)) {
			this.projection = createRadialProjection(dmat);
		}
		if (layout.equals(Layout.RADIAL_PIECES)) {
			this.projection = createRadialPiecesProjection(dmat);
		}
		if (layout.equals(Layout.RANDOM)) {
			this.projection = createRandomProjection(dmat);
		}
	}
	
	private float[][] createRandomProjection(DistanceMatrix dmat) {

		float[][] aux_proj = new float[dmat.getElementCount()][];

		for (int i = 0; i < dmat.getElementCount(); i++) {
			aux_proj[i] = new float[2];
			aux_proj[i][0] = (float) rand.nextFloat() - 0.5f;
			aux_proj[i][1] = (float) rand.nextFloat() - 0.5f;
		}
		return aux_proj;
	}

	private float[][] createRadialProjection(DistanceMatrix dmat) {

		List<Integer> ids = dmat.getIds();

		float[] cdata = dmat.getClassData();

		HashMap<Float, ArrayList<Integer>> energyIntervals = new HashMap<Float, ArrayList<Integer>>();

		for (int i = 0; i < dmat.getElementCount(); i++) {
			float key = cdata[ids.get(i)];
			if (!energyIntervals.containsKey(key)) {
				energyIntervals.put(key, new ArrayList<Integer>());
			}
			energyIntervals.get(key).add(ids.get(i));
		}

		float[][] aux_proj = new float[dmat.getElementCount()][];

		// find which index represents the native state
		// by convention its known that the first element represents the native state
		int nativeIndex = ids.get(0);
		float nativeEnergy = cdata[nativeIndex];

		for (List<Integer> list : energyIntervals.values()) {
			float delta_e = Math.abs(nativeEnergy - (cdata[list.get(0)] + 1));
			double delta_t = (2 * Math.PI) / list.size();
			double theta = 0.0;
			for (Integer i : list) {
				aux_proj[i] = new float[2];
				//				aux_proj[i][0] = (float) rand.nextFloat() - 0.5f;
				//				aux_proj[i][1] = (float) rand.nextFloat() - 0.5f;
				aux_proj[i][0] = (float) Math.cos(theta) * delta_e;
				aux_proj[i][1] = (float) Math.sin(theta) * delta_e;
				//				aux_proj[i][0] = (float) Math.cos(theta) * dmat.getDistance(nativeIndex, ids.get(i));
				//				aux_proj[i][1] = (float) Math.sin(theta) * dmat.getDistance(nativeIndex, ids.get(i));
				theta += delta_t;
			}
		}

		return aux_proj;
	}
	
	private float[][] createRadialPiecesProjection(DistanceMatrix dmat) {

		List<Integer> ids = dmat.getIds();

		float[] cdata = dmat.getClassData();

		int pieceSize = 500;

		HashMap<Float, ArrayList<ArrayList<Integer>>> energyIntervals = new HashMap<Float, ArrayList<ArrayList<Integer>>>();

		for (int i = 0; i < dmat.getElementCount(); i++) {
			float key = cdata[ids.get(i)];
			if (!energyIntervals.containsKey(key)) {
				energyIntervals.put(key, new ArrayList<ArrayList<Integer>>());
			}
			if (energyIntervals.get(key).isEmpty()) {
				energyIntervals.get(key).add(new ArrayList<Integer>());
			}
			int noPieces = energyIntervals.get(key).size();
			if (energyIntervals.get(key).get(noPieces - 1).size() < pieceSize) {
				energyIntervals.get(key).get(noPieces - 1).add(ids.get(i));
			} else {
				energyIntervals.get(key).add(new ArrayList<Integer>());
				energyIntervals.get(key).get(noPieces - 1).add(ids.get(i));
			}
		}

		float[][] aux_proj = new float[dmat.getElementCount()][];

		// find which index represents the native state
		// by convention its known that the first element represents the native state
		int nativeIndex = ids.get(0);
		float nativeEnergy = cdata[nativeIndex];

		for (ArrayList<ArrayList<Integer>> lists : energyIntervals.values()) {
			float offset = 0;
			for (ArrayList<Integer> list : lists) {
				float delta_e = Math.abs(nativeEnergy - (cdata[list.get(0)] + 1)) + offset;
				double delta_t = (2 * Math.PI) / list.size();
				double theta = 0.0;
				for (Integer i : list) {
					aux_proj[i] = new float[2];
					// aux_proj[i][0] = (float) rand.nextFloat() - 0.5f;
					// aux_proj[i][1] = (float) rand.nextFloat() - 0.5f;
					aux_proj[i][0] = (float) Math.cos(theta) * delta_e;
					aux_proj[i][1] = (float) Math.sin(theta) * delta_e;
					// aux_proj[i][0] = (float) Math.cos(theta) *
					// dmat.getDistance(nativeIndex, ids.get(i));
					// aux_proj[i][1] = (float) Math.sin(theta) *
					// dmat.getDistance(nativeIndex, ids.get(i));
					theta += delta_t;
				}
				offset += 0.55;
			}
		}

		return aux_proj;
	}

	public float[][] getProjection() {
		return projection;
	}
}
