package br.usp.pf.projection;

import java.util.List;

import distance.DistanceMatrix;

public class ForceScheme extends AbstractForceScheme {
	
	protected double iteration(DistanceMatrix dmat, float[][] aux_proj,
			int compsize, float decfactor, int[] closeToCenter) {
		
		double maxDist = 0;
		double totalDelta = 0;
		int[] mostDistElem = new int[2]; 
		
		List<Integer> ids = dmat.getIds();

		// for each instance
		for (int i = 0; i < dmat.getElementCount(); i++) {
			int instance1 = index[i];

			// for each other instance
			for (int j = 0; j < dmat.getElementCount(); j++) {
				int instance2 = index[j];
				
				if (instance1 == instance2) {
					continue;
				}

				// distance between projected instances
				double x1x2 = (aux_proj[ids.get(instance2)][0] - aux_proj[ids.get(instance1)][0]);
				double y1y2 = (aux_proj[ids.get(instance2)][1] - aux_proj[ids.get(instance1)][1]);
				double dr2 = Math.sqrt(x1x2 * x1x2 + y1y2 * y1y2);
				
				if (dr2 < EPSILON) {
					dr2 = EPSILON;
				}

				float drn = dmat.getDistance(instance1, instance2);

				// Calculating the (fraction of) delta
				double delta = Math.sqrt(drn) - Math.sqrt(dr2);
				delta *= Math.abs(delta);
				totalDelta += Math.abs(delta);
				delta *= decfactor;
				
				if (delta > maxDist) {
					maxDist = delta;
					mostDistElem[0] = instance1;
					mostDistElem[1] = instance2;
				}
					
				aux_proj[ids.get(instance2)][0] += delta * (x1x2 / dr2);
				aux_proj[ids.get(instance2)][1] += delta * (y1y2 / dr2);

				if (aux_proj[ids.get(instance2)][0] != aux_proj[ids.get(instance2)][0]) {
					System.out.println("Error: Force Scheme >> delta" + delta
							+ ", x1x2=" + x1x2 + ", dr2=" + dr2 + ", drn="
							+ drn);
				}
			}
		}
//		System.out.println("iteration: " + counter + " maxDist: " + maxDist);
//		System.out.println("iteration: " + counter + " total delta: " + (totalDelta / dmat.getElementCount()));
		return (totalDelta / dmat.getElementCount());
//		return maxDist;
	}
}
