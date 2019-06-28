package fr.pds.floralis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Clip;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.json.JSONException;

import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.tablemodel.SensorTableModel;

public class MyPanel extends JPanel implements MouseListener {
	BufferedImage image;
	Map<Rectangle, Integer> rectangle;

	private Point position;
	private Point mouse;

	JComboBox<Object> comboSensors = new JComboBox();
	List<Sensor> sensorsFoundList = new ArrayList<Sensor>();
	private JTable sensorsTable = new JTable();
	SensorTableModel sensorModel = new SensorTableModel();

	private String host;
	private int port;

	private JPanel sensorsLocated = new JPanel();

	public MyPanel(BufferedImage img1, Map<Rectangle, Integer> listRectangle,
			String host, int port, JTable sensorsTable, JComboBox comboSensors) {
		this.image = img1;
		this.rectangle = listRectangle;
		this.host = host;
		this.port = port;
		this.sensorsTable = sensorsTable;
		this.comboSensors = comboSensors;
		this.addMouseListener(this);

		this.add(sensorsLocated);
		this.add(sensorsTable);

		
	}

	@Override
	public Dimension getPreferredSize() {
		return image == null ? new Dimension(200, 200) : new Dimension(
				image.getWidth(), image.getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		System.out.println("Rentre dans le paintComponent");

		Graphics2D g2 = (Graphics2D) g.create();
		Graphics2D g3 = (Graphics2D) g.create();
		g3.setColor(Color.white);
		
		if (image != null) {
			int x = (getWidth() - image.getWidth()) / 2;
			int y = (getHeight() - image.getHeight()) / 2;
			g2.drawImage(image, x, y, this);
			rectangle.forEach((rectangle, id) -> {
				//g2.setColor(Color.white);
				// g2.translate(rectangle.x, rectangle.y);
					g2.draw(rectangle);
					g3.drawString(id.toString(), rectangle.x, rectangle.y);
					
				});
		}
		g2.dispose();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// récupérer la position du clique
		position = e.getPoint();
		System.out.println(position);

		findSensorByLocation(testLocation(position));
		allSensors();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void findSensorByLocation(int locationId) {
		System.out.println(locationId);
		try {
			FindSensorByLocation fs = new FindSensorByLocation(host, port);
			sensorsFoundList = fs.findByLocation(locationId);

			SensorTableModel sensorModelRefresh = new SensorTableModel(
					sensorsFoundList);

			String[] sensorsComboBox = new String[sensorModelRefresh
					.getRowCount() + 1];
			sensorsComboBox[0] = "-- Identifiant du capteur --";

			for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
				int tabIndex = listIndex + 1;
				sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex)
						.getId() + " ";
			}

			comboSensors.removeAllItems();

			for (int i = 0; i < sensorsComboBox.length; i++) {
				comboSensors.addItem(sensorsComboBox[i]);
			}

			sensorsTable.setModel(sensorModelRefresh);

		} catch (JSONException | IOException | InterruptedException e2) {
			e2.printStackTrace();
		}

	}

	private Integer testLocation(Point mouse) {
		Integer locationId = null;

		for (Map.Entry<Rectangle, Integer> entry : rectangle.entrySet()) {

			if (entry.getKey().getBounds().contains(mouse)) {

				locationId = entry.getValue();
				System.out.println(locationId);
			}

		}

		return locationId;

	}

	public void allSensors() {

		SensorTableModel sensorModelRefresh = new SensorTableModel(
				sensorsFoundList);

		String[] sensorsComboBox = new String[sensorModelRefresh.getRowCount() + 1];
		sensorsComboBox[0] = "-- Identifiant du capteur --";

		for (int listIndex = 0; listIndex < sensorsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			sensorsComboBox[tabIndex] = sensorsFoundList.get(listIndex).getId()
					+ " ";
		}

		comboSensors.removeAllItems();

		for (int i = 0; i < sensorsComboBox.length; i++) {
			comboSensors.addItem(sensorsComboBox[i]);
		}

		sensorsTable.setModel(sensorModelRefresh);

	}

	

}
