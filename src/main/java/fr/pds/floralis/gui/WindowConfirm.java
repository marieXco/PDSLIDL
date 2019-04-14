package fr.pds.floralis.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** 
 * ConfirmationWindow
 * Class to show a popup GUI to confirm a choice, customizable with a string
 * 
 * @author alveslaura
 *
 */

public class WindowConfirm extends JFrame{
	// Adding a serialVersionUID as the JFrame class extend Serializable
	// https://www.commentcamarche.net/forum/affich-1928411-serialversionuid
	private static final long serialVersionUID = 1L;

	public boolean init(String phrase) {
		// FIXME BEFORE EXPORTING THE JARS, uncomment the next line and comment the one after
		//ImageIcon img = new ImageIcon("./images/info-icon.png");
		ImageIcon img = new ImageIcon("src/main/resources/images/info-icon.png");

		int input = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment " + phrase + " ?", "Confirmation",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, img);
		
		// if input equals : 0 --> cliqued on yes, 1 --> cliqued on no, 2 --> cliqued on cancel
		if (0 == input) {
			return true;
		}
		else {
			return false;
		}
	}

}
