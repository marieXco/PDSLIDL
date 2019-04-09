package fr.pds.floralis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WindowDisconnect extends JFrame {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = -4392167884505063311L;
	//private JDBCConnectionPool jdb;
	//private Connection connect;
	
	private String host;
	private int port;

	private Color backgroundColor = Color.WHITE;
	private int LG = 350;
	private int HT = 120;
	private int HTdis = 40;

	JFrame windowWainting = new JFrame();

	JPanel container = new JPanel();
	JLabel opening = new JLabel("Deconnection");

	JPanel panelLogo = new LogoPanel();
	JPanel panelDisconnect = new JPanel();
	
	
	public WindowDisconnect(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	public void init() throws InterruptedException {
		setPort(port);
		setHost(host);
		
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
		
		//ConnectionClient ccDisconnect = new ConnectionClient(getHost, getPort, "CLOSE", null, null);
		//ccDisconnect.run();
		
		//jdb.backConnection(connect);
		// On attend un peu avant de la fermer
		TimeUnit.SECONDS.sleep(3);
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		
		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}