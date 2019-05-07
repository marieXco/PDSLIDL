package fr.pds.floralis.gui;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class WindowAlert extends JFrame {
	private static final long serialVersionUID = 2L;

	public void init() {

		JOptionPane.showMessageDialog(null, "Attention ! un capteur est en alerte !", "Nouvelle alert", JOptionPane.ERROR_MESSAGE);
	}
}
