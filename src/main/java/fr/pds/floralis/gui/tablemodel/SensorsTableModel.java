package fr.pds.floralis.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.pds.floralis.commons.bean.entity.Sensors;

public class SensorsTableModel extends AbstractTableModel {

	private List<Sensors> userData = new ArrayList<Sensors>();
	private String[] columnNames = { "Marque", "AdresseMac", "DateInstallation", "Etat", "En alerte", "En panne",
			"Caractéristiques" };

	public SensorsTableModel() {
	}

	public SensorsTableModel(List<Sensors> userData) {
		this.userData = userData;
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
			userAttribute = userObject.getCaracteristics();
			break;
		default:
			break;
		}
		return userAttribute;
	}

	public void addUser(Sensors user) {
		userData.add(user);
		fireTableDataChanged();
	}
}