/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.util.colors.scales;

import java.awt.Color;

/**
 *
 * @author Danilo Medeiros Eler
 */
public class DynamicScale extends ColorScale {

    public DynamicScale() {
        colors = new java.awt.Color[1];
        colors[0] = new java.awt.Color(200, 200, 200);
    }

    public int addColor(java.awt.Color color) {
        index++;
        java.awt.Color aux[] = new java.awt.Color[index+1];
        for (int i=0; i<colors.length; i++){
            aux[i] = colors[i];
        }
        colors = aux;
        colors[index] = color;
        return index;
    }
    /**
	 */
    private int index = 0;
}
