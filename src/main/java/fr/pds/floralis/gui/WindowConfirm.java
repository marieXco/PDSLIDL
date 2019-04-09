package fr.pds.floralis.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class WindowConfirm extends JFrame{
	// Adding a serialVersionUID as the JFrame class extend Serializable
	// https://www.commentcamarche.net/forum/affich-1928411-serialversionuid
	private static final long serialVersionUID = 1L;

	// Fenêtre de confirmation modulable, on y rentre la phrase voulue
	public boolean init(String phrase) {
		ImageIcon img = new ImageIcon("src/main/resources/images/info-icon.png");

		int input = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment " + phrase + " ?", "Confirmation",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, img);
		
		// if input equals : 0 --> cliqued on yes, 1 --> cliqued on no, 2 --> cliqued on cancel
		if (input == 0) {
			return true;
		}
		else {
			return false;
		}
	}

}
