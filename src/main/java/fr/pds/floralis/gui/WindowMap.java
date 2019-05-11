package fr.pds.floralis.gui;

import java.awt.Button;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WindowMap extends JFrame implements ActionListener {

	private String host;
	private int port;

	public WindowMap(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	// Creation of panels
	JPanel container = new JPanel();
	JPanel sensorsNoConfigPanel = new JPanel();
	JPanel mapPanel = new JPanel();
	JPanel sensorsByLocationPanel = new JPanel();

	// Button
	Button addingSensor = new Button("Ajouter un capteur");

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void initMap() {
		JFrame windowMap = new JFrame();

		windowMap.setSize(1375, 800);
		windowMap.setLocationRelativeTo(null);

		windowMap.setVisible(true);

		this.setLayout(new BorderLayout());
		

	}
}
