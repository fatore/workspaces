/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.pf.jung.mst;

/**
 *
 * @author Fatore
 */
public class MyNode {
    /**
	 */
    private int key;
    /**
	 */
    private int energy;

    /**
	 * @return
	 */
    public int getKey() {
        return key;
    }

    /**
	 * @return
	 */
    public int getEnergy() {
        return energy;
    }

    public MyNode(int key, int weight) {
        this.key = key;
        this.energy = weight;
    }

    

}
