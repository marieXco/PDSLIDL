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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.json.JSONObject;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.dao.SensorDao;
import fr.pds.floralis.gui.tablemodel.SensorsTableModel;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowWorker extends Thread implements ActionListener, Runnable {
	private JDBCConnectionPool jdb;
	private Connection connect;
	
	//TODO : refresh de l'index de la comboBox

	//Object pour lancer le top de la fin de la JFrame --> voir synchronized dans le cours
	public final Object valueWait = new Object();
	static JFrame window = new JFrame();

	//Container principal
	JPanel container1 = new JPanel();

	// Deux gros panels : plan + pannel de "listes", l'un au dessus de l'autre
	JPanel locationPanel = new JPanel();
	JPanel listsPanel = new JPanel();


	//Panel des capteurs : info contenant les boutons et la comboBox + la tableau
	JPanel sensorsPanel = new JPanel();
	JPanel infoSensorsPanel = new JPanel();

	//Boutons pour les capteurs
	Button buttonDeleteSensor = new Button("Supprimer le capteur");
	Button buttonUpdateSensor = new Button("Modifier les infos du capteur");


	//Panel des patients : info contenant les boutons et la comboBox + la tableau
	JPanel patientPanel = new JPanel();
	JPanel infoPatientPanel = new JPanel();

	//Boutons pour les patients
	Button buttonDeletePatient = new Button("Supprimer le patient");
	Button buttonUpdatePatient = new Button("Modifier les infos du patient");


	JMenuBar menuBar = new JMenuBar(); 

	JMenu account = new JMenu("Compte");
	JMenuItem accountModifyCode = new JMenuItem("Modifier mon code"); 
	JMenuItem accountModifyPassword = new JMenuItem("Modifier mon mot de passe"); 
	JMenuItem accountDisconnect = new JMenuItem("Deconnexion"); 

	JMenu adding = new JMenu("Ajouts");
	JMenuItem addingPatient = new JMenuItem("Ajouter un patient"); 
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur"); 


	JComboBox comboPatient;
	JComboBox<Object> comboSensors;

	//Listes pour les patients et les capteurs
	List<Patients> patientsList;
	List<Sensor> sensorsList;
	JTable tableSensors;
	private SensorsTableModel sensorModel;
	Button buttonRefreshSensor = new Button("R");

	public WindowWorker(JDBCConnectionPool jdb, Connection connect)  throws ClassNotFoundException, SQLException, IOException {
		this.jdb = jdb;
		this.connect = connect;	
	}

	public void init() throws SQLException {

		// Mise en place de la JMenu Bar
		window.setJMenuBar(menuBar);
		menuBar.add(account);
		menuBar.add(adding);

		account.add(accountModifyCode);
		account.add(accountModifyPassword);
		account.add(accountDisconnect);

		adding.add(addingPatient);
		adding.add(addingSensor);

		// Les éléments s'ajoutent les uns en dessous des autres
		infoPatientPanel.setLayout(new BoxLayout(infoPatientPanel, BoxLayout.X_AXIS));
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));

		patientPanel.add(infoPatientPanel);
		sensorsPanel.add(infoSensorsPanel);


		//		patientsList = Selects.SelectPatients(jdb, connect);
		//		PatientsTableModel patientModel = new PatientsTableModel(patientsList);
		//		JTable tablePatients = new JTable(patientModel) {};
		//
		//		tablePatients.setEnabled(false);
		//		JScrollPane panePatients = new JScrollPane(tablePatients);
		//		patientPanel.add(new JScrollPane(panePatients));
		//
		//		String[] selectPatient = new String[patientsList.size() + 1]; 
		//		selectPatient[0] = "--Identifiant du patient--";
		//
		//		for (int listIndex = 0; listIndex < patientsList.size(); listIndex++) {
		//			int tabIndex = listIndex + 1;
		//			selectPatient[tabIndex] = patientsList.get(listIndex).getFirstname() + " " + patientsList.get(listIndex).getLastname();
		//		}	
		//		
		//		comboPatient = new JComboBox<Object>(selectPatient);		
		//		infoPatientPanel.add(comboPatient);
		//		
		//		infoPatientPanel.add(buttonDeletePatient);
		//		infoPatientPanel.add(buttonUpdatePatient);		


		//On appelle le DAO des capteurs 
		SensorDao sensorDao = new SensorDao(connect);
		//findAll renvoie une liste de tous les capteurs de la base
		sensorsList = sensorDao.findAll();
		//On insère cette liste dans un modèle de tableau créé spécialement pour les capteurs
		sensorModel = new SensorsTableModel(sensorsList);
		//On ajoute le modèle à un JTable simple
		tableSensors = new JTable(sensorModel) {};

		//On interdit l'edition du tableau
		tableSensors.setEnabled(false);
		//On ajoute à un panneau qui permet de scroller notre JTable qui sera ajouter à notre panneau de capteurs
		JScrollPane paneSensors = new JScrollPane(tableSensors);
		sensorsPanel.add(new JScrollPane(paneSensors));


		//ComboBox d'identifiants pour les capteurs

		//On créé un tableau de ce que contient le tableau de capteur
		//getRowCount + 1 car getRowCount renvoie la taille de ce qui a dans le tableau (exemple 3,
		//éuivalent à sensorsList.size()), sachant que l'indice du tableau 0 est pris 
		//(--identifiant du capteur--), il faut que notre tableau
		//ait une case en plus que le nombre d'éléments dans le tableau du capteur 
		//(index [0] + 1 index pour chaque élément --> tableau de taille 4 donc getRowCount() + 1)

		String[] selectSensor = new String[sensorModel.getRowCount() + 1]; 
		selectSensor[0] = "--Identifiant du capteur--";

		for (int listIndex = 0; listIndex < sensorDao.findAll().size(); listIndex++) {
			int tabIndex = listIndex + 1;
			selectSensor[tabIndex] = sensorDao.findAll().get(listIndex).getId() + " ";
		}

		//Notre combo bo
		comboSensors = new JComboBox<Object>(selectSensor);


		//Ajout de la combo Box puis des boutons
		infoSensorsPanel.add(comboSensors);
		infoSensorsPanel.add(buttonDeleteSensor);
		infoSensorsPanel.add(buttonUpdateSensor);
		infoSensorsPanel.add(buttonRefreshSensor);


		//Mise en place des raccourcis
		accountModifyCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		accountModifyPassword.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		accountDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		addingPatient.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
		addingSensor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK));

		//Mise en place des Listeners pour les boutons
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

		//Bordures autour des différents panneaux
		locationPanel.setBorder(BorderFactory.createTitledBorder("Plan"));
		patientPanel.setBorder(BorderFactory.createTitledBorder("Liste des patients"));
		sensorsPanel.setBorder(BorderFactory.createTitledBorder("Liste des capteurs"));

		//Les panneaux se mettent les une a côté des autres
		listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.X_AXIS));

		//Les panneaux se mettent les uns en dessous des autres : plan + listes
		container1.setLayout(new BoxLayout(container1, BoxLayout.Y_AXIS));

		//Récupère la taille de l'écran
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//Met la fenêtre sur tout l'écran
		window.setBounds(0,0,screenSize.width, screenSize.height);

		//Bloque la taille des panneaux
		patientPanel.setMaximumSize(new Dimension(screenSize.width/2,300));
		sensorsPanel.setMaximumSize(new Dimension(screenSize.width/2,300));
		listsPanel.setPreferredSize(new Dimension(screenSize.width,300));

		listsPanel.add(patientPanel);
		listsPanel.add(sensorsPanel);
		container1.add(locationPanel);
		container1.add(listsPanel);

		//Bordures vides pour l'espace entre les deux gros panneaux
		listsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		//Bordures vides pour toute la fenêtre
		container1.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

		//Les éléments se mettent les uns en dessous des autres
		patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));

		// Main container : container1
		window.setContentPane(container1);
		window.setTitle("Floralis");
		window.setLocationRelativeTo(null);

		//DISPOSE --> ne ferme pas, laisse la place à la fenêtre de déconnection
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);

		//Si la fenêtre est fermée sans déconnection alors 
		//on lance un sychronized pour notifié le main que nous avons fini
		window.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
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
			new WindowAdd(jdb, connect).initAddSensor();
		}

		if (e.getSource() == buttonRefreshSensor) {
			System.out.println("Refresh sensor");
			SensorDao sensorDao = new SensorDao(connect);
			//findAll renvoie une liste de tous les capteurs de la base
			sensorsList = sensorDao.findAll();
			//On insère cette liste dans un modèle de tableau créé spécialement pour les capteurs
			SensorsTableModel sensorModels = new SensorsTableModel(sensorsList);
			//On ajoute le modèle à un JTable simple
			tableSensors.setModel(sensorModels);	

			String[] selectSensors = new String[sensorModels.getRowCount() + 1]; 
			selectSensors[0]= "--Identifiant du capteur--";

			for (int listIndex = 0; listIndex < sensorsList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				selectSensors[tabIndex] = sensorsList.get(listIndex).getId() + " ";
			}

			comboSensors.removeAllItems();
			for (int i = 0; i < selectSensors.length; i++) {
				comboSensors.addItem(selectSensors[i]);
			}
			
		}


		if (e.getSource() == accountModifyCode) {
			System.out.println("Modifiy code");
		}

		if (e.getSource() == accountModifyPassword) {
			System.out.println("Modifiy password");

		}


		if (e.getSource() == accountDisconnect) { 
			//On lance un sychronized pour notifié le main que nous avons fini
			synchronized (valueWait) {
				window.setVisible(false);
				valueWait.notify();
			}
		}

		if(e.getSource() == buttonDeletePatient) {
			//			int indexPatient = comboPatient.getSelectedIndex();
			//			if (indexPatient > 0) {
			//				int idPatient = patientsList.get(indexPatient - 1).getId();
			//				try {
			//					new WindowConfirm(jdb, connect).initDeletePatient(idPatient);
			//				} catch (SQLException e1) {
			//					e1.printStackTrace();
			//				}
			//			}
		}

		if(e.getSource() == buttonUpdatePatient) {
			//			int indexPatient = comboPatient.getSelectedIndex();
			//			if (indexPatient > 0) {
			//				int idPatient = patientsList.get(indexPatient - 1).getId();
			//				try {
			//					new WindowUpdate(jdb, connect).initUpdatePatient(idPatient);
			//				} catch (SQLException e1) {
			//					e1.printStackTrace();
			//				}
			//			}
		}

		if(e.getSource() == buttonDeleteSensor) {
			//Récupère l'index de la ComboBox
			int indexSensor = comboSensors.getSelectedIndex();

			//Si il est à 0, c'est qu'aucun vrai ID n'a été selectionné car index [0] = --id du capteur--
			if (indexSensor > 0) {
				//on récupère l'id du capteur contenu à l'index de la checkbox - 1
				// Index checkbox : 3 est équivalant à l'index 2 du tableau des capteurs 
				int idSensor = sensorsList.get(indexSensor - 1).getId();

				//On créer un object de JSON
				JSONObject obj = new JSONObject();

				//On ajout dans cet object une clé "id" dont la valeur est idSensor
				// { "id" : idSensor valeur } ;
				obj.put("id", idSensor);

				//On lance une fênetre de confirmation qui renvoie 'true' si on clique sur oui
				//'false' pour le reste
				boolean sure = new WindowConfirm(jdb, connect).init("supprimer ce capteur");

				//Si sure est à true alors on lance la supression en insérant l'object JSON contenant l'id
				if (sure) {
					SensorDao sensorDao = new SensorDao(connect);
					sensorDao.delete(obj);
				}
			}

		}

		if(e.getSource() == buttonUpdateSensor) {
			//Récupère l'index de la ComboBox
			int indexSensor = comboSensors.getSelectedIndex();

			//Si il est à 0, c'est qu'aucun vrai ID n'a été selectionné car index [0] = --id du capteur--
			if (indexSensor > 0) {
				//on récupère l'id du capteur contenu à l'index de la checkbox - 1
				// Index checkbox : 3 est équivalant à l'index 2 du tableau des capteurs 
				System.out.println(sensorsList.get(indexSensor - 1).getId());
				int idSensor = sensorsList.get(indexSensor - 1).getId();

				//On lance la fenêtre de modification avec l'id correspondant (pas de JSON pour l'instant
				// c'est entre deux IHM
				new WindowUpdate(jdb, connect).initUpdateSensor(idSensor);
			}

		}

	}

	// Méthode appelée par le frame.start du main
	public void run() {
		try {
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
