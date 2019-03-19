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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensors;
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
	JMenu patients = new JMenu("Patients");

	JMenuItem accountFirstItem = new JMenuItem("Modifier mon code"); 
	JMenuItem accountSecondItem = new JMenuItem("Modifier mon mot de passe"); 
	JMenuItem accountThirdItem = new JMenuItem("Deconnexion"); 

	JMenuItem patientsFirstItem = new JMenuItem("Ajouter un patient"); 
	JMenuItem patientsSecondItem = new JMenuItem("Ajouter un capteur"); 
	JMenuItem paitentsThirdItem = new JMenuItem("Supprimer un patient"); 
	
	Button buttonDeletePatient = new Button("Supprimer le patient");
	Button buttonUpdatePatient = new Button("Modifier les infos du patient");

	JPanel container1 = new JPanel();
	
	JComboBox comboPatient = null;
	
	List<Patients> patientsList;
	List<Sensors> sensorsList;

	//Autre combo box pour autre delete
	//String[] selectPersonnel = {"--------", "Lundi", "Mardi"} ; 
	JComboBox comboSensors = null;

	Thread t = new Thread();
	
	public void init() throws SQLException {
		
		// ComboBox pour autre delete
		//infoSensorsPanel.add(combo1);

		infoPatientPanel.setLayout(new BoxLayout(infoPatientPanel, BoxLayout.X_AXIS));
		infoSensorsPanel.setLayout(new BoxLayout(infoSensorsPanel, BoxLayout.X_AXIS));

		patientPanel.add(infoPatientPanel);
		sensorsPanel.add(infoSensorsPanel);
		
		//TODO Ajouter 2 boutons delete et update
		
		patientsList = Selects.SelectPatients(jdb, connect);
        PatientsTableModel patientModel = new PatientsTableModel(patientsList);
        JTable tablePatients = new JTable(patientModel) {};
       
		tablePatients.setEnabled(false);
		JScrollPane panePatients = new JScrollPane(tablePatients);
		patientPanel.add(new JScrollPane(panePatients));
		
		String[] selectPatient = new String[patientsList.size() + 1]; 
		selectPatient[0] = "----------";
		
		for (int listIndex = 0; listIndex < patientsList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			selectPatient[tabIndex] = patientsList.get(listIndex).getFirstname() + " " + patientsList.get(listIndex).getLastname();
		}	
		comboPatient = new JComboBox(selectPatient);		
		infoPatientPanel.add(comboPatient);
		
		
		
		sensorsList = Selects.SelectSensors(jdb, connect);
		SensorsTableModel sensorModel = new SensorsTableModel(sensorsList); 
		JTable tableSensors = new JTable(sensorModel) {};
		
		tableSensors.setEnabled(false);
		JScrollPane paneSensors = new JScrollPane(tableSensors);
		sensorsPanel.add(new JScrollPane(paneSensors));
		
	
		String[] selectSensor = new String[sensorsList.size() + 1]; 
		selectSensor[0] = "----------";
		
		for (int listIndex = 0; listIndex < sensorsList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			selectSensor[tabIndex] = sensorsList.get(listIndex).getId() + " ";
		}
		
		comboSensors = new JComboBox(selectSensor);
		
		infoSensorsPanel.add(comboSensors);
		
		infoPatientPanel.add(buttonDeletePatient);
		infoPatientPanel.add(buttonUpdatePatient);

		window.setJMenuBar(menuBar);
		menuBar.add(account);
		menuBar.add(patients);

		account.add(accountFirstItem);
		account.add(accountSecondItem);
		account.add(accountThirdItem);

		patients.add(patientsFirstItem);
		patients.add(patientsSecondItem);
		patients.add(paitentsThirdItem);

		accountFirstItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		accountSecondItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		accountThirdItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));

		patientsFirstItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));
		patientsSecondItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK));
		paitentsThirdItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK));

		buttonDeletePatient.addActionListener(this);
		buttonUpdatePatient.addActionListener(this);
		
		accountThirdItem.addActionListener(this);
		accountSecondItem.addActionListener(this);
		accountFirstItem.addActionListener(this);

		paitentsThirdItem.addActionListener(this);
		patientsSecondItem.addActionListener(this);
		patientsFirstItem.addActionListener(this);

		locationPanel.setBorder(BorderFactory.createTitledBorder("Plan"));
		patientPanel.setBorder(BorderFactory.createTitledBorder("Liste des patients"));
		sensorsPanel.setBorder(BorderFactory.createTitledBorder("Liste des capteurs"));

		listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.X_AXIS));
		container1.setLayout(new BoxLayout(container1, BoxLayout.Y_AXIS));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setBounds(0,0,screenSize.width, screenSize.height);
		patientPanel.setMaximumSize(new Dimension(screenSize.width - 600,300));
		sensorsPanel.setMaximumSize(new Dimension(600,300));
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
		if (e.getSource() == patientsFirstItem) {
			System.out.println("Add personnel");
			new WindowAdd(jdb, connect).initAddPatient();
		}
		if (e.getSource() == patientsSecondItem) {
			System.out.println("Add sensor");
			new WindowAdd(jdb, connect).initAddSensor();
		}

		if (e.getSource() == paitentsThirdItem) {
			System.out.println("Delete patient");
		}

		if (e.getSource() == accountFirstItem) {
			System.out.println("Modifiy code");
		}
		if (e.getSource() == accountSecondItem) {
			System.out.println("Modifiy password");
		}

		if (e.getSource() == accountThirdItem) { 
			synchronized (valueWait) {
				System.out.println ("Kill");
				window.setVisible(false);
				valueWait.notify();
			}
			System.out.println("Quit");
		}
		if(e.getSource() == buttonDeletePatient) {
			int index = comboPatient.getSelectedIndex();
			if (index > 0) {
				int id = patientsList.get(index - 1).getId();
				try {
					new WindowConfirm(jdb, connect).initDeletePatient(id);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if(e.getSource() == buttonUpdatePatient) {
			int index = comboPatient.getSelectedIndex();
			if (index > 0) {
				int id = patientsList.get(index - 1).getId();
				try {
					new WindowUpdate(jdb, connect).initUpdatePatient(id);
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
	}
	
	public void run() {
		try {
			init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
