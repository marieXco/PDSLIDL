package fr.pds.floralis.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.pds.floralis.commons.bean.entity.Patient;

public class PatientsTableModel extends AbstractTableModel {
	// Adding a serialVersionUID as the AbstractTableModel class extend Serializable
	private static final long serialVersionUID = -5101989079624145067L;
	private List<Patient> userData = new ArrayList<Patient>();
	private String[] columnNames = { "Id", "Prénom", "Nom", "Code" };

	public PatientsTableModel() {
	}

	public PatientsTableModel(List<Patient> userData) {
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
		Patient userObject = userData.get(row);
		switch (column) {
		case 0:
			userAttribute = userObject.getId();
			break;
		case 1:
			userAttribute = userObject.getFirstname();
			break;
		case 2:
			userAttribute = userObject.getLastname();
			break;
		case 3:
			userAttribute = userObject.getCode();
			break;
		default:
			break;
		}
		return userAttribute;
	}

	public void addUser(Patient user) {
		userData.add(user);
		fireTableDataChanged();
	}
}