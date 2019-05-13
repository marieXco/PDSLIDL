package fr.pds.floralis.gui;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;
import fr.pds.floralis.gui.tablemodel.SensorTableModel;

public class MainWindow extends Thread implements ActionListener, Runnable  {
	private String host;
	private int port;

	ObjectMapper objectMapper = new ObjectMapper();

	static JFrame window = new JFrame();

	/**
	 * Object to notify the end of the JFrame
	 */
	public final Object valueWait = new Object();

	/**
	 * Principal container 
	 */
	JPanel mainContainer = new JPanel();

	/**
	 * Two main panels, one above the other one
	 */
	JPanel southPanel = new JPanel();
	JPanel northPanel = new JPanel();

	/**
	 * Panel for the sensors : info for the buttons, the comboBox
	 * The other one for the sensor table
	 */
	JPanel sensorsPanel = new JPanel();
	JPanel infoSensorsPanel = new JPanel();

	/**
	 * Location panels
	 */
	JPanel locationPanel = new JPanel();
	JPanel locationList = new JPanel();
	JPanel messagePanel = new JPanel();
	JScrollPane locationScrollList = new JScrollPane();
	String text = "Aucun message pour le moment :)";
	JLabel message = new JLabel(text);

	/**
	 * Buttons for the sensors
	 */

	//Changing the text of the button if the selected sensor is configured or not
	String configuration = "Configuration";
	String toConfigure = "Configurer le capteur";
	String deleteConfig = "Supprimer la configuration";

	//Changing the text of the button if the selected sensor is turned on or not
	String onOff = "ON/OFF";
	String on = "ON";
	String off ="OFF";

	JButton buttonDeleteSensor = new JButton("Supprimer le capteur");
	JButton buttonUpdateSensor = new JButton("Modifier les infos du capteur");
	JButton buttonUpdateSensorState = new JButton(onOff);
	JButton buttonRefreshSensor = new JButton("Capteurs");
	JButton buttonNoConfigSensor = new JButton("Capteurs non configurés");
	JButton buttonYesConfigSensor = new JButton("Capteurs déjà configurés");
	JButton buttonConfigSensor = new JButton (configuration);
	JButton buttonUpdateConfig = new JButton ("Modifier les seuils");

	/**
	 * JMenubar and its componants
	 */
	JMenuBar menuBar = new JMenuBar();
	JMenu adding = new JMenu("Ajouts");
	JMenu mapping = new JMenu("Map");
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur");
	JMenuItem addingLocation = new JMenuItem("Ajouter une localisation");
	JMenuItem showingMap = new JMenuItem("Consulter la map des capteurs");
	JMenu indicators = new JMenu("Indicateurs"); 
	JMenuItem stats = new JMenuItem("Consulter les indicateurs");

	/**
	 * Button for the locations
	 */
	JButton buttonRefreshLocation = new JButton("Rafraichir");

	/**
	 * Elements of the sensor panel
	 */
	JComboBox<Object> comboSensors;
	List<Sensor> sensorsFoundList;
	private JTable sensorsTable;
	SensorTableModel sensorModel;


	/**
	 * Last selected button
	 * a flag need for refresh
	 * Refresh is different if is for all sensors, configured or no configured sensors
	 * 1 for all
	 * 2 for configured
	 * 3 for no configured
	 */
	int last = 1;

	/**
	 * for a refresh periodically
	 */
	int countMessage = 0;
	int countSensor = 0;

	/**
	 * Number of alert at the initialization of window
	 * Number of alert at the refresh
	 */
	int countAlert = 0;
	int countNewAlert;
	
	int countSensors = 0;
	int countNewSensor;
	
	int countLocation = 0;
	int countNewLocation;
	
	int countWarningLevel = 0;
	int countNewWarningLevel;
	

	/** 
	 * Constructor, takes the host and port from the main
	 * @param host
	 * @param port
	 */
	public MainWindow(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	/**
	 * Principal method, displays the Jframe
	 * @throws SQLException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws JSONException 
	 */
	public void init() throws SQLException, JsonParseException,
	JsonMappingException, IOException, JSONException, InterruptedException {
		setHost(host);
		setPort(port);


		window.setJMenuBar(menuBar);
		menuBar.add(adding);
		menuBar.add(mapping);
		adding.add(addingSensor);
		adding.add(addingLocation);
		mapping.add(showingMap);
		menuBar.add(indicators);
		indicators.add(stats);


		// Beginning of the sensor FindAll
		FindAllSensor fs = new FindAllSensor(host, port);
		sensorsFoundList = fs.findAll(false);
		// End of the sensor FindAll

		SensorTableModel sensorModel = new SensorTableModel(sensorsFoundList);
		sensorsTable = new JTable(sensorModel) {
			// See WindowConfirm for serialVersionUID
			private static final long serialVersionUID = 5025433811409489149L;
		};
		sensorsTable.setEnabled(false);

		JScrollPane paneSensors = new JScrollPane(sensorsTable);

		String[] sensorsComboBox = new String[sensorModel.getRowCount() + 1];
		sensorsComboBox[0] = "-- Identifiant du capteur --";

		for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() + " ";
		}

		comboSensors = new JComboBox<Object>(sensorsComboBox);


		infoSensorsPanel.add(comboSensors);
		infoSensorsPanel.add(buttonDeleteSensor);
		infoSensorsPanel.add(buttonUpdateSensor);
		infoSensorsPanel.add(buttonUpdateConfig);
		infoSensorsPanel.add(buttonConfigSensor);
		infoSensorsPanel.add(buttonUpdateSensorState);
		infoSensorsPanel.add(buttonRefreshSensor);
		infoSensorsPanel.add(buttonYesConfigSensor);
		infoSensorsPanel.add(buttonNoConfigSensor);


		// Keyboard shortcut
		addingSensor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				InputEvent.ALT_DOWN_MASK));
		addingLocation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.ALT_DOWN_MASK));

		comboSensors.addActionListener(this);
		buttonDeleteSensor.addActionListener(this);
		buttonUpdateSensor.addActionListener(this);
		buttonUpdateSensorState.addActionListener(this);
		buttonRefreshSensor.addActionListener(this);
		addingSensor.addActionListener(this);
		showingMap.addActionListener(this);
		addingLocation.addActionListener(this);
		buttonConfigSensor.addActionListener(this);
		buttonRefreshLocation.addActionListener(this);
		buttonNoConfigSensor.addActionListener(this);
		buttonYesConfigSensor.addActionListener(this);
		buttonUpdateConfig.addActionListener(this);
		stats.addActionListener(this);



		locationPanel.setBorder(BorderFactory.createTitledBorder("Localisations"));
		sensorsPanel.setBorder(BorderFactory.createTitledBorder("Liste des capteurs"));
		messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));


		// Elements are one next to the other
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

		// Elements are one below the other
		locationList.setLayout(new BoxLayout(locationList, BoxLayout.Y_AXIS));
		locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		messagePanel.setLayout(new BorderLayout());


		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		//get maximum window bounds
		Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();

		window.setBounds(0, 0, (int)maximumWindowBounds.getWidth() - 60, (int)maximumWindowBounds.getHeight() - 20);

		buttonRefreshLocation.setMaximumSize(new Dimension(80, 40));
		infoSensorsPanel.setMaximumSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height - 300));
		locationList.setMaximumSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height - 400));
		locationPanel.setMaximumSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height - 400));
		sensorsPanel.setMaximumSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height - 400));
		messagePanel.setMaximumSize(new Dimension(maximumWindowBounds.width, 100));
		northPanel.setMaximumSize(new Dimension(maximumWindowBounds.width, 500));
		southPanel.setMaximumSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height - 600));


		// Beginning of the locations recovery
		FindAllLocation fl = new FindAllLocation(host, port);
		List <Location>locationsFoundList = fl.findAll(false);
		// End of the locations recovery

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			if(!(locationsFoundList.get(listIndex).getSensorId().isEmpty())) {
				String sensorsIdString = locationsFoundList.get(listIndex).getSensorId().stream().map(Object::toString)
						.collect(Collectors.joining(", "));
				locationList.add(new JLabel(locationsFoundList.get(listIndex).getBuildingId() + " - " + locationsFoundList.get(listIndex).getRoomId() + " - " + locationsFoundList.get(listIndex).getFloorId() + 
						" - Identifiants des capteurs à cet endroit : " + sensorsIdString));
			}
		}


		locationPanel.add(buttonRefreshLocation, BorderLayout.CENTER);
		locationPanel.add(locationList);
		sensorsPanel.add(infoSensorsPanel);	
		sensorsPanel.add(new JScrollPane(paneSensors));
		southPanel.add(sensorsPanel);
		northPanel.add(locationPanel);
		mainContainer.add(messagePanel);
		mainContainer.add(northPanel);
		mainContainer.add(southPanel);

		messagePanel.add(message);
		message.setHorizontalAlignment(SwingConstants.CENTER);

		// Empty border for the space between the two panel
		northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		// Empty border for the space between the two panel
		locationList.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		// Empty border for all the window
		mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3));

		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setContentPane(mainContainer);
		window.setTitle("Floralis");
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// If the window is closed
		// Then the main is notify
		window.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				synchronized (valueWait) {
					window.setVisible(false);
					valueWait.notify();
				}
			}
		});


	}

	// To display all sensors in the table
	public void allSensors() {
		try {
			FindAllSensor fs = new FindAllSensor(host, port);
			sensorsFoundList = fs.findAll(false);

			SensorTableModel sensorModelRefresh = new SensorTableModel(sensorsFoundList);

			String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1]; 
			sensorsComboBox[0]= "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() + " ";
			}

			comboSensors.removeAllItems();

			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors.addItem(sensorsComboBox[i]);
			}

			sensorsTable.setModel(sensorModelRefresh);

		} catch (JSONException | IOException | InterruptedException e2) {
			e2.printStackTrace();
		}
		System.out.println(" Refresh -- Sensors - count : " + countSensor);
	}

	// To display just configured sensors in the table
	public void yesConfigSensors() {
		try {
			FindSensorByConfig fs = new FindSensorByConfig(host, port);
			sensorsFoundList = fs.findByConfig(false, true);

			SensorTableModel sensorModelRefresh = new SensorTableModel(sensorsFoundList);

			String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1]; 
			sensorsComboBox[0]= "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() + " ";
			}

			comboSensors.removeAllItems();

			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors.addItem(sensorsComboBox[i]);
			}

			sensorsTable.setModel(sensorModelRefresh);

		} catch (JSONException | IOException | InterruptedException e2) {
			e2.printStackTrace();
		}
		System.out.println(" Refresh -- Sensors Yes config - count : " + countSensor);
	}

	// To display just no configured sensors in the table
	public void noConfigSensors() {
		try {
			FindSensorByConfig fs = new FindSensorByConfig(host, port);
			sensorsFoundList = fs.findByConfig(false, false);

			SensorTableModel sensorModelRefresh = new SensorTableModel(sensorsFoundList);

			String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1]; 
			sensorsComboBox[0]= "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() + " ";
			}

			comboSensors.removeAllItems();

			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors.addItem(sensorsComboBox[i]);
			}

			sensorsTable.setModel(sensorModelRefresh);

		} catch (JSONException | IOException | InterruptedException e2) {
			e2.printStackTrace();
		}
		System.out.println(" Refresh -- Sensors No config - count : " + countSensor);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addingSensor) {
			try {
				new WindowAdd().run();
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			}

		}

		if (e.getSource() == addingLocation) {
			try {
				new WindowAdd().initAddLocation();
			} catch (JsonParseException e1) {

				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}


		if (e.getSource() == stats) {
			try {
				new WindowStats(getHost(), getPort()).initIndicators();
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}
		
		if (e.getSource() == showingMap) {
			
				new WindowMap(getHost(), getPort()).initMap();
			
		}


		if (e.getSource() == buttonRefreshLocation) {

			FindAllLocation fl = new FindAllLocation(host, port);
			List<Location> locationsFoundList;

			try {
				locationsFoundList = fl.findAll(false);

				locationList.removeAll();
				locationList.revalidate();
				locationList.repaint(); 

				for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
					if(!(locationsFoundList.get(listIndex).getSensorId().isEmpty())) {
						String sensorsIdString = locationsFoundList.get(listIndex).getSensorId().stream().map(Object::toString)
								.collect(Collectors.joining(", "));
						locationList.add(new JLabel(locationsFoundList.get(listIndex).getBuildingId() + " - " + locationsFoundList.get(listIndex).getRoomId() + " - " + locationsFoundList.get(listIndex).getFloorId() + 
								" - Identifiants des capteurs à cet endroit : " + sensorsIdString));
					}
				}

			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			locationPanel.removeAll();
			locationPanel.add(buttonRefreshLocation);
			locationPanel.add(locationList);

		}

		if (e.getSource() == buttonRefreshSensor) {
			last = 1;
			allSensors();
		}

		if (e.getSource() == buttonUpdateSensorState) {
			int indexSensor = comboSensors.getSelectedIndex();
			String stateChange;
			if(indexSensor > 0) {
				Sensor sensorUpdateState = sensorsFoundList.get(indexSensor - 1);
				if(sensorUpdateState.getState()) stateChange = "Eteind";
				else stateChange = "Allumé";
				if(sensorUpdateState.getConfigure()) {
					if(!sensorUpdateState.getAlert()) {
						if (sensorUpdateState.getState()) {
							sensorUpdateState.setState(false);
						} else {
							sensorUpdateState.setState(true);
						}

						JSONObject sensorUpdateStateJson = new JSONObject();
						sensorUpdateStateJson.put("id", sensorUpdateState.getId());
						sensorUpdateStateJson.put("sensorToUpdate", sensorUpdateState.toJSON());

						Request request = new Request();		
						request.setType("UPDATE");
						request.setEntity("SENSOR");
						request.setFields(sensorUpdateStateJson);

						ConnectionClient ccSensorUpdateState = new ConnectionClient(host, port, request.toJSON().toString());
						ccSensorUpdateState.run();
						
						if(sensorUpdateState.getState()) message.setText("Le capteur " + sensorUpdateState.getId() + " a été allumé" );
						else message.setText("Le capteur " + sensorUpdateState.getId() + " a été éteind" );
							

					} else {
						message.setText("Le capteur " + sensorUpdateState.getId() + " est en alerte, il ne peut pas être " + stateChange);
						message.setForeground(Color.BLACK);
					}
				} else {
					message.setText("Le capteur " + sensorUpdateState.getId() + " n'est pas configuré, il ne peut pas être " + stateChange);
					message.setForeground(Color.BLACK);
				}
			} else {
				message.setText("Vous devez sélectionner un capteur");
				message.setForeground(Color.BLACK);
			}
			
			refresh(last);

		}


		if (e.getSource() == buttonDeleteSensor) {
			int indexSensor = comboSensors.getSelectedIndex();

			if (indexSensor > 0) {
				Sensor SensorDelete = sensorsFoundList.get(indexSensor - 1);
				if(!SensorDelete.getAlert()) {
					JSONObject objSensorDelete = new JSONObject();
					objSensorDelete.put("id", SensorDelete.getId());

					boolean sure = new WindowConfirm().init("supprimer ce capteur");

					if (sure) {
						Request request = new Request();		
						request.setType("DELETE");
						request.setEntity("SENSOR");
						request.setFields(objSensorDelete);

						ConnectionClient ccSensorDelete = new ConnectionClient(host, port, request.toJSON().toString());
						ccSensorDelete.run();

						message.setText("Le capteur " + SensorDelete.getId() + " a été supprimé avec succès");
						message.setForeground(Color.BLACK);

						// TODO : Ici, il faut récupérer la localisation qui est associée au capteur pour 
						// supprimer dans cette localisation l'occurence du capteur supprimé
					}
				} else {
					message.setText("Le capteur " + SensorDelete.getId() + " est en alerte vous ne pouvez pas le supprimer");
					message.setForeground(Color.BLACK);
				}
			} else {
				message.setText("Vous devez selectionner l'identifiant du capteur à Supprimer");
				message.setForeground(Color.BLACK);
			}

		}

		if (e.getSource() == buttonUpdateSensor) {
			int indexSensor = comboSensors.getSelectedIndex();

			if (indexSensor > 0) {
				Sensor sensorUpdate = sensorsFoundList.get(indexSensor - 1);
				if(!sensorUpdate.getAlert()) {
					try {
						try {
							new WindowUpdate(getHost(), getPort()).initUpdateSensor(sensorUpdate.getId());
							refresh(last);
						} catch (HeadlessException | JSONException | InterruptedException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e1) {

						e1.printStackTrace();
					}
				} else {
					message.setText("Le capteur " + sensorUpdate.getId() + " est en alerte, vous ne pouvez pas le modifier" );
					message.setForeground(Color.BLACK);
				}
			} else {
				message.setText("Vous devez selectionner l'identifiant du capteur à Modifier");
				message.setForeground(Color.BLACK);
			}
		}

		if(e.getSource() == buttonUpdateConfig) {
			int indexSensor = comboSensors.getSelectedIndex();
			if(indexSensor > 0) {
				Sensor sensorUpdateWarningLevel = sensorsFoundList.get(indexSensor - 1);
				if(sensorUpdateWarningLevel.getConfigure()) {
					if(!sensorUpdateWarningLevel.getAlert()) {
						if(!sensorUpdateWarningLevel.getType().equals("FIRE")) {
						try {
							new WindowUpdateConfig().init(sensorUpdateWarningLevel.getId());
						} catch (JSONException | IOException e1) {
							e1.printStackTrace();
						}
						} else {
							message.setText("Le capteur " + sensorUpdateWarningLevel.getId() + " est de type feu, il n'a pas de seuils ");
							message.setForeground(Color.BLACK);
						}
					} else {
						message.setText("Le capteur " + sensorUpdateWarningLevel.getId() + " est en alerte, ses seuils ne peuvent pas être modifié" );
						message.setForeground(Color.BLACK);
					}
				} else {
					message.setText("Le capteur " + sensorUpdateWarningLevel.getId() + " n'est pas configuré, ses seuils ne peuvent pas être modifié " );
					message.setForeground(Color.BLACK);
				}
			} else {
				message.setText("Vous devez sélectionner un capteur");
				message.setForeground(Color.BLACK);
			}

		
		}


		if(e.getSource() == buttonConfigSensor) {
			int indexSensor = comboSensors.getSelectedIndex();

			if(indexSensor > 0) {
				Sensor sensorFoundConfig = sensorsFoundList.get(indexSensor - 1);
				if (!sensorFoundConfig.getAlert()) {
					if(!sensorFoundConfig.getConfigure()) {
						try {
							try {
								new WindowConfig(getHost(), getPort()).initConfigSensor(sensorFoundConfig.getId());
							} catch (HeadlessException | JSONException | InterruptedException e1) {
								e1.printStackTrace();
							}
						} catch (IOException e1) {

							e1.printStackTrace();
						}

					} else {
						boolean sure = new WindowConfirm().init("supprimer la configuration du capteur " + sensorFoundConfig.getId() );
						if (sure) {
							sensorFoundConfig.setMin(0);
							sensorFoundConfig.setMax(0);

							sensorFoundConfig.setState(false);
							sensorFoundConfig.setIpAddress(null);
							sensorFoundConfig.setPort(null);
							sensorFoundConfig.setIdLocation(0);
							sensorFoundConfig.setInstallation(null);

							// The sensor is no configured
							sensorFoundConfig.setConfigure(false);

							JSONObject sensorDeleteConfigJson = new JSONObject();
							sensorDeleteConfigJson.put("id", sensorFoundConfig.getId());
							sensorDeleteConfigJson.put("sensorToUpdate", sensorFoundConfig.toJSON());

							Request thirdRequest = new Request();
							thirdRequest.setType("UPDATE");
							thirdRequest.setEntity("SENSOR");
							thirdRequest.setFields(sensorDeleteConfigJson);

							ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
							ccSensorUpdate.run();

						}
					} 
				}
				else {
					message.setText("Le capteur " + sensorFoundConfig.getId() + " est en alerte, vous ne pouvez pas le configurer");
					message.setForeground(Color.BLACK);
				}
			} else {
				message.setText("Vous devez selectionner un capteur !");
				message.setForeground(Color.BLACK);
			}
		}


		if (e.getSource() == buttonNoConfigSensor) {
			last = 3;
			noConfigSensors();
		}


		if (e.getSource() == buttonYesConfigSensor) {
			last = 2;
			yesConfigSensors();
		}

		if(e.getSource() == comboSensors) {
			FindById di = new FindById(host, port);
			int indexSensor = comboSensors.getSelectedIndex();

			//if a sensor is selected
			if(indexSensor > 0) {
				int idSensor = sensorsFoundList.get(indexSensor - 1).getId();
				Sensor sensorSelected = new Sensor();
				try {
					sensorSelected = di.findById(false, idSensor);
				} catch (JSONException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				//if the selected sensor is configured
				if(sensorSelected.getConfigure() && !sensorSelected.getAlert()) {
					buttonConfigSensor.setText(deleteConfig);
					// if the selected sensor is turned on
					if(sensorSelected.getState()) {
						buttonUpdateSensorState.setText(off);
					} else {
						buttonUpdateSensorState.setText(on);
					}
				} else {
					buttonConfigSensor.setText(toConfigure);
				}
			} else {
				buttonConfigSensor.setText(configuration);
				buttonUpdateSensorState.setText(onOff);
			}
		}

	}

	//If there are a new alert, the sensors are refresh
	public void refreshNewElement() {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

		Runnable task1 = () -> {
			countMessage++;
			System.out.println(" Refresh - count : " + countMessage);
			FindAllSensor fs = new FindAllSensor(host, port);
			List<Sensor> sensorsFoundList = new ArrayList<Sensor>();
			try {
				sensorsFoundList = fs.findAll(false);
			} catch (JSONException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//a new sensor or a sensor delete
			countNewSensor = sensorsFoundList.size();
			// if it is the first connection
			if(countSensors == 0) countSensors = countNewSensor;
			
			// if there are new sensor or a sensor deleted, then countSensors is different than countNewSensor
			if(countSensors != countNewSensor) {
				refresh(last);
				countSensors = countNewSensor;
			} 
			
			// a new alert
			countNewAlert = 0;
			
			// count the number of alert
			for(Sensor s : sensorsFoundList) {
				if(s.getAlert()) countNewAlert++;
			}
			
			// if it is the first connection
			if(countNewAlert == 0) countAlert = 0;

			// if the number of alert is changing
			if(countAlert < countNewAlert) {
				refresh(last);
				if(countMessage > 1) new WindowAlert().init();
				countAlert = countNewAlert;
			} 
			
			// a new configuration
			countNewLocation = 0;
			
			// if there are a new configuration the sum of the id location is update
			for(Sensor s : sensorsFoundList) {
				countNewLocation+= s.getIdLocation();
			} 

			// if it is the first connection
			if(countLocation == 0) countLocation = countNewLocation;
			if(countLocation != countNewLocation) {
				refresh(last);
				countLocation = countNewLocation;
			} 
			
			// new warning level 
			countNewWarningLevel = 0;
			for(Sensor s : sensorsFoundList) {
				countNewWarningLevel+= s.getMin() + s.getMax(); 
			} 
			if(countWarningLevel == 0) countWarningLevel = countNewWarningLevel;
			if(countWarningLevel != countNewWarningLevel) {
				refresh(last);
				countWarningLevel = countNewWarningLevel;
			} 
			
		};

		ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 0 , 1, SECONDS);


		ses.schedule(new Runnable() {
			public void run() { 
				scheduledFuture.cancel(true); 
			}
		}, 3600, SECONDS
				);
	}

	// All the 30 seconds the sensors are refresh
	// This method is need if another client manage a sensor
	// Thus, the sensors are refresh 
	public void refreshSensors() {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

		Runnable task1 = () -> {
			countSensor++;
			refresh(last);
		};

		ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 0 , 30, SECONDS);


		ses.schedule(new Runnable() {
			public void run() { 
				scheduledFuture.cancel(true); 
			}
		}, 3600, SECONDS 
				);
	}

	// if the last selected button is all sensor, all sensor are refresh
	// if the last selected button is Yes Config sensor, configured sensor are refresh
	// if the last selected button is No Cnfig sensor, no configured sensor are refresh
	public void refresh(int last) {
		switch (last) {
		case 1 : 
			allSensors();
			break;
		case 2 : 
			yesConfigSensors();
			break;
		case 3 : 
			noConfigSensors();
			break;
		}
		if(countNewAlert > 0) {
			message.setText("Vous avez " + countNewAlert + " capteur(s) en alerte");
			message.setForeground(Color.RED);
		}
		else {
			message.setText(text);
			message.setForeground(Color.BLACK);
		}
	}


	public void run() {
		try {
			init();
			refreshNewElement();
			refreshSensors();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
