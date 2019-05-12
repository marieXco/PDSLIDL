
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
		JPanel alertPanel = new JPanel();
		JPanel panel= new JPanel();
		JComboBox sensorOption = new JComboBox();
		JComboBox stateOption = new JComboBox();
		JComboBox breakOption = new JComboBox();
		JComboBox alertOption = new JComboBox();
		JLabel labelSensor= new JLabel("Filtrer sur les capteurs : ");
		JLabel labelState= new JLabel("Etats des capteurs : ");
		JLabel labelBreak= new JLabel("Panne des capteurs : ");
		JLabel labelAlert= new JLabel("Filtrer sur les alertes : ");
		
		String ttemp = "Capteurs de température"; 
		String fire = "Capteurs de type fumée"; 
		String ggas = "Capteurs de fuite de gaz";
		String ppresence = "Capteurs de présence"; 
		String llight = "Capteurs de type lumière";
		String smoke = 	"FIRE";
		String presence = "PRESENCE"; 
		String light = 	"LIGHT"; 
		String gas = "GASLEAK"; 
		String temperature = 	"TEMPERATURE"; 
		
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
		    sensorOption.addItem("Capteurs non configurés");
			sensorPanel.add(labelSensor);
			sensorPanel.add(sensorOption); 
			requestPanel.add(sensorPanel);
			sensorOption.addActionListener(this);
			
		 
		    
		    //Alert Option 
			alertOption.addItem("Alerte par mois");
			alertOption.addItem("Alerte par an");
			alertOption.addItem("Alerte par type de capteurs");
			alertPanel.add(labelAlert);
		    alertPanel.add(alertOption); 
		    requestPanel.add(alertPanel);
		    alertOption.addActionListener(this);
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
		/*
			
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
			*/

		public void actionPerformed(ActionEvent e) {
			
			/*
			 * SENSOR OPTION 
			 */
	
			// TODO Auto-generated method stub
				int indexSensor = sensorOption.getSelectedIndex();

				switch(indexSensor) {
				case 0: 
					// case of sensor type filter 
					
					/*
					 * SMOKE TYPE 
					 */
					
					
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
					
					// case of sensor state filter
					
					//TODO: fonctionne mais ne se raffraichit pas automatiquement
					
					stateOption.addItem("Aucun");
					stateOption.addItem("Eteind");
					stateOption.addItem("Allumé");
					sensorPanel.add(labelState); 
					sensorPanel.add(stateOption);
					stateOption.addActionListener(this);
					break; 
					
				case 2: 
					
					// case of sensor breakdown filter
					
					breakOption.addItem("Aucun");
					breakOption.addItem("En panne");
					breakOption.addItem("En marche");
					sensorPanel.add(labelBreak); 
					sensorPanel.add(breakOption);
					breakOption.addActionListener(this);
					break; 
					
				case 3: 
					
					// case of sensor which havent configuration 
					FindSensorByConfig fsbc = new FindSensorByConfig(host, port);
					try {
						sensorFoundList = fsbc.findByConfig(false, false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countNoConfig = sensorFoundList.size(); 	
					JLabel noConfig = new JLabel("Nombre de Capteurs non configurés : " + countNoConfig);	
					resultPanel.add(noConfig);
					
					break; 
					
					
					default : 
						System.out.println("aie coup dur pour Célia");
						break; 
				}
				
				
				
				//cette partie la du code doit aller dans une autre méthode
				//comment bloquer la deuxieme ecoute de la 1ere JCOMBOBOx
				
				/* 
				 *  choice of state in JCombobox treatment
				 */
				
				int indexStateSensor = stateOption.getSelectedIndex();
				switch(indexStateSensor) {
				case 0: 
					System.out.println("init");
					break; 
				case 1: 
					
					/*
					 * case of off sensor
					 */
					
					FindSensorByState fsbsoff = new FindSensorByState(host, port);
					try {
						sensorFoundList = fsbsoff.findByState(false, false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countOff = sensorFoundList.size(); 	
					JLabel offSensor = new JLabel("Nombres de capteurs éteind : " + countOff); 
					resultPanel.add(offSensor);
					break; 
				case 2: 
					
					/*
					 * case of on sensor 
					 */

					FindSensorByState fsbson = new FindSensorByState(host, port);
					try {
						sensorFoundList = fsbson.findByState(false, true);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countOn = sensorFoundList.size(); 	
					JLabel onSensor = new JLabel("Nombre de capteurs allumés : " + countOn);
					resultPanel.add(onSensor);
					
					
					break; 
					
					default : 
						System.out.println("aie coup dur pour Célia");
				}
				
				// not working
				// TODO: ajouter le cas de panne par mois 
				int indexBreakSensor = breakOption.getSelectedIndex();
				switch(indexBreakSensor) {
				case 0:           	
					System.out.println("init");
					break; 
				case 1: 
					
					/*
					 * case of breakdown sensor
					 */
					//TODO: réinitialiser la page, la rafraichir 
			
					FindSensorByBreakdown fsbb = new FindSensorByBreakdown();
					try {
						sensorFoundList = fsbb.findByBreakdown(false, true);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countBreakdown = sensorFoundList.size(); 	
					JLabel sensorBreakdown = new JLabel("Nombre de capteurs en panne : " + countBreakdown);	
					resultPanel.add(sensorBreakdown);
					
					break; 
				case 2: 
					
					/*
					 * case of working sensor
					 */
					FindSensorByBreakdown fsbw = new FindSensorByBreakdown();
					try {
						sensorFoundList = fsbw.findByBreakdown(false, false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countWorking = sensorFoundList.size(); 	
					JLabel sensorWorking = new JLabel("Nombre de capteurs fonctionnel : " + countWorking);	
					resultPanel.add(sensorWorking);
					break;
					
					default : 
						System.out.println("aie coup dur pour Célia");
				}
				
				/*
				 * ALERT OPTION 
				 */
				
				int indexAlert = alertOption.getSelectedIndex();

				switch(indexAlert) {
				case 0: 
					//  case of alert by month 
					break; 
				case 1: 
					
					// case of alert by year 
					break; 
						
				case 2: 
					//  case of alert by sensor type 
// case of sensor type filter 
					
					/*
					 * SMOKE TYPE 
					 */
					
					FindAlertBySensorByType fabyf = new FindAlertBySensorByType();
					try {
						alertFoundList = fabyf.findByType(false, smoke);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countAlertSmoke = alertFoundList.size(); 	
					
					
					/*
					 *  LIGHT TYPE
					 */
					
					FindAlertBySensorByType fabyl = new FindAlertBySensorByType();
					try {
						alertFoundList = fabyl.findByType(false, light);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countAlertLight = alertFoundList.size(); 
					

					/*
					 * GAS TYPE
					 */
					
					FindAlertBySensorByType fabyg = new FindAlertBySensorByType();
					try {
						alertFoundList = fabyg.findByType(false, gas);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countAlertGas = alertFoundList.size(); 
					
					/*
					 *  PRESENCE TYPE
					 */
					
					FindAlertBySensorByType fabyp = new FindAlertBySensorByType();
					try {
						alertFoundList = fabyl.findByType(false, presence);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countAlertMove = alertFoundList.size(); 

					/*
					 * TEMPERATURE TYPE 
					 */
					
					FindAlertBySensorByType fabyt = new FindAlertBySensorByType();
					try {
						alertFoundList = fabyt.findByType(false, temperature);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countAlertTemperature = alertFoundList.size(); 

					/*List<Alert> alertgFoundList;
					List<Alert> alertlFoundList;
					List<Alert> alertpFoundList;
					List<Alert> alertTFoundList;*/
						
					// put values on JFRAME
					
					Object[][] donnees = {
		            { fire , countAlertSmoke },
		            { llight, countAlertLight},
		            { ggas, countAlertGas},
		            { ppresence, countAlertMove},
		            { ttemp, countAlertTemperature},
					};

					String[] entetes = {"Description", "Nombre"};
					JTable tabResultType = new JTable(donnees, entetes); 
					resultPanel.add(tabResultType); 
					break;
					
					
					default : 
						System.out.println("aie coup dur pour Célia");
						break; 
				}
		} 

	}
	
	
	
	
	
