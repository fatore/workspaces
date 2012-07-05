package br.usp.pf.jung.mst;

import java.util.Arrays;

public class Transition {

    /**
	 */
    int source;
    /**
	 */
    int target;

    public Transition(int source, int target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transition other = (Transition) obj;
        if (this.source != other.source) {
            return false;
        }
        if (this.target != other.target) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int hash = 3;
        int[] array = new int[]{this.source, this.target};
        hash = 67 * hash + Arrays.hashCode(array);

        return hash;
    }
}
