package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.JPanel;
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
import fr.pds.floralis.commons.bean.entity.TypeSensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;


public class WindowConfig extends JFrame implements ActionListener {

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
	JPanel containerNorth = new JPanel();
	JPanel containerSouth = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	JPanel locationPanel = new JPanel();
	JPanel statePanel = new JPanel();

	JComboBox<Object> location = new JComboBox<Object>();

	JTextField dateInstallation = new JTextField(10);
	JLabel dateInstallationLabel = new JLabel("Date d'installation :");
	
	JTextField addressIp = new JTextField(10);
	JLabel addressIpLabel = new JLabel("Address IP :");
	
	JTextField portSensor = new JTextField(4);
	JLabel portLabel = new JLabel("port :");
	
	JButton stateOn = new JButton("ON");
	JButton stateOff = new JButton("OFF");
	ButtonGroup state = new ButtonGroup();

	Button buttonConfigSensor = new Button("Configurer");

	JComboBox<Object> day = new JComboBox<Object>();

	String[] days = new String[32];

	JComboBox<Object> month = new JComboBox<Object>();

	String[] months = new String[13];

	JComboBox<Object> year = new JComboBox<Object>();

	String[] years = new String[12];

	JTextField min = new JTextField(5);
	JLabel minLabel = new JLabel("Seuil Min :");

	JTextField max = new JTextField(5);
	JLabel maxLabel = new JLabel("Seuil Max :");

	// Parameters for locations
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

	boolean success = false;
	
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
		
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		buttonConfigSensor.addActionListener(this);
		stateOn.addActionListener(this);
		stateOff.addActionListener(this);
		

		container.setPreferredSize(new Dimension(LG + 200, HT + 15));

		this.setTitle("Floralis - Configuration du capteur " + sensorFound.getId());
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);

		//Instalation day
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
			String yearMax = (yearIndex + 2018) + "";
			years[yearIndex] = yearMax;
		}

		day = new JComboBox<Object>(days);

		month = new JComboBox<Object>(months);

		year = new JComboBox<Object>(years);
		//End installation day

		//Location
		FindAllLocation fl = new FindAllLocation(host, port);
		locationsFoundList = fl.findAll(false);

		String[] locationsComboBox = new String[locationsFoundList.size() + 1];
		locationsComboBox[0] = "--Localisation--";

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			locationsComboBox[tabIndex] = locationsFoundList.get(listIndex).getBuildingId() + " - " + locationsFoundList.get(listIndex).getRoomId() + " - " + locationsFoundList.get(listIndex).getFloorId();
		}

		location = new JComboBox<Object>(locationsComboBox);
		//End Location

		state.add(stateOn);
		state.add(stateOff);
		stateOn.isSelected();

		mainInfosPanel.add(dateInstallationLabel);
		mainInfosPanel.add(day);
		mainInfosPanel.add(month);
		mainInfosPanel.add(year);
		
		mainInfosPanel.add(minLabel);
		mainInfosPanel.add(min);
		mainInfosPanel.add(maxLabel);
		mainInfosPanel.add(max);
		
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
		containerNorth.add(BorderLayout.NORTH, otherInfosPanel);
		
		containerSouth.add(locationPanel);
		containerSouth.add(statePanel);
		
		container.add(infos);
		container.add(buttonConfigSensor);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonConfigSensor) {
			try {
				Integer.parseInt(min.getText());
				Integer.parseInt(max.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("Les seuils ne peuvent contenir que des chiffres");
			}

			if (min.getText().isEmpty() || max.getText().isEmpty()) {
				infos.setText("Vous devez renseigner les seuils");
			}
			
			else if(addressIp.getText().isEmpty() || portSensor.getText().isEmpty()) {
				infos.setText("Vous devez renseigner l'adresse IP et le port du capteur");
			}

			// If date = 0 : any selected location
			else if (day.getSelectedIndex() <= 0 || month.getSelectedIndex() <= 0 || year.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner une date valide");
			}

			// If index = 0 : Any selected location
			else if (location.getSelectedIndex() <= 0 ) {
				infos.setText("Veuillez selectionner une localisation valable");
			}

			// If min > max
			else if (Integer.parseInt(min.getText()) > Integer.parseInt(max.getText())) {
				infos.setText("La valeur minimum doit être inferieure à la valeur maximum");
			}


			// End Sensor Find By Id

			else {
				Sensor sensorConfig = new Sensor();
				sensorConfig.setBrand(sensorFound.getBrand());
				sensorConfig.setMacAddress(sensorFound.getMacAddress());
				sensorConfig.setId(getId());
				sensorConfig.setType(sensorFound.getType());
				
				//Configuration
				sensorConfig.setMin(min.getText().trim());
				sensorConfig.setMax(max.getText().trim());
				
				sensorConfig.setState(true);
				sensorConfig.setIpAddress(addressIp.getText().trim());
				sensorConfig.setPort(portSensor.getText().trim());
				sensorConfig.setIdLocation(locationsFoundList.get(location.getSelectedIndex()-1).getId());
				
				// Now the sensor is configured
				sensorConfig.setConfigure(true);
				
				sensorConfig.setAlert(false);
				sensorConfig.setBreakdown(false);

				int dayInstallation = day.getSelectedIndex();
				int monthInstallation = month.getSelectedIndex() - 1;
				int yearInstallation = Integer.parseInt(years[year.getSelectedIndex()]);

				@SuppressWarnings("deprecation")
				Date dateInstallation = new Date(yearInstallation - 1900,
						monthInstallation, dayInstallation);

				sensorConfig.setInstallation(dateInstallation);
				
				JSONObject sensorConfigJson = new JSONObject();
				sensorConfigJson.put("id", sensorConfig.getId());
				sensorConfigJson.put("sensorToUpdate", sensorConfig.toJSON());

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
				
				success = true;
			}


		}
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
	
	

}
