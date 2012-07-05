/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.pf.view.util;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author fm
 */
public class PrintScreenPanel extends JPanel {
    /**
	 */
    private JPanel source;
    /**
	 */
    JButton saveImage;
    /**
	 */
    JTextField filenameField;
    
    public PrintScreenPanel(JPanel source) {
        super();
        this.setBorder(BorderFactory.createTitledBorder("Print Screen"));
        this.source = source;
        
        saveImage = new JButton("Save Image");
        saveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               saveToPngImageFile();
            }
        });
        
        this.add(saveImage);
        
        filenameField = new JTextField();
        filenameField.setColumns(40);
        filenameField.setText("../default-filename.png");
        
        this.add(filenameField);
        
    }
    
    public void saveToPngImageFile() {
        try {
            BufferedImage image = new BufferedImage
                    (this.source.getSize().width + 1,
                    this.source.getSize().height + 1,
                    BufferedImage.TYPE_INT_RGB);
            this.source.paint(image.getGraphics());
            ImageIO.write(image, "png", new File(filenameField.getText()));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
