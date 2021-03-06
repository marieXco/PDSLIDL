
	package fr.pds.floralis.gui;


	import java.awt.BorderLayout;
	import java.awt.Dimension;
	import java.awt.Font;
	import java.awt.GridLayout;
	import java.awt.event.*;
	import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
	import java.util.List;

	import javax.swing.JButton;
	import javax.swing.JComboBox;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JPanel;
	import javax.swing.JTable;


	import org.json.JSONException;
	
	
	import fr.pds.floralis.commons.bean.entity.Alert;
	import fr.pds.floralis.commons.bean.entity.Location;
	import fr.pds.floralis.commons.bean.entity.Patient;
	import fr.pds.floralis.commons.bean.entity.Sensor;

	
	// Window for the indicators use case
	public class WindowStats extends JFrame implements ActionListener {
		private String host;
		private int port;

		
		// Creation of panels
		JPanel container = new JPanel();
		JPanel requestPanel = new JPanel();
		JPanel resultPanel = new JPanel(); 
		JPanel result = new JPanel(); 
		JPanel indicatorInfo= new JPanel();
		JPanel sensorPanel = new JPanel();
		JPanel alertPanel = new JPanel();
		JPanel panel= new JPanel();
		
		// Creation of combo box 
		JComboBox sensorOption = new JComboBox();
		JComboBox stateOption = new JComboBox();
		JComboBox breakOption = new JComboBox();
		JComboBox alertOption = new JComboBox();
		
		// Creation of Label 
		JLabel labelSensor= new JLabel("Filtrer sur les capteurs : ");
		JLabel labelState= new JLabel("Etats des capteurs : ");
		JLabel labelBreak= new JLabel("Panne des capteurs : ");
		JLabel labelAlert= new JLabel("Filtrer sur les alertes : ");
		
		// Creation of Button 
		JButton newRequest = new JButton("Nouvelle requête"); 
		
		
		// Creation of String 
		String ttemp = "Capteurs de température"; 
		String fire = "Capteurs de type fumée"; 
		String ggas = "Capteurs de fuite de gaz";
		String ppresence = "Capteurs de présence"; 
		String llight = "Capteurs de type lumière";
		String smoke = 	"FIRE";
		String presence = "PRESENCE"; 
		String light = 	"LIGHT"; 
		String gas = "GASLEAK"; 
		String temperature = "TEMPERATURE"; 
		
		// Indicators list
		List<Location> roomFoundList; 
		List<Sensor> sensorFoundList;
		List<Alert> alertFoundList;
		List<Patient> patientFoundList;
		

		public void initIndicators() throws IOException{
		    
		    // count patient
		    
			// Creation of an object to use the findAll method
			FindAllSensor allsens = new FindAllSensor(host, port);
			try {
				// put the result in list 
				sensorFoundList = allsens.findAll();
			} catch (JSONException | IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// count how many values the list has 
			int countSensor = sensorFoundList.size(); 
			// print the result 
			JLabel sensorResult = new JLabel("  Nombre de capteurs : " + countSensor);
			indicatorInfo.add(sensorResult);
			sensorResult.setFont(new Font("Calibri", 1, 20));
			
			// count room 
	
			FindAllLocation fal = new FindAllLocation(host, port);
			try {
				roomFoundList = fal.findAll();
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
				alertFoundList = faa.findAll();
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
				patientFoundList = fap.findAll();
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
		    
		    
		    // Sensor option - JComboBox
		    sensorOption.addItem("Aucun");
		    sensorOption.addItem("Type de capteurs");
		    sensorOption.addItem("Etats des capteurs");
		    sensorOption.addItem("Panne des capteurs");
		    sensorOption.addItem("Capteurs non configurés");
			sensorPanel.add(labelSensor);
			sensorPanel.add(sensorOption); 
			requestPanel.add(sensorPanel);
			sensorOption.addActionListener(this);
			
		 
		    
		    //Alert Option - JComboBox
			alertOption.addItem("Aucun");
			alertOption.addItem("Alerte par mois");
			alertOption.addItem("Alerte par an");
			alertOption.addItem("Alerte par type de capteurs");
			alertPanel.add(labelAlert);
		    alertPanel.add(alertOption); 
		    requestPanel.add(alertPanel);
		    alertOption.addActionListener(this);
		    requestPanel.setLayout(new GridLayout(2, 1));
		    resultPanel.add(newRequest); 
		    resultPanel.add(result); 
		    newRequest.addActionListener(this);
		    
		    panel.add(requestPanel);
		    panel.add(resultPanel);
		    panel.setPreferredSize(new Dimension(1350, 500));
		    panel.setLayout(new GridLayout(1, 2));
		   
			// initialize parameters window
			this.setTitle("Indicateurs");
		    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    this.setLocationRelativeTo(null);
		    this.setSize(900, 600);
		    this.setContentPane(container);
		    this.setVisible(true);   
		    this.getContentPane().add(indicatorInfo, BorderLayout.NORTH);
		    this.getContentPane().add(panel, BorderLayout.SOUTH);
		    this.setVisible(true);
			
		}
		
	

		public void actionPerformed(ActionEvent e) {
			
			/*
			 * to do a new request 
			 */
			
			/*
			 * SENSOR OPTION 
			 */
	
			// TODO Auto-generated method stub
				int indexSensor = sensorOption.getSelectedIndex();

				switch(indexSensor) {
				case 0 : 
				
					break; 
				case 1	: 
					// case of sensor type filter 
					
					/*
					 * SMOKE TYPE 
					 */
					
					
					FindSensorByType fsbtf = new FindSensorByType(host, port);
					try {
						sensorFoundList = fsbtf.findByType(smoke);
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
						sensorFoundList = fsbtl.findByType(light);
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
						sensorFoundList = fsbtg.findByType(gas);
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
						sensorFoundList = fsbtp.findByType(presence);
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
						sensorFoundList = fsbtt.findByType(temperature);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countTemperature = sensorFoundList.size(); 	
						
					// put values on JFRAME
					
					Object[][] data = {
		            { fire , countSmoke },
		            { llight, countLight},
		            { ggas, countGas},
		            { ppresence, countMove},
		            { ttemp, countTemperature},
					};

					String[] entetes = {"Description", "Nombre"};
					JTable tabResultType = new JTable(data, entetes); 
					result.add(tabResultType); 
					pack();
					break; 
				case 2: 
					
					// case of sensor state filter
					
					//TODO: fonctionne mais ne se raffraichit pas automatiquement
					
					stateOption.addItem("Aucun");
					stateOption.addItem("Eteint");
					stateOption.addItem("Allumé");
					sensorPanel.add(labelState); 
					sensorPanel.add(stateOption);
					stateOption.addActionListener(this);
					pack();
					break; 
					
				case 3: 
					
					// case of sensor breakdown filter
					
					breakOption.addItem("Aucun");
					breakOption.addItem("En panne");
					breakOption.addItem("En marche");
					sensorPanel.add(labelBreak); 
					sensorPanel.add(breakOption);
					breakOption.addActionListener(this);
					pack(); 
					break; 
					
				case 4: 
					
					// case of sensor which havent configuration 
					FindSensorByConfig fsbc = new FindSensorByConfig(host, port);
					try {
						sensorFoundList = fsbc.findByConfig(false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countNoConfig = sensorFoundList.size(); 	
					JLabel noConfig = new JLabel("Nombre de Capteurs non configurés : " + countNoConfig);	
					result.add(noConfig);
					pack(); 
					break; 
					
					
					default : 
						System.out.println("Cas non trouvé");
						break; 
				}
			
				
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
						sensorFoundList = fsbsoff.findByState(false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countOff = sensorFoundList.size(); 	
					JLabel offSensor = new JLabel("Nombres de capteurs éteint : " + countOff); 
					result.add(offSensor);
					break; 
				case 2: 
					
					/*
					 * case of on sensor 
					 */

					FindSensorByState fsbson = new FindSensorByState(host, port);
					try {
						sensorFoundList = fsbson.findByState(true);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countOn = sensorFoundList.size(); 	
					JLabel onSensor = new JLabel("Nombre de capteurs allumés : " + countOn);
					result.add(onSensor);
					break; 
					
					default : 
						System.out.println("Cas non trouvé");
				}
				

				int indexBreakSensor = breakOption.getSelectedIndex();
				switch(indexBreakSensor) {
				case 0:           	
					System.out.println("init");
					break; 
				case 1: 
					
					/*
					 * case of breakdown sensor
					 */
			
					FindSensorByBreakdown fsbb = new FindSensorByBreakdown(host, port);
					try {
						sensorFoundList = fsbb.findByBreakdown(true);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countBreakdown = sensorFoundList.size(); 	
					JLabel sensorBreakdown = new JLabel("Nombre de capteurs en panne : " + countBreakdown);	
					result.add(sensorBreakdown);
					break; 
				case 2: 
					
					/*
					 * case of working sensor
					 */
					FindSensorByBreakdown fsbw = new FindSensorByBreakdown(host, port);
					try {
						sensorFoundList = fsbw.findByBreakdown(false);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int countWorking = sensorFoundList.size(); 	
					JLabel sensorWorking = new JLabel("Nombre de capteurs fonctionnel : " + countWorking);	
					result.add(sensorWorking);
					break;
					
					default : 
						System.out.println("Cas non trouvé");
				}
				
				/*
				 * ALERT OPTION 
				 */
				
				int indexAlert = alertOption.getSelectedIndex();

				switch(indexAlert) {
				case 0: 
					break; 
				case 1: 
					// case of alert by month
					FindAlertByMonthYear fabmy = new FindAlertByMonthYear(host, port);
					Date date = new Date(2019, 05, 01);
					int countAlertMonthYear = 0;
						try {
							countAlertMonthYear = fabmy.findByMonthYear(date);
						} catch (JSONException | IOException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					String alertMY = "En cours: ";
					FindAlertByMonthYear fabmy1 = new FindAlertByMonthYear(host, port);
					
					Date date1 = new Date(2019, 04, 12);
					int countAlertMonthYear1 = 0;
						try {
							countAlertMonthYear1 = fabmy1.findByMonthYear(date1);
						} catch (JSONException | IOException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					String alertMY1= "Avril"; 
					Object[][] data = {
				           { alertMY , countAlertMonthYear },
				           { alertMY1, countAlertMonthYear1}
						};

					String[] entetes = {"Description", "Nombre"};
					JTable tabResultAMType = new JTable(data, entetes); 
					result.add(tabResultAMType); 
					pack(); 
						
					break; 
				case 2: 
					
					// case of alert by year 
					Date dateYear = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
					String yearString = sdf.format(dateYear);

			        Integer year = Integer.parseInt(yearString);
					JPanel panelYear = new JPanel(); 
					panelYear.setLayout(new GridLayout(5, 1));
					for ( int i=0; i<5; i++) {
						FindAlertByYear faby = new FindAlertByYear(host, port);
						int countAlertFound = 0;
						Date dateAlertYear = new Date(year, 01, 01);
						try {
						countAlertFound = faby.findByYear(dateAlertYear);
						} catch (JSONException | IOException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
						JLabel labelalertYear = new JLabel("Nombre d'alerte de l'année "+ year + " : " + countAlertFound);
						panelYear.add(labelalertYear);
						year--; 
					} 
					result.add(panelYear);
					pack();

					break; 
						
				case 3: 
					//  case of alert by sensor type 
					
					/*
					 * SMOKE TYPE 
					 */
					int countAlertSmoke = 0; 
					FindAlertBySensorByType fabyf = new FindAlertBySensorByType();
					try {
						countAlertSmoke= fabyf.findByType(smoke);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 	
					
				
					/*
					 *  LIGHT TYPE
					 */
					int countAlertLight = 0;
					FindAlertBySensorByType fabyl = new FindAlertBySensorByType();
					try {
						countAlertLight = fabyl.findByType(light);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		 
					

					/*
					 * GAS TYPE
					 */
					int countAlertGas = 0; 
					FindAlertBySensorByType fabyg = new FindAlertBySensorByType();
					try {
						countAlertGas = fabyg.findByType(gas);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			
					
					/*
					 *  PRESENCE TYPE
					 */
					int countAlertMove = 0; 
					FindAlertBySensorByType fabyp = new FindAlertBySensorByType();
					try {
						countAlertMove = fabyl.findByType(presence);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					

					/*
					 * TEMPERATURE TYPE 
					 */
					int countAlertTemperature = 0; 
					FindAlertBySensorByType fabyt = new FindAlertBySensorByType();
					try {
						countAlertTemperature = fabyt.findByType(temperature);
					} catch (JSONException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
						
					// put values on JFRAME
					
					Object[][] dataAlert = {
		            { fire , countAlertSmoke },
		            { llight, countAlertLight},
		            { ggas, countAlertGas},
		            { ppresence, countAlertMove},
		            { ttemp, countAlertTemperature},
					};

					String[] tetes = {"Description", "Nombre"};
					JTable tabResultAlertType = new JTable(dataAlert, tetes); 
					result.add(tabResultAlertType); 
					pack(); 
					break;
					
					
					default : 
						System.out.println("Cas non trouvé");
						break; 
				}
				// launch a new request 
				if (e.getSource() == newRequest) {
					result.removeAll();
					breakOption.removeAllItems();
					sensorPanel.remove(breakOption);
					sensorPanel.remove(labelBreak);
					stateOption.removeAllItems();
					sensorPanel.remove(stateOption);
					sensorPanel.remove(labelState);
				}
		} 

	}
	
	
	
	
	
