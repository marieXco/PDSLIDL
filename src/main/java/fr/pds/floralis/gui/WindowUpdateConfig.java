package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class WindowUpdateConfig extends JFrame implements ActionListener {
	
	private int LG = 700;
	private int HT = 100;
	
	// Creation of panels
	JPanel container = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	
	JTextField resultSend = new JTextField(10);
	JTextPane infos = new JTextPane();
	
	/**
	 * the thresholds are different according to the type of sensors
	 */
	
	/**
	 * For TEMPERATURE
	 * The temperature have to be between the min and the max
	 */
	JTextField min = new JTextField(5);
	JLabel minLabel = new JLabel("Temperature Min :");
	JLabel minTempUnit = new JLabel("C°   ");

	JTextField max = new JTextField(5);
	JLabel maxLabel = new JLabel("Temperature Max :");
	JLabel maxTempUnit = new JLabel("C°   ");
	/**
	 * For PASSAGE AND LIGHT
	 * The sensibility of the sensor is different for the day and for the night
	 */
	JTextField duringDay = new JTextField(5);
	JLabel dayLabel = new JLabel("Durée journée :");
	JLabel dayTimeUnit = new JLabel("secondes   ");

	JTextField duringNight = new JTextField(5);
	JLabel nightLabel = new JLabel("Durée nuit :");
	JLabel nightTimeUnit = new JLabel("secondes   ");
	/**
	 * For GASLEAK
	 * It is just a maximum gas rate before the sensor is in alert
	 */
	JTextField gas = new JTextField(5);
	JLabel gasLabel = new JLabel("Taux de gaz maximum :");
	JLabel gasUnit = new JLabel("mg   ");
	/**
	 * For FIRE
	 * No choice for the user because as soon as there are smoke, the sensor is in alert
	 */
	
	Button buttonUpdateWarningLevel = new Button("Modifier les seuils");

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	protected int id;
	Sensor sensorFound;
	

	public void init(int id) throws JsonParseException, JsonMappingException, JSONException, IOException {

		setId(id);

		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Modification d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		buttonUpdateWarningLevel.addActionListener(this);


		// Beginning of sensor Find by id
		// Recovery of the sensor associate at the id in parameter
		JSONObject sensorIdFindById = new JSONObject();
		sensorIdFindById.put("id", getId());
		
		Request request = new Request();
		request.setType("FINDBYID");
		request.setEntity("SENSOR");
		request.setFields(sensorIdFindById);
		
		ConnectionClient ccSensorFindById = new ConnectionClient(request.toJSON().toString());
		ccSensorFindById.run();

		String retourSensorFindById = ccSensorFindById.getResponse();
		JSONObject sensorFoundJson = new JSONObject();
		sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

		ObjectMapper objectMapper = new ObjectMapper();
		sensorFound =  objectMapper.readValue(sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);
		// End sensor Find By Id
		
		if(sensorFound.getType().equals("FIRE")) {
			container.setPreferredSize(new Dimension(LG + 200, HT + 20));
		} else {
			container.setPreferredSize(new Dimension(LG + 200, HT + 50));
		}
		
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Configuration d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		this.setTitle("Floralis - Modification des seuils " + sensorFound.getId());
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(infos);
		container.add(buttonUpdateWarningLevel);

		
		switch (sensorFound.getType()) {
		case "TEMPERATURE" :
			mainInfosPanel.add(minLabel);
			mainInfosPanel.add(min);
			mainInfosPanel.add(minTempUnit);
			mainInfosPanel.add(maxLabel);
			mainInfosPanel.add(max);
			mainInfosPanel.add(maxTempUnit);
			break;
		case "PRESENCE":
			mainInfosPanel.add(dayLabel);
			mainInfosPanel.add(duringDay);
			mainInfosPanel.add(dayTimeUnit);
			mainInfosPanel.add(nightLabel);
			mainInfosPanel.add(duringNight);
			mainInfosPanel.add(nightTimeUnit);
			break;
		case "LIGHT":
			mainInfosPanel.add(dayLabel);
			mainInfosPanel.add(duringDay);
			mainInfosPanel.add(dayTimeUnit);
			mainInfosPanel.add(nightLabel);
			mainInfosPanel.add(duringNight);
			mainInfosPanel.add(nightTimeUnit);
			break;
		case "GASLEAK":
			mainInfosPanel.add(gasLabel);
			mainInfosPanel.add(gas);
			mainInfosPanel.add(gasUnit);
			break;
		}	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonUpdateWarningLevel) {
			try {
				if(sensorFound.getType().equals("TEMPERATURE")) {
					Integer.parseInt(min.getText());
					Integer.parseInt(max.getText());
				} else if(sensorFound.getType().equals("LIGHT") || sensorFound.getType().equals("PRESENCE")) {
					Integer.parseInt(duringDay.getText());
					Integer.parseInt(duringNight.getText());
				} else if(sensorFound.getType().equals("GASLEAK")) {
					Integer.parseInt(gas.getText());
				}
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("Les seuils ne peuvent contenir que des chiffres");
			}
			
			// Checking informations
			if (sensorFound.getType().equals("TEMPERATURE") && 
					(min.getText().isEmpty() || max.getText().isEmpty())) {
				infos.setText("Vous devez renseigner les seuils de température");
			}
			
			else if((sensorFound.getType().equals("LIGHT") || sensorFound.getType().equals("PRESENCE")) && 
					(duringDay.getText().isEmpty() || duringNight.getText().isEmpty())) {
				infos.setText("Vous devez renseigner les seuils de durée");
			}
			
			else if(sensorFound.getType().equals("GASLEAK") && gas.getText().isEmpty()) {
				infos.setText("Vous devez renseigner le seuil de gaz");
			}

			// If min > max
			else if (sensorFound.getType().equals("TEMPERATRUE") && Integer.parseInt(min.getText()) >= Integer.parseInt(max.getText())) {
				infos.setText("La valeur minimum doit être strictement inferieure à la valeur maximum");
			}


			else { 
				
				//Configuration acording to the type of the sensor
				switch (sensorFound.getType()) {
				case "TEMPERATURE":
					sensorFound.setMin(Integer.parseInt(min.getText().trim()));
					sensorFound.setMax(Integer.parseInt(max.getText().trim()));
					break;
				case "LIGHT":
					sensorFound.setMin(Integer.parseInt(duringNight.getText().trim()));
					sensorFound.setMax(Integer.parseInt(duringDay.getText().trim()));
					break;
				case "PRESENCE":
					sensorFound.setMin(Integer.parseInt(duringNight.getText().trim()));
					sensorFound.setMax(Integer.parseInt(duringDay.getText().trim()));
					break;
				case "GASLEAK":
					sensorFound.setMin(0);
					sensorFound.setMax(Integer.parseInt(gas.getText().trim()));
					break;
				}
				
				JSONObject sensorConfigJson = new JSONObject();
				sensorConfigJson.put("id", sensorFound.getId());
				sensorConfigJson.put("sensorToUpdate", sensorFound.toJSON());

				Request thirdRequest = new Request();
				thirdRequest.setType("UPDATE");
				thirdRequest.setEntity("SENSOR");
				thirdRequest.setFields(sensorConfigJson);

				ConnectionClient ccSensorUpdate = new ConnectionClient(thirdRequest.toJSON().toString());
				ccSensorUpdate.run();
				// End sensor Create

				// TODO : problème : arraylist ne se mets pas à jours

				// Beginning of the location update
				// This function is here because
				// When you add sensor, you attribute a location to this sensor
				// So, it have to add the new sensors at the 'sensor id table' of this location
				// TODO : modifier l'ancienne localisation 
				this.setVisible(false);
			}


		}
		
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	
}
