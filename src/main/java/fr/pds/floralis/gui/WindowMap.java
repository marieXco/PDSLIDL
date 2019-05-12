package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.json.JSONException;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;

import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.tablemodel.SensorTableModel;

public class WindowMap extends JFrame  implements ActionListener{

	/*
	 * //TODO : faire deux listes //initalisées une avec les capteurs non config
	 * l'autre avec les capteurs config quand ajouter un capteurs prendre de la
	 * liste non config pour mettre dans les config et inversement ajouter un
	 * jpanel pour mettre les listes au bouton adding sensor faire vérification
	 * (modifier la BDD en conséquence) nombre capteurs max par location
	 * demander à marie au bouton delete retirer de la liste
	 * 
	 * refresh state du capteurs pour afficher en rouge si nécessaire
	 * 
	 * faire mag propre
	 * 
	 * dessiner les rectangles correctement
	 */

	private String host;
	private int port;

	public WindowMap(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	private static final long serialVersionUID = 4187590358873827065L;

	private static int WIDTH = 500, HEIGHT = 250; // taille de la fenetre par
													// defaut
//	List<Sensor> sensorsLocatedList = new ArrayList<Sensor>();
	List<Sensor> sensorsNoConfigList = new ArrayList<Sensor>();
	

	/**
	 * Elements of the sensor panel
	 */
	JComboBox<Object> comboSensors = new JComboBox<Object>();
	List<Sensor> sensorsFoundList = new ArrayList<Sensor>();
	private JTable sensorsTable = new JTable();
	SensorTableModel sensorModel = new SensorTableModel();
	private JTable sensorsLocated = new JTable();

	Map<Rectangle, Integer> listRectangle = new HashMap<>();
	
	private JPanel backPanel;

	private Rectangle salledejeu = new Rectangle(85, 83, 81, 58);
	private Rectangle foyer = new Rectangle(84, 174, 81, 58);
	private Rectangle toilettes = new Rectangle(177, 165, 43, 70);
	private Rectangle ascenseur = new Rectangle(275, 77, 60, 230);

	private int nbRect = 0; // nb de rectangles dans le frame
	static int defX = WIDTH / 5, defY = HEIGHT / 5; // dimensions d'un rectangle
	private int CoordX = 0, CoordY = 0; // Coordonnées de départ du quadrillage
	private Builder builder;
	private BufferedImage img1;
	private BufferedImage img2;

	private JTextField idSensortoLocate;
	private JButton addLocation;
	private JPanel showLocation;


	// Button
	Button addingSensor = new Button("Ajouter un capteur");
	private JComboBox<Object> comboSensors1 = new JComboBox<Object>();
	private JTable sensorsTable1 = new JTable();

	
	public void initMap() {
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setSize(1200, 900);
		this.setAlwaysOnTop(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		listRectangle.put(salledejeu, 1);
		listRectangle.put(foyer, 2);
		listRectangle.put(toilettes, 3);
		listRectangle.put(ascenseur, 4);

		try {
			img1 = ImageIO
					.read(new File("src/main/resources/images/etage.png"));
			img2 = ImageIO.read(new File("src/main/resources/images/rdc.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		backPanel = new JPanel();
		MyPanel image = new MyPanel(img1, listRectangle, host, port, sensorsTable, comboSensors);
		MyPanel image2 = new MyPanel(img2, listRectangle, host, port, sensorsTable, comboSensors);
		
		
		
		backPanel.add(sensorsLocated);
		backPanel.add(sensorsTable);
		
		backPanel.add(comboSensors1);
		backPanel.add(sensorsTable1);
		
		noConfigSensors();
		
		FlowLayout fLayout = (FlowLayout) backPanel.getLayout();
		backPanel.setLayout(fLayout);
		backPanel.add(image, BorderLayout.NORTH);
		backPanel.add(image2, BorderLayout.SOUTH);
		backPanel.add(new JLabel("Saisir l'id du capteur à placer : "));
		idSensortoLocate = new JTextField(20);
		backPanel.add(idSensortoLocate);
		addLocation = new JButton("Attribuer une localisation");
		addLocation.addActionListener(this);
		backPanel.add(addLocation);
		
		this.setContentPane(backPanel);
		this.setVisible(true);
		
	}
	
	public void allSensors() {
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
	}
	
	public void noConfigSensors() {
		try {
			FindSensorByConfig fs = new FindSensorByConfig(host, port);
			sensorsNoConfigList = fs.findByConfig(false, false);
			SensorTableModel sensorModelRefresh = new SensorTableModel(sensorsNoConfigList);
			String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1]; 
			sensorsComboBox[0]= "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsNoConfigList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsNoConfigList.get(listIndex).getId() + " ";
			}
			comboSensors1.removeAllItems();
			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors1.addItem(sensorsComboBox[i]);
			}
			sensorsTable1.setModel(sensorModelRefresh);
		} catch (JSONException | IOException | InterruptedException e2) {
			e2.printStackTrace();
		}
	
	}
	
	@Override	
	public void actionPerformed (ActionEvent e){
		if(e.getSource() instanceof JButton)
		{
			JButton clickedButton = (JButton)e.getSource();
			if(clickedButton == addLocation){
				
				showPopupToSetLocation();	
				
			}
		}

	}
	

	private void showPopupToSetLocation() {
		showLocation = new JPanel(new GridLayout(0, 2, 10, 2));
		
		JTextField locationToconfig= new JTextField(15);

		showLocation.add(new JLabel("Nouvelle localisation du capteur:"));
		showLocation.add(locationToconfig);
		backPanel.add(showLocation);
		backPanel.validate();
		//showLocation.setVisible(true);
		// showLocation.setEnabled(true);
	}


	

	

	

}
