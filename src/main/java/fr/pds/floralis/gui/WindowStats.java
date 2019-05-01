
	package fr.pds.floralis.gui;


	import java.awt.BorderLayout;
	import java.awt.Button;
	import java.awt.HeadlessException;
	import java.awt.event.*;
	import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
	import javax.swing.BoxLayout;
	import javax.swing.JComboBox;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JPanel;
	import javax.swing.JPasswordField;
	import javax.swing.JTextField;
	import javax.swing.JTextPane;
	import javax.swing.text.SimpleAttributeSet;
	import javax.swing.text.StyleConstants;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.server.dao.DAO;

	//TODO: ajouter un onglet pour accéder à ses stats visibles seulement par les administrateurs sur WindowAdd
	//TODO: faire les scripts
	
	// this is the window where 
	public class WindowStats extends JFrame implements ActionListener {
		private String host;
		private int port;
		private int LG = 950;
		private int HT = 180;
		
		public WindowStats(String host, int port) throws HeadlessException {
			super();
			this.host = host;
			this.port = port;
		}
		
		
		// Creation of panels
		JPanel container = new JPanel();
		JPanel requestPanel = new JPanel();
		JPanel resultPanel = new JPanel(); 
		

		// Creation of all parameters necessary 
		Button countBedroom = new Button("Nombre de logement");
		Button countAllSensor = new Button("Nombre de capteurs");
		Button countOffSensor = new Button("Nombre de capteurs éteint");
		Button countOnSensor = new Button("Nombre de capteurs allumés");
		Button countNoLocationSensor = new Button("Nombres de capteurs non placés");
		//Button countRoom = new Button("Nombre d'espace communs");
		
		
		// Indicators values
		List<Room> roomFoundList; 
		
		
		

		public void initIndicators() throws IOException{
			
			
			//adding button to JPanel
			requestPanel.add(countAllSensor);
			requestPanel.add(countBedroom);
			requestPanel.add(countOffSensor);
			requestPanel.add(countNoLocationSensor);
			requestPanel.add(countOnSensor);
			container.add(BorderLayout.WEST, requestPanel); 
			container.add(BorderLayout.EAST, resultPanel);
			

			countBedroom.addActionListener(this);
			countAllSensor.addActionListener(this);
			countOffSensor.addActionListener(this);
			countOnSensor.addActionListener(this);
			countNoLocationSensor.addActionListener(this);
		
			this.setTitle("Floralis - Indicateurs");
		    pack(); //permet d'avoir une bonne dimension pour la fenetre
			this.setContentPane(container);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setVisible(true);
			
		}
		
		public void actionPerformed(ActionEvent e) {
			//when the user wants to know how many home there's on EHPAD
			if (e.getSource() == countBedroom) {
					FindAllRoom allrm = new FindAllRoom(host, port);
					try {
						roomFoundList = allrm.findAll(false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countRoom = roomFoundList.size(); 	
					System.out.println("Nombre de logement : " + countRoom);
				}
			if(e.getSource() == countAllSensor) {
				
			}
			
			if(e.getSource() == countOffSensor) {
				
			}
			
			if(e.getSource() == countOnSensor) {
				
			}
			
			if(e.getSource() == countNoLocationSensor) {
				
			}
			
		}


		
		//TODO: adding action performed method
		
		
		//TODO: How am I supposed to be connected with de bdd ??
		
		//TODO: draw graphic (optional)
		
		//TODO: init methode to send a request.
		
		//TODO: method who print results

		//TODO: DAO for all indicators
		//TODO : modify the request handlers for filters and indicators
}
