/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.pf.util.colors.scales;

import java.awt.Color;

/**
 *
 * @author Fernando
 */
public class CategoryScale extends ColorScale {

    private static final Color[] BASE_COLORS = {/*Color.DARK_GRAY,*/Color.MAGENTA, Color.YELLOW,
        Color.BLUE, Color.WHITE, Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.GREEN
    };

    public CategoryScale() {
        colors = new java.awt.Color[(BASE_COLORS.length - 1) * 25];

        int intercolros = colors.length / (BASE_COLORS.length - 1);
        int k = 0;

        for (int i = 0; i < BASE_COLORS.length - 1; i++) {
            for (float j = 0; j < intercolros; j++) {
                colors[k++] = interpolate(BASE_COLORS[i], BASE_COLORS[i + 1], j / (intercolros - 1));
            }
        }
    }

    private Color interpolate(Color a, Color b, float alpha) {
        int blue = (int) Math.min((1 - alpha) * a.getBlue() + alpha * b.getBlue(), 255);
        int green = (int) Math.min((1 - alpha) * a.getGreen() + alpha * b.getGreen(), 255);
        int red = (int) Math.min((1 - alpha) * a.getRed() + alpha * b.getRed(), 255);
        return new Color(red, green, blue);
    }

}
