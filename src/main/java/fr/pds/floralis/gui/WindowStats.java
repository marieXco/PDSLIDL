
	package fr.pds.floralis.gui;


	import java.awt.BorderLayout;
	import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
	import java.awt.event.*;
	import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
	import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JPanel;
	import javax.swing.JPasswordField;
import javax.swing.JTable;
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
		
	
		
		
		// Creation of panels
		JPanel container = new JPanel();
		JPanel requestPanel = new JPanel();
		JPanel resultPanel = new JPanel(); 
		JPanel indicatorInfo= new JPanel();
		JPanel sensorPanel = new JPanel();
		JPanel panel= new JPanel();
		JComboBox stateOption = new JComboBox();
		JComboBox sensorOption = new JComboBox();
		JComboBox breakOption = new JComboBox();
		JLabel labelSensor= new JLabel("Filtrer sur les capteurs : ");
		JLabel labelState= new JLabel("Etats des capteurs : ");
		JLabel labelBreak= new JLabel("Panne des capteurs : ");
		
		
		// Indicators values
		List<Location> roomFoundList; 
		List<Sensor> sensorFoundList;
		List<Alert> alertFoundList;
		List<Patient> patientFoundList;
		
		
		

		public void initIndicators() throws IOException{
			
			this.setTitle("Indicateurs");
		    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    this.setLocationRelativeTo(null);
		    this.setSize(900, 600);
		    
		    // count patient
		    
			FindAllSensor allsens = new FindAllSensor(host, port);
			try {
				sensorFoundList = allsens.findAll(false);
			} catch (JSONException | IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int countSensor = sensorFoundList.size(); 	
			JLabel sensorResult = new JLabel("  Nombre de capteurs : " + countSensor);
			indicatorInfo.add(sensorResult);
			sensorResult.setFont(new Font("Calibri", 1, 20));
			
			// count room 
	
			FindAllLocation fal = new FindAllLocation(host, port);
			try {
				roomFoundList = fal.findAll(false);
			} catch (JSONException | IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			int countRoom = roomFoundList.size(); 	
			JLabel roomResult = new JLabel("Nombre de pièces : " + countRoom);
		    indicatorInfo.add(roomResult);
		    roomResult.setFont(new Font("Calibri", 1, 20));
		    
		    // count alert
		    
		    /*
			FindAllAlert faa = new FindAllAlert(host, port);
			roomResult.setFont(new Font("Calibri", 1, 20));
			try {
				alertFoundList = faa.findAll(false);
			} catch (JSONException | IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			int countAlert = alertFoundList.size(); 	
			JLabel alertResult = new JLabel("Nombre d'alertes : " + countAlert);
			alertResult.setFont(new Font("Calibri", 1, 20));
			indicatorInfo.add(alertResult);
			*/
			
			// count patient
			
		    FindAllPatient fap = new FindAllPatient(host, port);
			try {
				patientFoundList = fap.findAll(false);
			} catch (JSONException | IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int countpatient = patientFoundList.size(); 	
		    JLabel patientResult = new JLabel("Nombre de patients : " + countpatient );
		    patientResult.setFont(new Font("Calibri", 1, 20));
		    indicatorInfo.add(patientResult);
		    indicatorInfo.setPreferredSize(new Dimension(1350, 150));
		    indicatorInfo.setLayout(new GridLayout(1, 4));
		    
		    
		    // Sensor option 
		    
		    sensorOption.addItem("Type de capteurs");
		    sensorOption.addItem("Etats des capteurs");
		    sensorOption.addItem("Panne des capteurs");
			sensorPanel.add(labelSensor);
			sensorPanel.add(sensorOption); 
			requestPanel.add(sensorPanel);
			sensorOption.addActionListener(this);
			
		 
		    
		    //Panel choice of alert
		    JButton alert = new JButton("Jcombo box des alertes");
		    requestPanel.add(alert);
		    //requestPanel.setPreferredSize(new Dimension(700, 200));
		    requestPanel.setLayout(new GridLayout(2, 1));
		    
		   //TODO: ajouter des éléments dans la bdd pour les tests (patients, anciennes alertes) 
		    //TODO: implémenter la comparaison sur les 12 mois, cette partie doit se trouver dans l'item listener des dates 

		    resultPanel.setLayout(new GridLayout(1, 1));
		    
		    panel.add(requestPanel);
		    panel.add(resultPanel);
		    panel.setPreferredSize(new Dimension(1350, 500));
		    panel.setLayout(new GridLayout(1, 2));
		    
		   // container.add(top, BorderLayout.NORTH);
		    this.setContentPane(container);
		    this.setVisible(true);   
		    this.getContentPane().add(indicatorInfo, BorderLayout.NORTH);
		    this.getContentPane().add(panel, BorderLayout.SOUTH);
		    this.setVisible(true);
		 
		    
			
		}
		
	
			    
		
		
			
		//OLD ACTION PERFORMED 
				
				/*countSmokeSensor.setVisible(true);
				countLightSensor.setVisible(true);
				countGasSensor.setVisible(true);
				countMoveSensor.setVisible(true);
				countTempSensor.setVisible(true);
				countOffSensor.setVisible(true);
				countOnSensor.setVisible(true);
				countNoLocationSensor.setVisible(true);*/
			
			
		/*	if(e.getSource() == countOffSensor) {
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
				FindSensorByBreakdown fsbb = new FindSensorByBreakdown();
				try {
					sensorFoundList = fsbb.findByBreakdown(false, true);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countBreakdown = sensorFoundList.size(); 	
				System.out.println("Nombre de Capteurs en panne : " + countBreakdown);	
			}
			
			if (e.getSource() == countAlertBySensorType) {
				FindAlertBySensorByType faby = new FindAlertBySensorByType();
				String type = "FIRE";
				try {
					alertFoundList = faby.findByType(false, type);
				} catch (JSONException | IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int countAlertType = alertFoundList.size(); 	
				System.out.println("Nombre d'alerte d'alerte de capteurs de fumée: " + countAlertType);	 
			}*/

		public void actionPerformed(ActionEvent e) {
	
			// TODO Auto-generated method stub
				int indexSensor = sensorOption.getSelectedIndex();

				switch(indexSensor) {
				case 0: 
					// create a list of sensor by their type 
					
					/*
					 * SMOKE TYPE 
					 */
					
					String fire = "Capteurs de type fumée"; 
					String smoke = 	"FIRE"; 
					FindSensorByType fsbtf = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtf.findByType(false, smoke);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countSmoke = sensorFoundList.size(); 
					
					/*
					 *  LIGHT TYPE
					 */
					
					String llight = "Capteurs de type lumière";
					String light = 	"LIGHT"; 
					FindSensorByType fsbtl = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtl.findByType(false, light);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countLight = sensorFoundList.size(); 	

					/*
					 * GAS TYPE
					 */
					
					String ggas = "Capteurs de fuite de gaz";
					String gas = "GASLEAK"; 
					FindSensorByType fsbtg = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtg.findByType(false, gas);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countGas = sensorFoundList.size(); 
					
					/*
					 *  PRESENCE TYPE
					 */
					
					String ppresence = "Capteurs de présence"; 
					String presence = "PRESENCE"; 
					FindSensorByType fsbtp = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtp.findByType(false, presence);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countMove = sensorFoundList.size(); 

					/*
					 * TEMPERATURE TYPE 
					 */

					String ttemp = "Capteurs de température"; 
					String temperature = 	"TEMPERATURE"; 
					FindSensorByType fsbtt = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtt.findByType(false, temperature);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countTemperature = sensorFoundList.size(); 	
						
					// put values on JFRAME
					
					Object[][] donnees = {
		            { fire , countSmoke },
		            { llight, countLight},
		            { ggas, countGas},
		            { ppresence, countMove},
		            { ttemp, countTemperature},
					};

					String[] entetes = {"Description", "Nombre"};
					JTable tabResultType = new JTable(donnees, entetes); 
					resultPanel.add(tabResultType); 
					break; 
				case 1: 
					System.out.print("je suis un second test");
					//TODO: fonctionne mais ne se raffraichit pas automatiquement
					stateOption.addItem("Aucun");
					stateOption.addItem("Eteind");
					stateOption.addItem("Allumé");
					sensorPanel.add(labelState); 
					sensorPanel.add(stateOption);
					stateOption.addActionListener(this);
					break; 
				case 2: 
					System.out.println("Il se passe des choses ");
					breakOption.addItem("Aucun");
					breakOption.addItem("En panne");
					breakOption.addItem("En marche");
					sensorPanel.add(labelBreak); 
					sensorPanel.add(breakOption);
					breakOption.addActionListener(this);
					break; 
					
					default : 
						System.out.println("aie coup dur pour Célia");
						break; 
				}
				//cette partie la du code doit aller dans une autre méthode
				//comment bloquer la deuxieme ecoute de la 1ere JCOMBOBOx
				int indexStateSensor = stateOption.getSelectedIndex();
				switch(indexStateSensor) {
				case 0: 
					System.out.println("Rien est sélectionné, init");
					break; 
				case 1: 
					System.out.println("je suis dans tout mes états");
					break; 
				case 2: 
					System.out.println("Je fonctionne bien ");
					break; 
					
					default : 
						System.out.println("aie coup dur pour Célia");
				}
				
				int indexBreakSensor = breakOption.getSelectedIndex();
				switch(indexBreakSensor) {
				case 0: 
					System.out.println("Rien est sélectionné, init");
					break; 
				case 1: 
					System.out.println("je suis en panne");
					break; 
				case 2: 
					System.out.println("Je marche bien ");
					break; 
					
					default : 
						System.out.println("aie coup dur pour Célia");
				}
		} 

	}
	
	
	
	
	
