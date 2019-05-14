package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Building;
import fr.pds.floralis.commons.bean.entity.Floor;
import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;


public class WindowConfig extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8097747904160110502L;
	private int LG = 700;
	private int HT = 150;

	JTextPane infos = new JTextPane();
	SimpleAttributeSet centrer = new SimpleAttributeSet();

	// Creation of panels
	JPanel container = new JPanel();
	JPanel containerNorth = new JPanel();
	JPanel containerSouth = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	JPanel warningLevelPanel = new JPanel();
	JPanel locationPanel = new JPanel();
	JPanel statePanel = new JPanel();
	
	/**
	 *  Parameters for sensor
	 */
	JComboBox<Object> location = new JComboBox<Object>();

	JTextField dateInstallation = new JTextField(10);
	JLabel dateInstallationLabel = new JLabel("Date d'installation :");

	JTextField addressIp = new JTextField(10);
	JLabel addressIpLabel = new JLabel("Address IP :");

	JTextField portSensor = new JTextField(4);
	JLabel portLabel = new JLabel("port :");

	JButton stateOn = new JButton("ON");
	JButton stateOff = new JButton("OFF");
	ButtonGroup stateOnOff = new ButtonGroup();
	Boolean state = true;

	Button buttonConfigSensor = new Button("Configurer");

	JComboBox<Object> day = new JComboBox<Object>();

	String[] days = new String[32];

	JComboBox<Object> month = new JComboBox<Object>();

	String[] months = new String[13];

	JComboBox<Object> year = new JComboBox<Object>();

	String[] years = new String[6];


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

	/**
	 *  Parameters for locations
	 */
	JTextField identifiant = new JTextField(10);
	JLabel identifiantLabel = new JLabel("Identifiant :");
	public final Object waitAdd = new Object();

	List<Floor> floorFoundList = new ArrayList<Floor>();

	List<Building> buildingsFoundList = new ArrayList<Building>();

	List<Room> roomsFoundList = new ArrayList<Room>();

	List<Location> locationsFoundList = new ArrayList<Location>();


	Location[] locationsFoundTab = null;

	Building[] buildingsFoundTab;

	Room[] roomsFoundTab;

	Floor[] floorsFoundTab;

	ObjectMapper objectMapper = new ObjectMapper();
	Sensor sensorFound;

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

		JSONObject sensorIdFindById = new JSONObject();
		sensorIdFindById.put("id", getId());

		Request request = new Request();
		request.setType("FINDBYID");
		request.setEntity("SENSOR");
		request.setFields(sensorIdFindById);

		ConnectionClient ccSensorFindById = new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindById.run();

		String retourSensorFindById = ccSensorFindById.getResponse();
		JSONObject sensorFoundJson = new JSONObject();
		sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

		ObjectMapper objectMapper = new ObjectMapper();
		sensorFound =  objectMapper.readValue(sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);

		// Dimension of the window according to the type of the sensor
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

		buttonConfigSensor.addActionListener(this);
		stateOn.addActionListener(this);
		stateOff.addActionListener(this);

		this.setTitle("Floralis - Configuration du capteur " + sensorFound.getId());
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);


		//Installation day
		days[0] = "Jour";

		months[0] = "Mois";

		years[0] = "Annee";

		for (int dayIndex = 1; dayIndex < days.length; dayIndex++) {
			String daysMax = (dayIndex) + "";
			days[dayIndex] = daysMax;
		}

		for (int monthIndex = 1; monthIndex < months.length; monthIndex++) {
			String monthMax = (monthIndex) + "";
			months[monthIndex] = monthMax;
		}

		for (int yearIndex = 1; yearIndex < years.length; yearIndex++) {
			String yearMax = (yearIndex + 2014) + "";
			years[yearIndex] = yearMax;
		}

		day = new JComboBox<Object>(days);

		month = new JComboBox<Object>(months);

		year = new JComboBox<Object>(years);
		//End installation day

		//Location
		FindAllLocation fl = new FindAllLocation(host, port);
		locationsFoundList = fl.findAll();

		String[] locationsComboBox = new String[locationsFoundList.size() + 1];
		locationsComboBox[0] = "--Localisation--";

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			locationsComboBox[tabIndex] = locationsFoundList.get(listIndex).getBuildingId() + " - " + locationsFoundList.get(listIndex).getRoomId() + " - " + locationsFoundList.get(listIndex).getFloorId();
		}

		location = new JComboBox<Object>(locationsComboBox);
		//End Location





		//by default : state on
		// Managing state
		stateOnOff.setSelected(stateOn.getModel(), true);
		stateOn.setBackground(new Color(43,81,224));
		stateOff.setBackground(new Color(201,226,245));
		// End state

		// Add of parameters
		stateOnOff.add(stateOn);
		stateOnOff.add(stateOff);

		mainInfosPanel.add(dateInstallationLabel);
		mainInfosPanel.add(day);
		mainInfosPanel.add(month);
		mainInfosPanel.add(year);

		otherInfosPanel.add(addressIpLabel);
		otherInfosPanel.add(addressIp);
		otherInfosPanel.add(portLabel);
		otherInfosPanel.add(portSensor);

		locationPanel.add(location);
		statePanel.add(stateOn);
		statePanel.add(stateOff);


		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(containerNorth);
		container.add(containerSouth);

		containerNorth.setLayout(new BoxLayout(containerNorth, BoxLayout.Y_AXIS));
		containerNorth.add(BorderLayout.NORTH, mainInfosPanel);
		containerNorth.add(BorderLayout.NORTH, warningLevelPanel);
		containerNorth.add(BorderLayout.NORTH, otherInfosPanel);

		containerSouth.add(locationPanel);
		containerSouth.add(statePanel);

		container.add(infos);
		container.add(buttonConfigSensor);


		// the warning level depend of the type of the sensor
		switch (sensorFound.getType()) {
		case "TEMPERATURE" :
			warningLevelPanel.add(minLabel);
			warningLevelPanel.add(min);
			warningLevelPanel.add(minTempUnit);
			warningLevelPanel.add(maxLabel);
			warningLevelPanel.add(max);
			warningLevelPanel.add(maxTempUnit);
			break;
		case "PRESENCE":
			warningLevelPanel.add(dayLabel);
			warningLevelPanel.add(duringDay);
			warningLevelPanel.add(dayTimeUnit);
			warningLevelPanel.add(nightLabel);
			warningLevelPanel.add(duringNight);
			warningLevelPanel.add(nightTimeUnit);
			break;
		case "LIGHT":
			warningLevelPanel.add(dayLabel);
			warningLevelPanel.add(duringDay);
			warningLevelPanel.add(dayTimeUnit);
			warningLevelPanel.add(nightLabel);
			warningLevelPanel.add(duringNight);
			warningLevelPanel.add(nightTimeUnit);
			break;
		case "GASLEAK":
			warningLevelPanel.add(gasLabel);
			warningLevelPanel.add(gas);
			warningLevelPanel.add(gasUnit);
			break;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonConfigSensor) {
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

			// ------------ for type TEMPERATURE ------------
			if (sensorFound.getType().equals("TEMPERATURE") && 
					(min.getText().isEmpty() || max.getText().isEmpty())) {
				infos.setText("Vous devez renseigner les seuils de température");
			}

			// If min > max
			else if (sensorFound.getType().equals("TEMPERATURE") && 
					(Integer.parseInt(min.getText()) >= Integer.parseInt(max.getText()))) {
				infos.setText("La valeur minimum doit être strictement inferieure à la valeur maximum");
			}

			// ------------ For type LIGHT and PRESENCE ------------
			else if((sensorFound.getType().equals("LIGHT") || sensorFound.getType().equals("PRESENCE")) && 
					(duringDay.getText().isEmpty() || duringNight.getText().isEmpty())) {
				infos.setText("Vous devez renseigner les seuils de durée");
			}

			// For type GASLEAK
			else if(sensorFound.getType().equals("GASLEAK") && gas.getText().isEmpty()) {
				infos.setText("Vous devez renseigner le seuil de gaz");
			}

			// ------------ For all the types ------------
			else if(addressIp.getText().isEmpty() || portSensor.getText().isEmpty()) {
				infos.setText("Vous devez renseigner l'adresse IP et le port du capteur");
			}

			else if(portSensor.getText().length()<3) {
				infos.setText("Un port est constitué d'au moins 4 chiffres");
			}

			// If date = 0 : any selected location
			else if (day.getSelectedIndex() <= 0 || month.getSelectedIndex() <= 0 || year.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner une date valide");
			}

			// If index = 0 : Any selected location
			else if (location.getSelectedIndex() <= 0 ) {
				infos.setText("Veuillez selectionner une localisation valable");
			}

			else { 

				//Configuration depend of the type of the sensor
				switch (sensorFound.getType()) {
				case "TEMPERATURE":
					sensorFound.setMin(Integer.parseInt(min.getText().trim()));
					sensorFound.setMax(Integer.parseInt(max.getText().trim()));
					break;
				case "LIGHT":
					sensorFound.setMax(Integer.parseInt(duringNight.getText().trim()));
					sensorFound.setMin(Integer.parseInt(duringDay.getText().trim()));
					break;
				case "PRESENCE":
					sensorFound.setMax(Integer.parseInt(duringNight.getText().trim()));
					sensorFound.setMin(Integer.parseInt(duringDay.getText().trim()));
					break;
				case "GASLEAK":
					sensorFound.setMin(0);
					sensorFound.setMax(Integer.parseInt(gas.getText().trim()));
					break;
				case "FIRE":
					sensorFound.setMin(0);
					sensorFound.setMax(2);
					break;	
				}

				sensorFound.setState(stateOnOff.isSelected(stateOn.getModel()));
				sensorFound.setIpAddress(addressIp.getText().trim());
				sensorFound.setPort(portSensor.getText().trim());
				sensorFound.setIdLocation(locationsFoundList.get(location.getSelectedIndex()-1).getId());

				// Now the sensor is configured
				sensorFound.setConfigure(true);

				sensorFound.setAlert(false);
				sensorFound.setBreakdown(false);

				int dayInstallation = day.getSelectedIndex();
				int monthInstallation = month.getSelectedIndex() - 1;
				int yearInstallation = Integer.parseInt(years[year.getSelectedIndex()]);

				@SuppressWarnings("deprecation")
				Date dateInstallation = new Date(yearInstallation - 1900,
						monthInstallation, dayInstallation);

				sensorFound.setInstallation(dateInstallation);

				JSONObject sensorConfigJson = new JSONObject();
				sensorConfigJson.put("id", sensorFound.getId());
				sensorConfigJson.put("sensorToUpdate", sensorFound.toJSON());

				Request thirdRequest = new Request();
				thirdRequest.setType("UPDATE");
				thirdRequest.setEntity("SENSOR");
				thirdRequest.setFields(sensorConfigJson);

				ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
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

		// Managing state (ON/OFF)
		else if(e.getSource() == stateOn) {
			stateOnOff.setSelected(stateOn.getModel(), true);
			stateOn.setBackground(new Color(43,81,224));
			stateOff.setBackground(new Color(201,226,245));

		}

		else if(e.getSource() == stateOff) {
			stateOnOff.setSelected(stateOff.getModel(), true);
			stateOff.setBackground(new Color(43,81,224));
			stateOn.setBackground(new Color(201,226,245));
		}

	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}






}
