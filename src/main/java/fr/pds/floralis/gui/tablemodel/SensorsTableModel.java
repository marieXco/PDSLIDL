package fr.pds.floralis.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

<<<<<<< HEAD
import fr.pds.floralis.commons.bean.entity.Sensor;
=======
>>>>>>> 3428ca928c2eeb2172880a930c6dfb82f884de59
import fr.pds.floralis.commons.bean.entity.Sensors;

public class SensorsTableModel extends AbstractTableModel {

<<<<<<< HEAD
	private List<Sensor> userData = new ArrayList<Sensor>();
	private String[] columnNames = { "Marque", "Adresse Mac", "Date Installation", "Etat",
=======
	private List<Sensors> userData = new ArrayList<Sensors>();
	private String[] columnNames = { "Marque", "AdresseMac", "DateInstallation", "Etat", "En alerte", "En panne",
>>>>>>> 3428ca928c2eeb2172880a930c6dfb82f884de59
			"Caractéristiques" };

	public SensorsTableModel() {
	}

<<<<<<< HEAD
	public SensorsTableModel(List<Sensor> sensorsList) {
		this.userData = sensorsList;
=======
	public SensorsTableModel(List<Sensors> userData) {
		this.userData = userData;
>>>>>>> 3428ca928c2eeb2172880a930c6dfb82f884de59
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return userData.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object userAttribute = null;
<<<<<<< HEAD
		Sensor userObject = userData.get(row);
		switch (column) {
		case 0:
			userAttribute = userObject.getBrand();
			break;
		case 1:
			userAttribute = userObject.getMacAdress();
			break;
		case 2:
			userAttribute = userObject.getInstallation();
			break;
		case 3:
			userAttribute = userObject.getState();
			break;
		case 4:
=======
		Sensors userObject = userData.get(row);
		switch (column) {
		case 1:
			userAttribute = userObject.getBrand();
			break;
		case 2:
			userAttribute = userObject.getMacAddress();
			break;
		case 3:
			userAttribute = userObject.getDateInstallation();
			break;
		case 4:
			userAttribute = userObject.getState();
			break;
		case 5:
			userAttribute = userObject.getAlert();
			break;
		case 6:
			userAttribute = userObject.getBreakdown();
			break;
		case 7:
>>>>>>> 3428ca928c2eeb2172880a930c6dfb82f884de59
			userAttribute = userObject.getCaracteristics();
			break;
		default:
			break;
		}
		return userAttribute;
	}

<<<<<<< HEAD
	public void addUser(Sensor user) {
=======
	public void addUser(Sensors user) {
>>>>>>> 3428ca928c2eeb2172880a930c6dfb82f884de59
		userData.add(user);
		fireTableDataChanged();
	}
}