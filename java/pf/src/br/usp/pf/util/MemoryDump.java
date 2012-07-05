/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.util;

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class MemoryDump extends Thread {

    public static void dump() {
        if (md != null) {
            md.interrupt();
        }

        md = new MemoryDump();
        md.start();
    }

    public static void finished() {
        if (md != null) {
            md.interrupt();
        }

        md = null;
    }

    @Override
    public void run() {
        try {
            while (true) {
                MemoryDump.this.check();
                Thread.sleep(5000);
            }
        } catch (InterruptedException ex) {
            //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void check() {
        Runtime r = Runtime.getRuntime();
        r.gc();

        double usedMemory = (r.totalMemory() / 1024.0 / 1024.0) - (r.freeMemory() / 1024.0 / 1024.0);
        if (this.maxMemoryUsed < usedMemory) {
            this.maxMemoryUsed = usedMemory;
        }

        NumberFormat form = NumberFormat.getInstance();
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(2);

        System.out.println("used: " + form.format(usedMemory) + " MB - "
                + "max: " + form.format(this.maxMemoryUsed) + " MB");
    }
    
    /**
	 */
    private double maxMemoryUsed = Double.MIN_VALUE;
    private static MemoryDump md;
}
