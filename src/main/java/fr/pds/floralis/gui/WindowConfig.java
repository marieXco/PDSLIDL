package fr.pds.floralis.gui;

import java.awt.HeadlessException;

import javax.swing.JPanel;

public class WindowConfig {
	
	private String host;
	private int port;
	
	public WindowConfig(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}
}
