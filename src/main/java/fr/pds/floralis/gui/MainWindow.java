package fr.pds.floralis.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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

public class MainWindow extends Thread implements ActionListener, Runnable {
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
	JScrollPane locationScrollList = new JScrollPane();

	/**
	 * Buttons for the sesnors
	 */
	Button buttonDeleteSensor = new Button("Supprimer le capteur");
	Button buttonUpdateSensor = new Button("Modifier les infos du capteur");
	Button buttonUpdateSensorState = new Button("Allumer/Eteindre");
	Button buttonRefreshSensor = new Button("Refresh");

	/**
	 * JMenubar and its componants
	 */
	JMenuBar menuBar = new JMenuBar();
	JMenu adding = new JMenu("Ajouts");
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur");
	JMenuItem addingLocation = new JMenuItem("Ajouter une localisation");

	/**
	 * Button for the locations
	 */
	Button buttonRefreshLocation = new Button("Rafraichir");

	/**
	 * Elements of the sensor panel
	 */
	JComboBox<Object> comboSensors;
	List<Sensor> sensorsFoundList;
	private JTable sensorsTable;
	SensorTableModel sensorModel;

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

		
		// Beginning of the sensor FindAll
		findAllSensor fs = new findAllSensor(host, port);
		sensorsFoundList = fs.findAll(false);
		// End of the sensor FindAll
		
		SensorTableModel sensorModel = new SensorTableModel(sensorsFoundList);
		sensorsTable = new JTable(sensorModel) {
			// See WindowConfirm for serialVersionUID
			private static final long serialVersionUID = 5025433811409489149L;
		};
		sensorsTable.setEnabled(false);

		JScrollPane paneSensors = new JScrollPane(sensorsTable);

		// ComboBox d'identifiants pour les capteurs

		// On créé un tableau de ce que contient le tableau de capteur
		// getRowCount + 1 car getRowCount renvoie la taille de ce qui a dans le
		// tableau (exemple 3,
		// éuivalent à sensorsList.size()), sachant que l'indice du tableau 0
		// est pris
		// (--identifiant du capteur--), il faut que notre tableau
		// ait une case en plus que le nombre d'éléments dans le tableau du
		// capteur
		// (index [0] + 1 index pour chaque élément --> tableau de taille 4 donc
		// getRowCount() + 1)
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

		
		// Mise en place des raccourcis
		addingSensor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				InputEvent.ALT_DOWN_MASK));
		addingLocation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.ALT_DOWN_MASK));

		
		buttonDeleteSensor.addActionListener(this);
		buttonUpdateSensor.addActionListener(this);
		buttonUpdateSensorState.addActionListener(this);
		buttonRefreshSensor.addActionListener(this);
		addingSensor.addActionListener(this);
		addingLocation.addActionListener(this);
		buttonRefreshLocation.addActionListener(this);

		
		locationPanel.setBorder(BorderFactory.createTitledBorder("Localisations"));
		sensorsPanel.setBorder(BorderFactory
				.createTitledBorder("Liste des capteurs"));

		
		// Elements les uns à côté des autres
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

		// Elements un en dessous des autres
		locationList.setLayout(new BoxLayout(locationList, BoxLayout.Y_AXIS));
		locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setBounds(0, 0, screenSize.width, screenSize.height);

		buttonRefreshLocation.setMaximumSize(new Dimension(80, 40));
		locationList.setMaximumSize(new Dimension(screenSize.width - 900, screenSize.height - 400));
		locationPanel.setMaximumSize(new Dimension(screenSize.width - 900, screenSize.height - 400));
		sensorsPanel.setMaximumSize(new Dimension(900, screenSize.height - 400));
		northPanel.setMaximumSize(new Dimension(screenSize.width, screenSize.height - 400));
		southPanel.setMaximumSize(new Dimension(screenSize.width, 400));
		
		
		// Début de la récupération des localisations
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("LOCATION");
		request.setFields(new JSONObject());
		ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, request.toString());
		ccLocationFindAll.run();

		String retourCcLocationFindAll = ccLocationFindAll.getResponse();
		JSONObject locationsFound = new JSONObject();	
		locationsFound.put("locationsFound", retourCcLocationFindAll);

		Location [] locationsFoundTab =  objectMapper.readValue(locationsFound.get("locationsFound").toString(), Location[].class);
		List <Location> locationsFoundList = Arrays.asList(locationsFoundTab);
		// Fin de la récupération des localisations

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			if(!(locationsFoundTab[listIndex].getSensorId().isEmpty())) {
				String sensorsIdString = locationsFoundTab[listIndex].getSensorId().stream().map(Object::toString)
	                    .collect(Collectors.joining(", "));
			locationList.add(new JLabel(locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName() + 
					" - Identifiants des capteurs à cet endroit : " + sensorsIdString));
			}
		}
		
		
		locationPanel.add(buttonRefreshLocation);
		locationPanel.add(locationList);
		sensorsPanel.add(infoSensorsPanel);	
		sensorsPanel.add(new JScrollPane(paneSensors));
		northPanel.add(locationPanel);
		northPanel.add(sensorsPanel);
		mainContainer.add(northPanel);
		mainContainer.add(southPanel);


		// Bordures vides pour l'espace entre les deux gros panneaux
		northPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		southPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		// Bordures vides pour l'espace dans le panneau des capteurs (du padding)
		locationList.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		// Bordures vides pour toute la fenêtre
		mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addingSensor) {
			try {
				new WindowAdd(getHost(), getPort()).initAddSensor();
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			} catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (e.getSource() == addingLocation) {
			try {
				new WindowAdd(getHost(), getPort()).initAddLocation();
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
			Request request = new Request();
			request.setType("FINDALL");
			request.setEntity("LOCATION");
			request.setFields(new JSONObject());
			ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, request.toString());
			ccLocationFindAll.run();

			String retourCcLocationFindAll = ccLocationFindAll.getResponse();
			JSONObject locationsFound = new JSONObject();	
			locationsFound.put("locationsFound", retourCcLocationFindAll);

			Location[] locationsFoundTab;

			// On retire tout ce qui est contenu dans le panneau locationList
			locationList.removeAll();
			locationList.revalidate();
			locationList.repaint(); 

			try {
				locationsFoundTab = objectMapper.readValue(
						locationsFound.get("locationsFound").toString(), Location[].class);

				for (int listIndex = 0; listIndex < locationsFoundTab.length; listIndex++) {
					if(!(locationsFoundTab[listIndex].getSensorId().isEmpty())) {
						String sensorsIdString = locationsFoundTab[listIndex].getSensorId().stream().map(Object::toString)
			                    .collect(Collectors.joining(", "));
					locationList.add(new JLabel(locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName() + 
							" - Identifiants des capteurs à cet endroit : " + sensorsIdString));
					}
				}

				// On retire tout ce qui est dans le panneau des localisations et on y rajoute les composants modifiés
				locationPanel.removeAll();
				locationPanel.add(buttonRefreshLocation);
				locationPanel.add(locationList);

			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == buttonRefreshSensor) {
			
			try {
				findAllSensor fs = new findAllSensor(host, port);
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
			
		}
		
		if (e.getSource() == buttonUpdateSensorState) {
			int indexSensor = comboSensors.getSelectedIndex();
			
			Sensor sensorUpdateState = sensorsFoundList.get(indexSensor - 1);
			if (sensorUpdateState.getState() == true) {
				sensorUpdateState.setState(false);
			} else {
				sensorUpdateState.setState(true);
			}

			JSONObject sensorUpdateStateJson = new JSONObject(sensorUpdateState);
			
			Request request = new Request();		
			request.setType("UPDATE");
			request.setEntity("SENSOR");
			request.setFields(sensorUpdateStateJson);
			
			ConnectionClient ccSensorUpdateState = new ConnectionClient(host, port, request.toString());
			ccSensorUpdateState.run();
			// Fin du sensorUpdate 
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

				int idLocationUpdate = sensorsFoundList.get(indexSensor - 1).getIdLocation();

				JSONObject sensorFoundDelete = new JSONObject();

				// On ajout dans cet object une clé "id" dont la valeur est
				// idSensor
				// { "id" : idSensor valeur } ;
				sensorFoundDelete.put("id", idLocationUpdate);

				// Si sure est à true alors on lance la supression en insérant
				// l'object JSON contenant l'id
				if (sure) {
					Request request = new Request();		
					request.setType("DELETE");
					request.setEntity("SENSOR");
					request.setFields(sensorFoundDelete);
					
					ConnectionClient ccSensorDelete = new ConnectionClient(host, port, request.toString());
					ccSensorDelete.run();

					// Ici, il faut récupérer la localisation qui est associée au capteur pour 
					// supprimer dans cette localisation l'occurence du capteur supprimé
					Request secondRequest = new Request();		
					secondRequest.setType("FINDBYID");
					secondRequest.setEntity("LOCATION");
					secondRequest.setFields(sensorFoundDelete);
					
					ConnectionClient ccLocationFindById = new ConnectionClient(host, port, secondRequest.toString());
					ccLocationFindById.run();

					String retoursLocationFindById = ccLocationFindById.getResponse();
					JSONObject locationFoundJson = new JSONObject();	
					locationFoundJson.put("retourLocation", retoursLocationFindById);

					ObjectMapper objectMapper = new ObjectMapper();
					Location locationFound;

					try {
						locationFound = objectMapper.readValue(
								locationFoundJson.get("retourLocation").toString(), Location.class);

						// Ici, on récupère tous les id des sensors de la localisation trouvée
						List <Integer> oldListSensorLocation = new ArrayList<Integer>();
						// On créer un nouveau tableau
						List <Integer> newListSensorLocation = new ArrayList<Integer>();
						

						// On ajoute dans ce nouveau tableau, tous les capteurs sauf celui qu'on vient de supprimer
						if(!oldListSensorLocation.contains(idSensorDelete)) {
							newListSensorLocation.addAll(oldListSensorLocation);
						}
						else {
							newListSensorLocation.addAll(oldListSensorLocation);
							newListSensorLocation.add(idSensorDelete);
						}

						// On modifie en mettant notre nouveau tableau puis on fait l'update sur la table des localisations
						locationFound.setSensorId(newListSensorLocation);
						JSONObject parametersLocation = new JSONObject(locationFound);	

						Request third = new Request();		
						third.setType("UPDATE");
						third.setEntity("LOCATION");
						third.setFields(parametersLocation);
						
						ConnectionClient ccLocationUpdate = new ConnectionClient(host, port, third.toString());
						ccLocationUpdate.run();

					} catch (JSONException | IOException e1) {
						e1.printStackTrace();
					}

				}
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

				int idSensor = sensorsFoundList.get(indexSensor - 1).getId();

				try {
					new WindowUpdate(getHost(), getPort()).initUpdateSensor(idSensor);
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}

		}

	}

	// Méthode appelée par le frame.start du main
	public void run() {
		try {
			init();
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
