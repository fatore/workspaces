package br.usp.pf.util;

import distance.dissimilarity.AbstractDissimilarity;
import matrix.AbstractVector;

public class EuclidianDistance implements AbstractDissimilarity {

    public float calculate(AbstractVector v1, AbstractVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";

        float dist = 0;
        for (int i = 0; i < v1.size(); i++) {
        	
        	float val1 = v1.getValue(i);
            float val2 = v2.getValue(i);
            
        	dist += Math.pow(val1 - val2, 2);
        }
        
        dist = (float) Math.sqrt(dist);

        return dist;
    }
}
