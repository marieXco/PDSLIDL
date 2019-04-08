package fr.pds.floralis.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;
import fr.pds.floralis.gui.tablemodel.SensorsTableModel;

public class MainWindow extends Thread implements ActionListener, Runnable {
	private String host;
	private int port;

	ObjectMapper objectMapper = new ObjectMapper();

	// Object pour lancer le top de la fin de la JFrame --> voir synchronized
	// dans le cours
	public final Object valueWait = new Object();
	static JFrame window = new JFrame();

	// Container principal
	JPanel container1 = new JPanel();

	// Deux gros panels : nord + sud, l'un au dessus de l'autre
	JPanel southPanel = new JPanel();
	JPanel northPanel = new JPanel();

	// Panels des capteurs : info contenant les boutons et la comboBox + la
	// tableau
	JPanel sensorsPanel = new JPanel();
	JPanel infoSensorsPanel = new JPanel();

	// Panels des localisations :
	JPanel locationPanel = new JPanel();
	JPanel locationList = new JPanel();
	JScrollPane locationScrollList = new JScrollPane();

	// Boutons pour les capteurs
	Button buttonDeleteSensor = new Button("Supprimer le capteur");
	Button buttonUpdateSensor = new Button("Modifier les infos du capteur");

	// Panel des patients : info contenant les boutons et la comboBox + la
	// tableau
	JPanel patientPanel = new JPanel();
	JPanel infoPatientPanel = new JPanel();

	// Boutons pour les patients
	Button buttonDeletePatient = new Button("Supprimer le patient");
	Button buttonUpdatePatient = new Button("Modifier les infos du patient");
	Button buttonRefreshSensor = new Button("Rafraichir");

	JMenuBar menuBar = new JMenuBar();

	JMenu account = new JMenu("Compte");
	JMenuItem accountModifyCode = new JMenuItem("Modifier mon code");
	JMenuItem accountModifyPassword = new JMenuItem("Modifier mon mot de passe");
	JMenuItem accountDisconnect = new JMenuItem("Deconnexion");

	JMenu adding = new JMenu("Ajouts");
	JMenuItem addingPatient = new JMenuItem("Ajouter un patient");
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur");
	JMenuItem addingLocation = new JMenuItem("Ajouter une localisation");

	JMenu deleting = new JMenu("Suppressions");

	JMenu modifying = new JMenu("Modifications");

	// Boutons pour les localisations
	Button buttonRefreshLocation = new Button("Rafraichir");

	JComboBox<Object> comboSensors;

	//Liste pour les sensors
	List<Sensor> sensorsFoundList;

	private JTable sensorsTable;

	SensorsTableModel sensorModel;

	public MainWindow(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public void init() throws SQLException, JsonParseException,
	JsonMappingException, IOException {
		setHost(host);
		setPort(port);

		// Mise en place de la JMenu Bar
		window.setJMenuBar(menuBar);
		menuBar.add(account);
		menuBar.add(adding);
		menuBar.add(deleting);
		menuBar.add(modifying);

		account.add(accountModifyCode);
		account.add(accountModifyPassword);
		account.add(accountDisconnect);

		adding.add(addingPatient);
		adding.add(addingSensor);
		adding.add(addingLocation);		

		// Les éléments s'ajoutent les uns en dessous des autres
		infoPatientPanel.setLayout(new BoxLayout(infoPatientPanel,
				BoxLayout.X_AXIS));
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel,
				BoxLayout.X_AXIS));

		patientPanel.add(infoPatientPanel);
		sensorsPanel.add(infoSensorsPanel);	

		//On récupère tous les capteurs qui ont été crées
		ConnectionClient ccSensorFindAll = new ConnectionClient(host, port, "SENSOR", "FINDALL", null);
		ccSensorFindAll.run();

		// Le retour est en String, à vrai dire c'est un tableau de String, un capteur = un élément du tableau renvoyé
		String retoursCcSensorFindAll = ccSensorFindAll.getResponse();

		// On le met dans un objet JSON pour qu'il soit bien formé
		JSONObject sensorsFound = new JSONObject();
		sensorsFound.put("sensorsFound", retoursCcSensorFindAll);

		// On récupère les varaibles de notre retour et on les "map" avec la classe Sensor
		// retour { "id" : 1, "idLocation" : 3, "type" : null, "state" :true, "alerts" : null, "brand" : "Thomson", "macAdress" : "1323:23:EZ:1223", "installation" : "2019-01-01", "caracteristics" : "ze", "breakdowns" : null}
		Sensor[] sensorsFoundTab =  objectMapper.readValue(
				sensorsFound.get("sensorsFound").toString(), Sensor[].class);

		// On insère cette liste dans un modèle de tableau créé spécialement
		// pour les capteurs
		sensorsFoundList = Arrays.asList(sensorsFoundTab);
		SensorsTableModel sensorModel = new SensorsTableModel(sensorsFoundList);

		// On ajoute le modèle à un JTable simple
		sensorsTable = new JTable(sensorModel) {
			// See WindowConfirm for serialVersionUID
			private static final long serialVersionUID = 5025433811409489149L;
		};

		// On interdit l'edition du tableau
		sensorsTable.setEnabled(false);

		JScrollPane paneSensors = new JScrollPane(sensorsTable);
		sensorsPanel.add(new JScrollPane(paneSensors));

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

		// Ajout de la combo Box puis des boutons
		infoSensorsPanel.add(comboSensors);
		infoSensorsPanel.add(buttonDeleteSensor);
		infoSensorsPanel.add(buttonUpdateSensor);
		infoSensorsPanel.add(buttonRefreshSensor);

		// Mise en place des raccourcis
		accountModifyCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		accountModifyPassword.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, InputEvent.CTRL_MASK));
		accountDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		addingPatient.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.ALT_DOWN_MASK));
		addingSensor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				InputEvent.ALT_DOWN_MASK));
		addingLocation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.ALT_DOWN_MASK));

		// Mise en place des Listeners pour les boutons
		buttonDeletePatient.addActionListener(this);
		buttonUpdatePatient.addActionListener(this);

		buttonDeleteSensor.addActionListener(this);
		buttonUpdateSensor.addActionListener(this);
		buttonRefreshSensor.addActionListener(this);

		accountDisconnect.addActionListener(this);
		accountModifyPassword.addActionListener(this);
		accountModifyCode.addActionListener(this);

		addingSensor.addActionListener(this);
		addingPatient.addActionListener(this);
		addingLocation.addActionListener(this);

		buttonRefreshLocation.addActionListener(this);

		// Bordures autour des différents panneaux
		locationPanel.setBorder(BorderFactory.createTitledBorder("Localisations"));
		patientPanel.setBorder(BorderFactory
				.createTitledBorder("Liste des patients"));
		sensorsPanel.setBorder(BorderFactory
				.createTitledBorder("Liste des capteurs"));

		// Les panneaux se mettent les une a côté des autres
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

		// Les panneaux se mettent les uns en dessous des autres : plan + listes
		locationList.setLayout(new BoxLayout(locationList, BoxLayout.Y_AXIS));
		container1.setLayout(new BoxLayout(container1, BoxLayout.Y_AXIS));

		// Récupère la taille de l'écran
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Met la fenêtre sur tout l'écran
		window.setBounds(0, 0, screenSize.width, screenSize.height);

		// Bloque la taille des panneaux
		locationPanel.setMaximumSize(new Dimension(screenSize.width - 900, screenSize.height - 400));
		sensorsPanel.setMaximumSize(new Dimension(900, screenSize.height - 400));
		northPanel.setMaximumSize(new Dimension(screenSize.width, screenSize.height - 400));
		patientPanel.setMaximumSize(new Dimension(screenSize.width, 400));	
		southPanel.setMaximumSize(new Dimension(screenSize.width, 400));

		// Fonctionne comme celui des Capteurs ligne 148.
		ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, "LOCATION", "FINDALL", null);
		ccLocationFindAll.run();

		String retourCcLocationFindAll = ccLocationFindAll.getResponse();
		JSONObject locationsFound = new JSONObject();	
		locationsFound.put("locationsFound", retourCcLocationFindAll);

		Location [] locationsFoundTab =  objectMapper.readValue(
				locationsFound.get("locationsFound").toString(), Location[].class);

		// On passe notre tableau en liste
		List <Location>locationsFoundList = Arrays.asList(locationsFoundTab);

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			locationList.add(new JLabel(locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName() + 
					" - Identifiants des capteurs à cet endroit : " + Arrays.toString(locationsFoundTab[listIndex].getSensorId())));
		}
		// Fin de la récupération des localisations

		// Ajout de tous les panneaux à leur bon endroit
		locationPanel.add(buttonRefreshLocation);
		locationPanel.add(locationList);

		northPanel.add(locationPanel);
		northPanel.add(sensorsPanel);
		southPanel.add(patientPanel);
		container1.add(northPanel);
		container1.add(southPanel);


		// Bordures vides pour l'espace entre les deux gros panneaux
		northPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		southPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		// Bordures vides pour l'espace dans le panneau des capteurs (du padding)
		locationList.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		// Bordures vides pour toute la fenêtre
		container1.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		// Les éléments se mettent les uns en dessous des autres
		patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));

		// Main container : container1
		window.setContentPane(container1);
		window.setTitle("Floralis");
		window.setLocationRelativeTo(null);

		// DISPOSE --> ne ferme pas, laisse la place à la fenêtre de
		// déconnection
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);

		// Si la fenêtre est fermée sans déconnection alors
		// on lance un sychronized pour notifié le main que nous avons fini
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
		if (e.getSource() == addingPatient) {
			System.out.println("Add personnel");
		}

		if (e.getSource() == addingSensor) {
			System.out.println("Add sensor");
			try {
				new WindowAdd(getHost(), getPort()).initAddSensor();
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == addingLocation) {
			System.out.println("Add Location");
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
			ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, "LOCATION", "FINDALL", null);
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
					locationList.add(new JLabel(locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName() + 
							" - Identifiants des capteurs à cet endroit : " + Arrays.toString(locationsFoundTab[listIndex].getSensorId())));
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
			// Voir ligne 148
			ConnectionClient ccSensorFindAllRefresh = new ConnectionClient(host, port, "SENSOR", "FINDALL", null);
			ccSensorFindAllRefresh.run();

			String retoursCcSensorFindAllRefresh = ccSensorFindAllRefresh.getResponse();
			JSONObject sensorsFoundRefresh = new JSONObject();
			sensorsFoundRefresh.put("sensorsFoundRefresh", retoursCcSensorFindAllRefresh);

			Sensor[] sensorsFoundRefreshTab = null;
			try {
				sensorsFoundRefreshTab = objectMapper.readValue(
						sensorsFoundRefresh.get("sensorsFoundRefresh").toString(), Sensor[].class);
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}

			sensorsFoundList = Arrays.asList(sensorsFoundRefreshTab);
			SensorsTableModel sensorModelRefresh = new SensorsTableModel(sensorsFoundList);

			String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1]; 
			sensorsComboBox[0]= "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() + " ";
			}
			// Fin du connectionClient
			

			// On enlève tous les éléments de la comboBox
			comboSensors.removeAllItems();

			//On la rempli des éléments trouvés avec le nouveau connectionClient
			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors.addItem(sensorsComboBox[i]);
			}

			// On déclare le nouveau model comme étant le model de notre sensorTable
			sensorsTable.setModel(sensorModelRefresh);

		}

		if (e.getSource() == accountModifyCode) {
			System.out.println("Modifiy code");
		}

		if (e.getSource() == accountDisconnect) {
			// On lance un sychronized pour notifié le main que nous avons fini
			synchronized (valueWait) {
				window.setVisible(false);
				valueWait.notify();
			}
		}

		if (e.getSource() == buttonDeletePatient) {

		}

		if (e.getSource() == buttonUpdatePatient) {

		}

		if (e.getSource() == buttonDeleteSensor) {
			// Récupère l'index de la ComboBox
			int indexSensor = comboSensors.getSelectedIndex();

			// Si il est à 0, c'est qu'aucun vrai ID n'a été selectionné car
			// index [0] = --id du capteur--
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
					ConnectionClient ccSensorDelete = new ConnectionClient(host, port, "SENSOR", "DELETE", objSensorDelete.toString());
					ccSensorDelete.run();

					// TODO : modifier les noms 
					// Ici, il faut récupérer la localisation qui est associée au capteur pour 
					// supprimer dans cette localisation l'occurence du capteur supprimé
					ConnectionClient ccLocationFindById = new ConnectionClient(host, port, "LOCATION", "FINDBYID", sensorFoundDelete.toString());
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
						int sensorsCount = locationFound.getSensorId().length;
						// On créer un nouveau tableau
						int[] newListSensorLocation = new int[sensorsCount];

						// On ajoute dans ce nouveau tableau, tous les capteurs sauf celui qu'on vient de supprimer
						// FIXME : j'arrive pas à totalement le supprimer, alors pour l'instant, il devient juste 0
						for (int i = 0; i < sensorsCount; i++) {
							newListSensorLocation[i] = locationFound.getSensorId()[i];
							if(newListSensorLocation[i] == idSensorDelete) {
								newListSensorLocation[i] = 0;
							}
						}

						// On modifie en mettant notre nouveau tableau puis on fait l'update sur la table des localisations
						locationFound.setSensorId(newListSensorLocation);
						JSONObject parametersLocation = new JSONObject(locationFound);	

						ConnectionClient ccLocationUpdate = new ConnectionClient(host, port, "LOCATION", "UPDATE", parametersLocation.toString());
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
					new WindowUpdate().initUpdateSensor(idSensor);
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
