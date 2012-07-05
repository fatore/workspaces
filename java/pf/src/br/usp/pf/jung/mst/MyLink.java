/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.pf.jung.mst;

/**
 *
 * @author Fatore
 */
public class MyLink {
    /**
	 */
    private int weight;
    /**
	 */
    private int id;

    /**
	 * @return
	 */
    public int getId() {
        return id;
    }

    /**
	 * @return
	 */
    public int getWeight() {
        return weight;
    }

    public MyLink(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }



}
