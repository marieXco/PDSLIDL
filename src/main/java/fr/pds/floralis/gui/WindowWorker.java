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

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.dao.SensorDao;
import fr.pds.floralis.gui.tablemodel.PatientsTableModel;
import fr.pds.floralis.gui.tablemodel.SensorsTableModel;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowWorker extends Thread implements ActionListener, Runnable {
	private JDBCConnectionPool jdb;
	private Connection connect;
	
	public final Object valueWait = new Object();
	static JFrame window = new JFrame();

	JPanel locationPanel = new JPanel();
	JPanel patientPanel = new JPanel();
	JPanel sensorsPanel = new JPanel();
	JPanel listsPanel = new JPanel();
	JPanel infoSensorsPanel = new JPanel();
	JPanel infoPatientPanel = new JPanel();

	JMenuBar menuBar = new JMenuBar(); 

	JMenu account = new JMenu("Compte");
	JMenu adding = new JMenu("Ajouts");

	JMenuItem accountModifyCode = new JMenuItem("Modifier mon code"); 
	JMenuItem accountModifyPassword = new JMenuItem("Modifier mon mot de passe"); 
	JMenuItem accountDisconnect = new JMenuItem("Deconnexion"); 

	JMenuItem addingPatient = new JMenuItem("Ajouter un patient"); 
	JMenuItem addingSensor = new JMenuItem("Ajouter un capteur"); 

	Button buttonDeletePatient = new Button("Supprimer le patient");
	Button buttonUpdatePatient = new Button("Modifier les infos du patient");

	Button buttonDeleteSensor = new Button("Supprimer le capteur");
	Button buttonUpdateSensor = new Button("Modifier les infos du capteur");

	JPanel container1 = new JPanel();
	JComboBox comboPatient;
	JComboBox comboSensors;

	List<Patients> patientsList;
	List<Sensor> sensorsList;

	Thread t = new Thread();


	public void init() throws SQLException {
		infoPatientPanel.setLayout(new BoxLayout(infoPatientPanel, BoxLayout.X_AXIS));
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));

		patientPanel.add(infoPatientPanel);
		sensorsPanel.add(infoSensorsPanel);
		

		patientsList = Selects.SelectPatients(jdb, connect);
		PatientsTableModel patientModel = new PatientsTableModel(patientsList);
		JTable tablePatients = new JTable(patientModel) {};

		tablePatients.setEnabled(false);
		JScrollPane panePatients = new JScrollPane(tablePatients);
		patientPanel.add(new JScrollPane(panePatients));

		String[] selectPatient = new String[patientsList.size() + 1]; 
		selectPatient[0] = "--Identifiant du patient--";

		for (int listIndex = 0; listIndex < patientsList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			selectPatient[tabIndex] = patientsList.get(listIndex).getFirstname() + " " + patientsList.get(listIndex).getLastname();
		}	
		
		comboPatient = new JComboBox<Object>(selectPatient);		
		infoPatientPanel.add(comboPatient);
		
		infoPatientPanel.add(buttonDeletePatient);
		infoPatientPanel.add(buttonUpdatePatient);		
		
		
		SensorDao sensorDao = new SensorDao(connect);
		sensorsList = sensorDao.findAll();
		SensorsTableModel sensorModel = new SensorsTableModel(sensorsList); 
		JTable tableSensors = new JTable(sensorModel) {};


		tableSensors.setEnabled(false);
		JScrollPane paneSensors = new JScrollPane(tableSensors);
		sensorsPanel.add(new JScrollPane(paneSensors));


		String[] selectSensor = new String[sensorsList.size() + 1]; 
		selectSensor[0] = "--Identifiant du capteur--";

		for (int listIndex = 0; listIndex < sensorsList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			selectSensor[tabIndex] = sensorsList.get(listIndex).getId() + " ";
		}

		comboSensors = new JComboBox<Object>(selectSensor);
		infoSensorsPanel.add(comboSensors);

		infoSensorsPanel.add(buttonDeleteSensor);
		infoSensorsPanel.add(buttonUpdateSensor);

		window.setJMenuBar(menuBar);
		menuBar.add(account);
		menuBar.add(adding);

		account.add(accountModifyCode);
		account.add(accountModifyPassword);
		account.add(accountDisconnect);

		adding.add(addingPatient);
		adding.add(addingSensor);

		accountModifyCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		accountModifyPassword.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		accountDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		addingPatient.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
		addingSensor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK));

		buttonDeletePatient.addActionListener(this);
		buttonUpdatePatient.addActionListener(this);

		buttonDeleteSensor.addActionListener(this);
		buttonUpdateSensor.addActionListener(this);

		accountDisconnect.addActionListener(this);
		accountModifyPassword.addActionListener(this);
		accountModifyCode.addActionListener(this);

		addingSensor.addActionListener(this);
		addingPatient.addActionListener(this);

		locationPanel.setBorder(BorderFactory.createTitledBorder("Plan"));
		patientPanel.setBorder(BorderFactory.createTitledBorder("Liste des patients"));
		sensorsPanel.setBorder(BorderFactory.createTitledBorder("Liste des capteurs"));

		listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.X_AXIS));
		container1.setLayout(new BoxLayout(container1, BoxLayout.Y_AXIS));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setBounds(0,0,screenSize.width, screenSize.height);
		patientPanel.setMaximumSize(new Dimension(screenSize.width/2,300));
		sensorsPanel.setMaximumSize(new Dimension(screenSize.width/2,300));
		listsPanel.setPreferredSize(new Dimension(screenSize.width,300));

		listsPanel.add(patientPanel);
		listsPanel.add(sensorsPanel);
		container1.add(locationPanel);
		container1.add(listsPanel);

		listsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		container1.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
		sensorsPanel.setLayout(new BoxLayout(sensorsPanel, BoxLayout.Y_AXIS));

		window.setContentPane(container1);
		window.setTitle("Floralis");
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);

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

	public WindowWorker(JDBCConnectionPool jdb, Connection connect)  throws ClassNotFoundException, SQLException, IOException {
		this.jdb = jdb;
		this.connect = connect;	
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addingPatient) {
			System.out.println("Add personnel");
			//new WindowAdd(jdb, connect).initAddPatient();
		}

		if (e.getSource() == addingSensor) {
			System.out.println("Add sensor");
			new WindowAdd(jdb, connect).initAddSensor();
		}


		if (e.getSource() == accountModifyCode) {
			System.out.println("Modifiy code");
		}

		if (e.getSource() == accountModifyPassword) {
			System.out.println("Modifiy password");
		}


		if (e.getSource() == accountDisconnect) { 
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
			int indexSensor = comboSensors.getSelectedIndex();
			if (indexSensor > 0) {
				int idSensor = sensorsList.get(indexSensor - 1).getId();

				JSONObject obj = new JSONObject();
				obj.put("id", idSensor);
				
				PGobject jsonObject = new PGobject();
				jsonObject.setType("json");

				try {
					jsonObject.setValue(obj.toString());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				if (new WindowConfirm(jdb, connect).init("supprimer ce capteur")) {
					SensorDao sensorDao = new SensorDao(connect);
					sensorDao.delete(obj);
				}
			}
		}

		if(e.getSource() == buttonUpdateSensor) {
			int indexSensor = comboSensors.getSelectedIndex();
			if (indexSensor > 0) {
				int idSensor = sensorsList.get(indexSensor - 1).getId();
				new WindowUpdate(jdb, connect).initUpdateSensor(idSensor);
			}
		}
	}

	public void run() {
		try {
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
