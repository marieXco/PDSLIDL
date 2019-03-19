package fr.pds.floralis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowDisconnect extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JDBCConnectionPool jdb;
	private Connection connect;

	private Color backgroundColor = Color.WHITE;
	private int LG = 350;
	private int HT = 120;
	private int HTdis = 40;

	JFrame windowWainting = new JFrame();

	JPanel container = new JPanel();
	JLabel opening = new JLabel("Deconnection");

	JPanel panelLogo = new LogoPanel();
	JPanel panelDisconnect = new JPanel();


	public WindowDisconnect(JDBCConnectionPool jdbc, Connection connection) throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		this.jdb = jdbc;
		this.connect = connection;
	}
	
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
		jdb.backConnection(connect);
		TimeUnit.SECONDS.sleep(3);
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}