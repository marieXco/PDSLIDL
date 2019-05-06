
	package fr.pds.floralis.gui;


	import java.awt.BorderLayout;
	import java.awt.Button;
	import java.awt.HeadlessException;
	import java.awt.event.*;
	import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
	import javax.swing.BoxLayout;
	import javax.swing.JComboBox;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JPanel;
	import javax.swing.JPasswordField;
	import javax.swing.JTextField;
	import javax.swing.JTextPane;
	import javax.swing.text.SimpleAttributeSet;
	import javax.swing.text.StyleConstants;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Patient;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.server.dao.DAO;

	//TODO: ajouter un onglet pour accéder à ses stats visibles seulement par les administrateurs sur WindowAdd
	//TODO: faire les scripts
	
	// this is the window where 
	public class WindowStats extends JFrame implements ActionListener {
		private String host;
		private int port;
		
		public WindowStats(String host, int port) throws HeadlessException {
			super();
			this.host = host;
			this.port = port;
		}
		
		
		// Creation of panels
		JPanel container = new JPanel();
		JPanel requestPanel = new JPanel();
		JPanel resultPanel = new JPanel(); 
		

		// Creation of all parameters necessary 
		JComboBox indicators = new JComboBox();
		JLabel label = new JLabel("Indicateurs :");
		
		
		Button countRoom = new Button("Nombre de pièce");
		Button countAllSensor = new Button("Nombre de capteurs");
		Button countOffSensor = new Button("Nombre de capteurs éteint");
		Button countBreakdownSensor = new Button("Nombre de capteurs en panne");
		Button countOnSensor = new Button("Nombre de capteurs allumés");
		Button countNoConfigSensor = new Button("Nombres de capteurs non configurés");
		Button countAlert = new Button("Nombre d'alerte");
		 //countSmokeSensor = new Button("Nombre de capteurs de fumée");
		Button countLightSensor = new Button("Nombre de capteurs de lumière");
		Button countGasSensor = new Button("Nombre de capteurs de gaz");
		Button countMoveSensor = new Button("Nombre de capteurs de mouvement");
		Button countTempSensor = new Button("Nombre de capteurs température ");
		Button countAllPatient = new Button("Nombre de patients"); 
		Button countAlertByMonth = new Button("Nombre d'alerte par mois");
		Button countAlertByYear = new Button("Nombre d'alerte par an");
		
		
		// Indicators values
		List<Location> roomFoundList; 
		List<Sensor> sensorFoundList;
		List<Alert> alertFoundList;
		List<Patient> patientFoundList;
		
		
		

		public void initIndicators() throws IOException{
			
			
			//adding button to JPanel
			requestPanel.add(countAllSensor);
			requestPanel.add(countRoom);
			requestPanel.add(countOffSensor);
			requestPanel.add(countNoConfigSensor);
			/*
			requestPanel.add(countOnSensor);
			requestPanel.add(countAlert);
			requestPanel.add(countSmokeSensor);
			requestPanel.add(countLightSensor);
			requestPanel.add(countGasSensor);
			requestPanel.add(countMoveSensor);
			requestPanel.add(countTempSensor);
			requestPanel.add(countAllPatient); */
			requestPanel.add(countAlertByMonth);
			requestPanel.add( countBreakdownSensor);
			requestPanel.add(countAlertByYear)
			//requestPanel.add(countPresenceSensor);
			
			indicators.addItem("Nombre d'alerte");
			requestPanel.add(indicators);
			indicators.addActionListener(this);
			

			container.add(BorderLayout.WEST, requestPanel); 
			container.add(BorderLayout.EAST, resultPanel);
			

			countRoom.addActionListener(this);
			countAllSensor.addActionListener(this);
			countOffSensor.addActionListener(this);
			countOnSensor.addActionListener(this);
			countNoConfigSensor.addActionListener(this);
			countAlert.addActionListener(this);
			//countSmokeSensor.addActionListener(this);
			countLightSensor.addActionListener(this);
			countGasSensor.addActionListener(this);
			countMoveSensor.addActionListener(this);
			countTempSensor.addActionListener(this);
			countAllPatient.addActionListener(this);
			countAlertByMonth.addActionListener(this);
			countBreakdownSensor.addActionListener(this);
			countAlertByYear.addActionListener(this);
			
			
			
			/*countSmokeSensor.setVisible(false);
			countLightSensor.setVisible(false);
			countGasSensor.setVisible(false);
			countMoveSensor.setVisible(false);
			countTempSensor.setVisible(false);
			countOffSensor.setVisible(false);
			countOnSensor.setVisible(false);
			countNoLocationSensor.setVisible(false);*/
			
		
			this.setTitle("Floralis - Indicateurs");
			this.setContentPane(container);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setVisible(true);
			this.setSize(900, 800);
			
		}
		
		public void actionPerformed(ActionEvent e) {
			//when the user wants to know how many home there's on EHPAD
			if (e.getSource() == countRoom) {
				FindAllLocation allloc = new FindAllLocation(host, port);
				try {
					roomFoundList = allloc.findAll(false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countRoom = roomFoundList.size(); 	
				System.out.println("Nombre de pièce : " + countRoom);
			}
			
			if(e.getSource() == countAllSensor) {
				FindAllSensor allsens = new FindAllSensor(host, port);
				try {
					sensorFoundList = allsens.findAll(false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countSensor = sensorFoundList.size(); 	
				System.out.println("Nombre de capteurs : " + countSensor);
				/*countSmokeSensor.setVisible(true);
				countLightSensor.setVisible(true);
				countGasSensor.setVisible(true);
				countMoveSensor.setVisible(true);
				countTempSensor.setVisible(true);
				countOffSensor.setVisible(true);
				countOnSensor.setVisible(true);
				countNoLocationSensor.setVisible(true);*/
			}
			
			if(e.getSource() == countAlert) {
				FindAllAlert allAl = new FindAllAlert(host, port);
				try {
					alertFoundList = allAl.findAll(false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countAlert = alertFoundList.size(); 	
				System.out.println("Nombre d'Alerte : " + countAlert);

			}
			if(e.getSource() == countOffSensor) {
				FindSensorByState allloc = new FindSensorByState(host, port);
				try {
					sensorFoundList = allloc.findByState(false, false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countOff = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs éteint : " + countOff);	
			}
			
			if(e.getSource() == countOnSensor) {
				FindSensorByState allloc = new FindSensorByState(host, port);
				try {
					sensorFoundList = allloc.findByState(false, true);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countOn = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs allumés : " + countOn);
				
			}
			
			if(e.getSource() == countNoConfigSensor) {
				FindSensorByConfig allloc = new FindSensorByConfig(host, port);
				try {
					sensorFoundList = allloc.findByConfig(false, false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countNoConfig = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs non configurés : " + countNoConfig);	
			}
			
			/*if(e.getSource() == countSmokeSensor) {
				String smoke = 	"FIRE"; 
				FindSensorByType allloc = new FindSensorByType(host, port);
				try {
					sensorFoundList = allloc.findByType(false, smoke);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countSmoke = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs de fumée : " + countSmoke);
			}*/
	
			
			if(e.getSource() == countLightSensor) {
				String light = 	"LIGHT"; 
				FindSensorByType allloc = new FindSensorByType(host, port);
				try {
					sensorFoundList = allloc.findByType(false, light);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countLight = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs de lumière : " + countLight);
				
			}
			
			if(e.getSource() == countGasSensor) {
				String light = 	"GASLEAK"; 
				FindSensorByType allloc = new FindSensorByType(host, port);
				try {
					sensorFoundList = allloc.findByType(false, light);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countGas = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs de lumière : " + countGas);
							
			}
			
			if(e.getSource() == countMoveSensor) {
				String light = 	"PRESENCE"; 
				FindSensorByType allloc = new FindSensorByType(host, port);
				try {
					sensorFoundList = allloc.findByType(false, light);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countMove = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs de présence: " + countMove);
				
			}
			
			if(e.getSource() == countTempSensor) {
				String light = 	"TEMPERATURE"; 
				FindSensorByType allloc = new FindSensorByType(host, port);
				try {
					sensorFoundList = allloc.findByType(false, light);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countTemperature = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs de température : " + countTemperature);
				
			}
			
			if (e.getSource() == countAllPatient) {
				FindAllPatient allloc = new FindAllPatient(host, port);
				try {
					patientFoundList = allloc.findAll(false);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countpatient = patientFoundList.size(); 	
				System.out.println("Nombre de patients : " + countpatient);
			}
			
			if (e.getSource() == countAlertByMonth) {
				FindAlertByMonthYear faby = new FindAlertByMonthYear(host, port);
				Date date = new Date();
				try {
					alertFoundList = faby.findByMonthYear(false, date);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countAlertMonthYear = alertFoundList.size(); 	
				System.out.println("Nombre d'alerte de l'année en cours: " + countAlertMonthYear);	
			}
			
			if (e.getSource() == countAlertByYear) {
				FindAlertByYear faby = new FindAlertByYear(host, port);
				Date date = new Date();
				try {
					alertFoundList = faby.findByYear(false, date);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countAlertYear = alertFoundList.size(); 	
				System.out.println("Nombre d'alerte de l'année en cours: " + countAlertYear);	
			}
			
			if(e.getSource() == countBreakdownSensor) {
				FindSensorByBreakdown fsbb = new FindSensorByBreakdown(host, port);
				try {
					sensorFoundList = fsbb.findByBreakdown(false, true);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countBreakdown = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs en panne : " + countBreakdown);	
			}

			
		}

	}
