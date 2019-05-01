package fr.pds.floralis.gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class WindowConfig extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8097747904160110502L;
	private int LG = 700;
	private int HT = 120;
	
	JTextPane infos = new JTextPane();
	SimpleAttributeSet centrer = new SimpleAttributeSet();
	
	// Creation of panels
	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	
	private String host;
	private int port;
	protected int id;
	
	public WindowConfig(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}
	
	public void initConfigSensor(int id) throws JsonParseException,
	JsonMappingException, IOException, JSONException, InterruptedException {
		this.id = id;
		
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Modification d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);
		
		container.setPreferredSize(new Dimension(LG + 200, HT));
		
		this.setTitle("Floralis - Configuration d'un capteur");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
}
