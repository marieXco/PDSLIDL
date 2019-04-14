package fr.pds.floralis.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/** 
 * Logo Panel 
 * Method made to show the logo on the first GUI
 * 
 * @author alveslaura
 *
 */

public class LogoPanel extends JPanel {
	// Adding a serialVersionUID as the JPanel class extend Serializable
	// https://www.commentcamarche.net/forum/affich-1928411-serialversionuid
	private static final long serialVersionUID = -1820830856142284793L;

	public void paintComponent(Graphics g){
		try {
			// FIXME BEFORE EXPORTING THE JARS, uncomment the next line and comment the one after
			//Image img = ImageIO.read(new File("./images/logo1.png"));
			Image img = ImageIO.read(new File("src/main/resources/images/logo1.png"));
			g.drawImage(img, 25, 0, 300, 110, this);
		} catch (IOException e) {
			e.printStackTrace();
		}                
	} 
}
