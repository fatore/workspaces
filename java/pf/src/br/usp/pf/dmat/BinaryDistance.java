package br.usp.pf.dmat;

import distance.dissimilarity.AbstractDissimilarity;
import matrix.AbstractVector;

public class BinaryDistance implements AbstractDissimilarity {

    private static final float EPSILON = 0.0001f;

    public float calculate(AbstractVector v1, AbstractVector v2) {
        assert (v1.size() == v2.size()) : "ERROR: vectors of different sizes!";

        float p = 0, s = 0, q = 0, r = 0;

        for (int i = 0; i < v1.size(); i++) {

            float val1 = v1.getValue(i);
            float val2 = v2.getValue(i);

            if (val1 * val2 >= 1) {
                p++;
            } else if (val1 < EPSILON && val2 < EPSILON) {
                s++;
            } else if (val1 < EPSILON) {
                r++;
            } else {
                q++;
            }
        }

        float t = p + q + r + s;

        //================ Distances ============

        //simpleMatch 
        float simpleMatch = (q + r) / t;

        //jaccard 
        float jaccard = (q + r) / (p + q + r);

        //hamming -> ruim
        float hamming = (q + r);

        //original
        float original = 1 - p / (p + 2 * (q + r));

        //my1 -> bom, parece ser melhor que my2
        float my1 = t / p;
        
        //nova
        float nova = ((p + q + r + 1) / (p + 1)) - 1;     
        
        //my2 -> bom
        float my2 = (p + s + 2 * (q + r)) / p;
        
        //my3 -> horrivel
        float my3 = (2 * (q + r)) / p;

        //unknown1 -> ruim
        float unknown1 = (p + s)/ t;

        //unknown2 -> muito oposto
        float unknown2 = p/ t;

        //unknown3 - meio oposto
        float unknown3 = p/(p + q + r);

        //unknown4 -> +/--- oposto, mais pra ruim
        float unknown4 = 2*p/(2*p + q + r);

        //unknown5 ruim
        float unknown5 = 2*(p + s)/(2 * (p + s) + q + r);

        //unknown6 ruim
        float unknown6 = (p + s)/(p + s + 2 * (q + r));

        //unknown7 -> muito oposto
        float unknown7 = p / (p + s + 2 * (q + r));

        //unknown8 -> +/- oposto
        float unknown8 =  p / (p + 2 * (q + r));

        //unknown9 -> +/-- oposto
        float unknown9 =  p / (q + r);

        //unknown10 -> +/--- oposto, mais pra ruim
        float unknown10 =  p + s / (q + r);

        //========================================

        return nova;
    }
}
