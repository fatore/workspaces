/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.util.colors.scales;

import java.awt.Color;

/**
 *
 * @author PC
 */
public class GreenToWhiteScale extends ColorScale {

    /**
     * Creates a new instance of UndefinedCS
     */
    public GreenToWhiteScale() {
        colors = new Color[256];

        int mid = (int) (colors.length * 0.65f);

        for (int i = 0; i < mid; i++) {
            int value = (int) (((float) i / (float) mid) * 255);
            colors[i] = new Color(0, value, 0);
        }

        for (int i = mid; i < colors.length; i++) {
            int value = (int) (((float) (i - mid) / (float) (colors.length - 1 - mid)) * 255);
            colors[i] = new Color(value, 255, value);
        }
    }
}
