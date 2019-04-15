
	package fr.pds.floralis.gui;


	import java.awt.Button;

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



	public class WindowStats {
		
		
		// Creation of panels
		JPanel container = new JPanel();
		JPanel RequestPanel = new JPanel();
		JPanel ResultPanel = new JPanel(); //TODO: contient tableau + Une ligne plus gros pour highlight 
		


		// Creation of all parameters necessary 
		public void initIndicators() {


		// TODO: Combobox for indicators
		
		JComboBox<Object> indicators = new JComboBox<Object>(); 
		indicators.addItem("Home");
		indicators.addItem("Sensor");
		indicators.addItem("Breakdown");
		indicators.addItem("Alert");
		indicators.addItem("room");
		indicators.addItem("patient");
		// TODO: combo box to add dynamic parameters
		JComboBox<Object> parameters = new JComboBox<Object>(); 
		}

		// Button to have the results of the request
		Button buttonResult = new Button("Filtrer");
		//button to associate many request (?) OPTIONAL
		//Button buttonAddFilter = new Button("+");
		
		//TODO: adding action performed method
		
		//TODO: How am I supposed to be connected with de bdd ??
		
		//TODO: draw graphic (optional)
		
		//TODO: init methode to send a request.
		
		//TODO: method who print results

		//TODO: DAO for all indicators
		//TODO : modify the request handlers for filters and indicators
}
