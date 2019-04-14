package fr.pds.floralis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** 
 * WindowDisconnect
 * GUI that shows the last GUI, that simulates the deconnection 
 * 
 * @author alveslaura
 *
 */

public class WindowDisconnect extends JFrame {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = -4392167884505063311L;

	private Color backgroundColor = Color.WHITE;
	private int LG = 350;
	private int HT = 120;
	private int HTdis = 40;

	JFrame windowWainting = new JFrame();

	JPanel container = new JPanel();
	JLabel opening = new JLabel("Fermeture...");

	JPanel panelLogo = new LogoPanel();
	JPanel panelDisconnect = new JPanel();

	public void init() throws InterruptedException {
		container.setBackground(backgroundColor);
		panelDisconnect.setBackground(backgroundColor);

		panelDisconnect.add(opening);

		panelLogo.setPreferredSize(new Dimension(LG, HT));
		panelDisconnect.setPreferredSize(new Dimension(LG, HTdis));
		container.add(panelLogo);
		container.add(panelDisconnect);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setUndecorated(true);
		this.setContentPane(container);
		pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		
		this.setVisible(true);
		TimeUnit.SECONDS.sleep(3);
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}