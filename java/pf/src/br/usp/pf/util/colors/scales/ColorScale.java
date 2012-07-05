package br.usp.pf.util.colors.scales;

import java.awt.Color;

public abstract class ColorScale {
    
    /**
	 */
    private float min = 0.0f;
    /**
	 */
    private float max = 1.0f;
    /**
	 */
    private boolean reverse = false;
    /**
	 */
    protected Color[] colors;  
    /**
	 */
    private float multValue;

    public Color getColor(float value) {
        int index = Math.round((value - min) * multValue);
        if (!reverse) {
            return colors[index];
        } else {
            return colors[colors.length - index - 1];
        }
    }

    public int getNumberColors() {
        return colors.length;
    }
    
    /**
	 * @return
	 */
    public float getMin() {
        return min;
    }
    
    public Color getThisColor(int value) {
        return colors[value];
    }

    public void setMinMax(float min, float max) {
        if (max >= min) {
            this.max = max;
            this.min = min;
        } else {
             System.err.println("The min value of color should be smaller " +"than max value");
        }
        multValue = (colors.length-1)/(max - min);
    }

    /**
	 * @return
	 */
    public float getMax() {
        return max;
    }

    /**
	 * @return
	 */
    public boolean isReverse() {
        return reverse;
    }

    /**
	 * @param reverse
	 */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
