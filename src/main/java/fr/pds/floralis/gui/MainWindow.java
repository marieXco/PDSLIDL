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
	JButton buttonRefreshSensor = new JButton("Refresh");
	JButton buttonNoConfigSensor = new JButton("Voir les capteurs non configurés");
	JButton buttonYesConfigSensor = new JButton("Voir les capteurs déjà configurés");
	JButton buttonConfigSensor = new JButton (configuration);

	/**
	 * JMenubar and its componants
	 */
	JMenuBar menuBar = new JMenuBar();
	JMenu adding = new JMenu("Ajouts");
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur");
	JMenuItem addingLocation = new JMenuItem("Ajouter une localisation");
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
		adding.add(addingSensor);
		adding.add(addingLocation);	
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
		infoSensorsPanel.add(buttonUpdateSensorState);
		infoSensorsPanel.add(buttonRefreshSensor);
		infoSensorsPanel.add(buttonYesConfigSensor);
		infoSensorsPanel.add(buttonNoConfigSensor);
		infoSensorsPanel.add(buttonConfigSensor);


		// Mise en place des raccourcis
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
		addingLocation.addActionListener(this);
		buttonConfigSensor.addActionListener(this);
		buttonRefreshLocation.addActionListener(this);
		buttonNoConfigSensor.addActionListener(this);
		buttonYesConfigSensor.addActionListener(this);
		stats.addActionListener(this);



		locationPanel.setBorder(BorderFactory.createTitledBorder("Localisations"));
		sensorsPanel.setBorder(BorderFactory.createTitledBorder("Liste des capteurs"));
		messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));


		// Elements les uns à côté des autres
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

		// Elements un en dessous des autres
		locationList.setLayout(new BoxLayout(locationList, BoxLayout.Y_AXIS));
		locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		messagePanel.setLayout(new BorderLayout());


		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

		//get maximum window bounds
		Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();

		window.setBounds(0, 0, (int)maximumWindowBounds.getWidth(), (int)maximumWindowBounds.getHeight());

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

		// Bordures vides pour l'espace entre les deux gros panneaux
		northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		// Bordures vides pour l'espace dans le panneau des capteurs (du padding)
		locationList.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		// Bordures vides pour toute la fenêtre
		mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3));


		// DISPOSE --> ne ferme pas, laisse la place à la fenêtre de déconnection
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setContentPane(mainContainer);
		window.setTitle("Floralis");
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Si la fenêtre est fermée alors on notifie le main, 
		// que la fenêtre vient de se fermer
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
				WindowAdd toto = new WindowAdd(window);
				toto.run();
				System.out.println("ahahahahah ça marche putain");
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		if (e.getSource() == addingLocation) {
			try {
				new WindowAdd(window).initAddLocation();
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


		if (e.getSource() == buttonRefreshLocation) {
			// Ici, un refresh vient d'être fait sur les localisations, fonctionne comme pour les lignes 270-293
			FindAllLocation fl = new FindAllLocation(host, port);
			List<Location> locationsFoundList;

			try {
				locationsFoundList = fl.findAll(false);

				// On retire tout ce qui est contenu dans le panneau locationList
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}



			// On retire tout ce qui est dans le panneau des localisations et on y rajoute les composants modifiés
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

			if(indexSensor > 0) {
				Sensor sensorUpdateState = sensorsFoundList.get(indexSensor - 1);
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
				refresh(last);

			} else {
				message.setText("Vous devez sélectionner un capteur");
				message.setForeground(Color.BLACK);
			}	
		}


		if (e.getSource() == buttonDeleteSensor) {
			// Récupère l'index de la ComboBox
			int indexSensor = comboSensors.getSelectedIndex();

			// Si il est à 0, c'est qu'aucun vrai ID n'a été selectionné car index [0] = --id du capteur--
			if (indexSensor > 0) {
				// on récupère l'id du capteur contenu à l'index de la checkbox
				// - 1
				// Index checkbox : 3 est équivalant à l'index 2 du tableau des
				// capteurs
				int idSensorDelete = sensorsFoundList.get(indexSensor - 1).getId();

				// On créer un object de JSON
				JSONObject objSensorDelete = new JSONObject();

				// On ajout dans cet object une clé "id" dont la valeur est
				// idSensor
				// { "id" : idSensor valeur } ;
				objSensorDelete.put("id", idSensorDelete);

				// On lance une fênetre de confirmation qui renvoie 'true' si on
				// clique sur oui
				// 'false' pour le reste
				boolean sure = new WindowConfirm()
						.init("supprimer ce capteur");

				//int idLocationUpdate = sensorsFoundList.get(indexSensor - 1).getIdLocation();

				//JSONObject sensorFoundDelete = new JSONObject();

				// On ajout dans cet object une clé "id" dont la valeur est
				// idSensor
				// { "id" : idSensor valeur } ;
				//sensorFoundDelete.put("id", idLocationUpdate);

				// Si sure est à true alors on lance la supression en insérant
				// l'object JSON contenant l'id
				if (sure) {
					Request request = new Request();		
					request.setType("DELETE");
					request.setEntity("SENSOR");
					request.setFields(objSensorDelete);

					ConnectionClient ccSensorDelete = new ConnectionClient(host, port, request.toJSON().toString());
					ccSensorDelete.run();

					message.setText("Le capteur " + idSensorDelete + " a été supprimé avec succès");
					message.setForeground(Color.BLACK);
					refresh(last);

					// TODO : Ici, il faut récupérer la localisation qui est associée au capteur pour 
					// supprimer dans cette localisation l'occurence du capteur supprimé

				}
			} else {
				message.setText("Vous devez selectionner l'identifiant du capteur à Supprimer");
				message.setForeground(Color.BLACK);
			}

		}

		if (e.getSource() == buttonUpdateSensor) {
			// Récupère l'index de la ComboBox
			int indexSensor = comboSensors.getSelectedIndex();

			// Si il est à 0, c'est qu'aucun vrai ID n'a été selectionné car
			// index [0] = --id du capteur--
			if (indexSensor > 0) {

				// on récupère l'id du capteur contenu à l'index de la checkbox
				// - 1
				// Index checkbox : 3 est équivalant à l'index 2 du tableau des
				// capteurs
				System.out.println(sensorsFoundList.get(indexSensor - 1).getId());

				int idSensorUpdate = sensorsFoundList.get(indexSensor - 1).getId();

				try {
					try {
						new WindowUpdate(getHost(), getPort()).initUpdateSensor(idSensorUpdate);
					} catch (HeadlessException | JSONException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			} else {
				message.setText("Vous devez selectionner l'identifiant du capteur à Modifier");
				message.setForeground(Color.BLACK);
			}

			refresh(last);
		}


		if(e.getSource() == buttonConfigSensor) {
			int indexSensor = comboSensors.getSelectedIndex();

			if(indexSensor > 0) {
				int idSensorFound = sensorsFoundList.get(indexSensor - 1).getId();
				Sensor sensorFound = new Sensor();
				FindById co = new FindById(host, port);
				try {
					sensorFound = co.findById(false, idSensorFound);
				} catch (JSONException | IOException | InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (!sensorFound.getConfigure()) {
					System.out.println(sensorFound.getId());

					try {
						try {
							new WindowConfig(getHost(), getPort()).initConfigSensor(sensorFound.getId());
						} catch (HeadlessException | JSONException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} catch (IOException e1) {

						e1.printStackTrace();
					}

				} else if (sensorFound.getConfigure()) {
					boolean sure = new WindowConfirm().init("supprimer la configuration du capteur " + sensorFound.getId() );

					if (sure) {
						sensorFound.setMin(null);
						sensorFound.setMax(null);

						sensorFound.setState(false);
						sensorFound.setIpAddress(null);
						sensorFound.setPort(null);
						sensorFound.setIdLocation(0);
						sensorFound.setInstallation(null);

						// The sensor is no configured
						sensorFound.setConfigure(false);

						JSONObject sensorDeleteConfigJson = new JSONObject();
						sensorDeleteConfigJson.put("id", sensorFound.getId());
						sensorDeleteConfigJson.put("sensorToUpdate", sensorFound.toJSON());

						Request thirdRequest = new Request();
						thirdRequest.setType("UPDATE");
						thirdRequest.setEntity("SENSOR");
						thirdRequest.setFields(sensorDeleteConfigJson);

						ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
						ccSensorUpdate.run();
					}
				}

			} else {
				message.setText("Vous devez selectionner un capteur !");
				message.setForeground(Color.BLACK);
			}

			refresh(last);
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
				Sensor toto = new Sensor();
				try {
					toto = di.findById(false, idSensor);
				} catch (JSONException | IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				//if the selected sensor is configured
				if(toto.getConfigure()) {
					buttonConfigSensor.setText(deleteConfig);
					// if the selected sensor is turned on
					if(toto.getState()) {
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

	
	
	

	public void refreshAlert() {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

		Runnable task1 = () -> {
			countMessage++;
			System.out.println(" Refresh -- Alert - count : " + countMessage);
			FindAllSensor fs = new FindAllSensor(host, port);
			List<Sensor> sensorsFoundList = new ArrayList<Sensor>();
			try {
				sensorsFoundList = fs.findAll(false);
			} catch (JSONException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			countNewAlert = 0;
			for(Sensor s : sensorsFoundList) {
				if(s.getAlert()) countNewAlert++;
			}
			
			if(countNewAlert == 0) countAlert = 0;
			
			if(countAlert < countNewAlert) {
				refresh(last);
				System.out.println("Nombre de capteur en alert initialement " + countAlert);
				System.out.println("Nombre de capteur en alert maintenant " + countNewAlert);
				if(countMessage > 1) new WindowAlert().init();
				countAlert = countNewAlert;
			} 
			
			if(countNewAlert > 0) {
				message.setText("Vous avez " + countAlert + " capteur(s) en alerte");
				message.setForeground(Color.RED);
			}
			
			
		};

		ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 0 , 30, SECONDS);


		ses.schedule(new Runnable() {
			public void run() { 
				scheduledFuture.cancel(true); 
			}
		}, 360, SECONDS
				);
	}

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
		}, 360, SECONDS 
				);
	}
	

	public void refresh(int last) {
		if(last == 1) allSensors();
		if(last == 2) yesConfigSensors();
		if(last == 3) noConfigSensors();
		if(countNewAlert > 0) {
			message.setText("Vous avez " + countAlert + " capteur(s) en alerte");
			message.setForeground(Color.RED);
		}
		else {
			message.setText(text);
			message.setForeground(Color.BLACK);
		}
	}

	// Méthode appelée par le frame.start du main
	public void run() {
		try {
			init();
//			refreshMessage();
			refreshAlert();
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
