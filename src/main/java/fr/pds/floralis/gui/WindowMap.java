package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;
import fr.pds.floralis.gui.tablemodel.SensorTableModel;

public class WindowMap extends JFrame implements ActionListener {

	private String host;
	private int port;

	public WindowMap(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	// TODO : refresh la map
	// TODO : refaire propre : il y a des trucs inutiles genre la liste des
	// capteurs non config
	// TODO : mettre le code au propre et commenter aussi

	private static final long serialVersionUID = 4187590358873827065L;

	private static int WIDTH = 500, HEIGHT = 250; // taille de la fenetre par
													// defaut
													// List<Sensor>
													// sensorsLocatedList = new
													// ArrayList<Sensor>();
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

	// TODO : bien placer les rectangles quand le serveur marchera
	private Rectangle entree = new Rectangle(25, 50, 80, 38);
	private Rectangle couloirRdc = new Rectangle(25, 140, 81, 85);
	private Rectangle couloirEtage = new Rectangle(25, 140, 81, 85);
	private Rectangle salleCommune = new Rectangle(120, 137, 145, 65);
	private Rectangle ascenseurRdc = new Rectangle(200, 50, 100, 50);
	private Rectangle ascenseurEtage = new Rectangle(200, 50, 100, 50);
	private Rectangle refectoire = new Rectangle(80, 95, 380, 25);
	private Rectangle cuisine = new Rectangle(80, 95, 380, 25);
	private Rectangle infirmerie = new Rectangle(80, 95, 380, 25);

	private BufferedImage img1;
	// private BufferedImage img2;

	private JTextField idSensortoLocate;
	private JButton addLocation;
	private JPanel showLocation;
	private static Sensor sensorToDisplay;
	private JTextField locationToconfig;
	JTextPane infos = new JTextPane();
	SimpleAttributeSet centrer = new SimpleAttributeSet();

	// Button
	// TODO : mettre ce bouton en dessous de la liste des capteurs d'une zone
	// pour direct ajouter dans la zone
	// TODO : il faut donc une liste des capteurs non config au clique de ce
	// bouton et
	// ajouter
	// TODO : il faut un capteur de chaque type par zone donc filtrer
	Button addingSensor = new Button("Ajouter un capteur");
	private JComboBox<Object> comboSensors1 = new JComboBox<Object>();
	private JTable sensorsTable1 = new JTable();

	public void initMap() {
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setSize(1200, 900);
		this.setAlwaysOnTop(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		listRectangle.put(entree, 1);
		listRectangle.put(couloirRdc, 2);
		listRectangle.put(couloirEtage, 3);
		listRectangle.put(salleCommune, 4);
		listRectangle.put(ascenseurRdc, 5);
		listRectangle.put(ascenseurEtage, 6);
		listRectangle.put(refectoire, 7);
		listRectangle.put(cuisine, 8);
		listRectangle.put(infirmerie, 9);

		try {
			img1 = ImageIO.read(new File("src/main/resources/images/map.png"));
			// img2 = ImageIO.read(new
			// File("src/main/resources/images/rdc.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		backPanel = new JPanel();
		MyPanel image = new MyPanel(img1, listRectangle, host, port,
				sensorsTable, comboSensors);
		// MyPanel image2 = new MyPanel(img2, listRectangle, host, port,
		// sensorsTable, comboSensors);

		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);
		infos.setParagraphAttributes(centrer, true);
		infos.setText("Ajout d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		backPanel.add(sensorsLocated);
		backPanel.add(sensorsTable);

		backPanel.add(comboSensors1);
		backPanel.add(sensorsTable1);

		noConfigSensors();

		FlowLayout fLayout = (FlowLayout) backPanel.getLayout();
		backPanel.setLayout(fLayout);
		backPanel.add(image, BorderLayout.NORTH);
		// backPanel.add(image2, BorderLayout.SOUTH);
		backPanel.add(new JLabel("Id du capteur à placer:"));
		idSensortoLocate = new JTextField(8);
		backPanel.add(idSensortoLocate);
		addLocation = new JButton("Attribuer une localisation");
		addLocation.addActionListener(this);
		backPanel.add(addLocation);

		this.setContentPane(backPanel);
		this.setVisible(true);

	}

	// public void allSensors() {
	// SensorTableModel sensorModelRefresh = new
	// SensorTableModel(sensorsFoundList);
	// String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() +
	// 1];
	// sensorsComboBox[0]= "-- Identifiant du capteur --";
	// for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++)
	// {
	// int tabIndex = listIndex + 1;
	// sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId() +
	// " ";
	// }
	// comboSensors.removeAllItems();
	// for (int i = 0; i < sensorsComboBox.length; i++) {
	// comboSensors.addItem(sensorsComboBox[i]);
	// }
	// sensorsTable.setModel(sensorModelRefresh);
	// }

	public void noConfigSensors() {
		try {
			FindSensorByConfig fs = new FindSensorByConfig(host, port);
			sensorsNoConfigList = fs.findByConfig(false);
			SensorTableModel sensorModelRefresh = new SensorTableModel(
					sensorsNoConfigList);
			String[] sensorsComboBox = new String[sensorModelRefresh
					.getRowCount() + 1];
			sensorsComboBox[0] = "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsNoConfigList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsNoConfigList.get(listIndex)
						.getId() + " ";
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
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton clickedButton = (JButton) e.getSource();
			if (clickedButton == addLocation) {

				this.sensorToDisplay = null;
				String idToFind = idSensortoLocate.getText().toString().trim();
				// try {
				// Integer.parseInt(idSensortoLocate.getText());
				// } catch (java.lang.NumberFormatException ex) {
				// infos.setText("L'identifiant ne peut contenir que des chiffres");
				// }
				if (idToFind.trim().length() > 0) {
					showLocation = new JPanel(new FlowLayout());
					infos.setText("");
					showLocation.add(infos);
					showLocation.validate();
					showLocation.remove(infos);
					showLocation.validate();
					int idFound = 0;
					try {
						idFound = Integer.parseInt(idToFind);

					} catch (java.lang.NumberFormatException ex) {
						showLocation = new JPanel(new FlowLayout());
						infos.setText("");
						showLocation.add(infos);
						showLocation.validate();
						showLocation.remove(infos);
						showLocation.validate();
						showLocation = new JPanel(new FlowLayout());
						showLocation.add(infos);

						infos.setText("L'identifiant ne peut contenir que des chiffres");
						backPanel.add(showLocation);
						backPanel.validate();
					}
					JSONObject sensorIdFindById = new JSONObject();
					sensorIdFindById.put("id", idFound);

					Request request = new Request();
					request.setType("FINDBYID");
					request.setEntity("SENSOR");
					request.setFields(sensorIdFindById);

					ConnectionClient ccSensorFindById = new ConnectionClient(
							host, port, request.toJSON().toString());
					ccSensorFindById.run();

					String retourSensorFindById = ccSensorFindById
							.getResponse();
					JSONObject sensorFoundJson = new JSONObject();
					sensorFoundJson
							.put("sensorFoundJson", retourSensorFindById);

					ObjectMapper objectMapper = new ObjectMapper();
					try {
						sensorToDisplay = objectMapper.readValue(
								sensorFoundJson.get("sensorFoundJson")
										.toString(), Sensor.class);
					} catch (Exception e1) {

						e1.printStackTrace();
					}
					if ((Object) sensorToDisplay == null) {
						showLocation = new JPanel(new FlowLayout());
						infos.setText("");
						showLocation.add(infos);
						showLocation.validate();
						showLocation.remove(infos);
						showLocation.validate();
						showLocation = new JPanel(new FlowLayout());
						showLocation.add(infos);

						infos.setText("Ce capteur n'existe pas");
						backPanel.add(showLocation);
						backPanel.validate();
					} else {
						showPopupToSetLocation();
					}
				} else {
					showLocation = new JPanel(new FlowLayout());
					infos.setText("");
					showLocation.add(infos);
					showLocation.validate();
					showLocation.remove(infos);
					showLocation.validate();
					showLocation = new JPanel(new FlowLayout());
					showLocation.add(infos);

					infos.setText("Veuillez saisir un identifiant");
					backPanel.add(showLocation);
					backPanel.validate();

				}

			} else if (clickedButton == addLocation) {

				String locationId = locationToconfig.getText().toString()
						.trim();
				if (locationId.trim().length() > 0) {
					showLocation = new JPanel(new FlowLayout());
					infos.setText("");
					showLocation.add(infos);
					showLocation.validate();
					showLocation.remove(infos);
					showLocation.validate();
					try {
						int idLocation = Integer.parseInt(locationId);
						sensorToDisplay.setIdLocation(idLocation);
						showLocation = new JPanel(new FlowLayout());
						infos.setText("");
						showLocation.add(infos);
						showLocation.validate();
						showLocation.remove(infos);
						showLocation.validate();
						showLocation = new JPanel(new FlowLayout());
						showLocation.add(infos);

						infos.setText("OK");
						backPanel.add(showLocation);
						backPanel.validate();

					} catch (java.lang.NumberFormatException ex) {
						showLocation = new JPanel(new FlowLayout());
						infos.setText("");
						showLocation.add(infos);
						showLocation.validate();
						showLocation.remove(infos);
						showLocation.validate();
						showLocation = new JPanel(new FlowLayout());
						showLocation.add(infos);

						infos.setText("L'identifiant ne peut contenir que des chiffres");
						backPanel.add(showLocation);
						backPanel.validate();
					}
					// Beginning of sensor Update

					JSONObject sensorUpdateJson = new JSONObject();
					sensorUpdateJson.put("id", sensorToDisplay.getId());
					sensorUpdateJson.put("sensorToUpdate",
							sensorToDisplay.toJSON());

					Request thirdRequest = new Request();
					thirdRequest.setType("UPDATELOCATIONID");
					thirdRequest.setEntity("SENSOR");
					thirdRequest.setFields(sensorUpdateJson);

					ConnectionClient ccSensorUpdate = new ConnectionClient(
							host, port, thirdRequest.toJSON().toString());
					ccSensorUpdate.run();
				}

				else {
					showLocation = new JPanel(new FlowLayout());
					infos.setText("");
					showLocation.add(infos);
					showLocation.validate();
					showLocation.remove(infos);
					showLocation.validate();
					showLocation = new JPanel(new FlowLayout());
					showLocation.add(infos);

					infos.setText("Veuillez saisir un identifiant");
					backPanel.add(showLocation);
					backPanel.validate();
				}
				// End sensorUpdate
			}
		}

	}

	private void showPopupToSetLocation() {
		showLocation = new JPanel(new FlowLayout());
		JTextField locationToconfig = new JTextField(10);
		FlowLayout fLayout = (FlowLayout) showLocation.getLayout();
		showLocation.add(new JLabel("Nouvelle localisation du capteur:"));
		showLocation.add(locationToconfig);
		JButton validateLocation = new JButton("Valider");
		showLocation.add(validateLocation, BorderLayout.SOUTH);
		backPanel.add(showLocation, BorderLayout.SOUTH);
		backPanel.validate();

	}

}
