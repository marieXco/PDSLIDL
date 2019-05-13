package fr.pds.floralis.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * WindowOpening
 * It's the first window that the client will see
 * It waits for to see if the getconnection worked
 * @author alveslaura
 *
 */

public class WindowOpening extends Thread {
	// TODO : seeing if getConnection worked
	public final Object valueWaitEndGetConnection = new Object();

	JFrame window = new JFrame();

	private Color backgroundColor = Color.WHITE;
	private int LG = 350;
	private int HT = 120;
	private int HTpb = 40;

	JPanel container = new JPanel();
	JLabel opening = new JLabel("Atribution d'une connection");

	JPanel panelLogo = new LogoPanel();
	JPanel panelConnectionBar = new JPanel();

	JProgressBar progressbar = new JProgressBar(0, 100);


	public void init() {
		container.setBackground(backgroundColor);
		panelConnectionBar.setBackground(backgroundColor);

		opening.setAlignmentX(Component.CENTER_ALIGNMENT);
		progressbar.setAlignmentX(Component.CENTER_ALIGNMENT);

		opening.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		progressbar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		progressbar.setValue(0);
		progressbar.setStringPainted(true);


		panelConnectionBar.add(progressbar);
		panelConnectionBar.add(opening);

		panelLogo.setPreferredSize(new Dimension(LG, HT));
		progressbar.setPreferredSize(new Dimension(LG, HTpb));
		panelLogo.setPreferredSize(new Dimension(LG, HT));

		panelConnectionBar.setLayout(new BoxLayout(panelConnectionBar, BoxLayout.Y_AXIS));

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(panelLogo);
		container.add(panelConnectionBar);
		window.setUndecorated(true);
		window.setContentPane(container);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		int position = 0;

		// progressBar grow up of 8 all the 2 msc
		while (position < 100) {
			progressbar.setValue(position);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			position += 8;
		}

	}


	public void run() {
		init();
		synchronized (valueWaitEndGetConnection) {
			System.out.println("WindowWaiting finished");
			window.setVisible(false);
			valueWaitEndGetConnection.notify(); 
		} 
	}
}
