package fr.pds.floralis.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import fr.pds.floralis.commons.bean.entity.Sensor;

public class SensorsTableModel extends AbstractTableModel {
	// Adding a serialVersionUID as the AbstractTableModel class extend Serializable
	private static final long serialVersionUID = 4371698137427718534L;
	
	private List<Sensor> userData = new ArrayList<Sensor>();
	private String[] columnNames = {"Id" ,"Marque", "Adresse Mac", "Date Installation", "Etat",
			"Caractéristiques"};

	public SensorsTableModel() {
	}


	public SensorsTableModel(List<Sensor> sensorsList) {
		this.userData = sensorsList;
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
		Sensor userObject = userData.get(row);
		String on = "";
		
		if(userObject.getState() == true) {
			on = "Allumé";
		} else {
			on = "Eteint";
		}
		
		String state = "";
		
		if(userObject.getAlerts() != null) {
			state = "Alerte";
		} else {
			state = "RAS";
		}
		
		if(userObject.getBreakdowns() != null) {
			state = "En panne";
		}
		
		switch (column) {
		case 0: 
			userAttribute = userObject.getId();
			break;
		case 1:
			userAttribute = userObject.getBrand();
			break;
		case 2:
			userAttribute = userObject.getMacAdress();
			break;
		case 3:
			userAttribute = userObject.getInstallation();
			break;
		case 4:
			userAttribute = on;
			break;
		case 5:
			userAttribute = userObject.getCaracteristics();
			break;
//		case 6:
//			userAttribute = state;
//			break;
		default:
			break;
		}
		return userAttribute;
	}


	public void remove() {
		while(!(this.userData.isEmpty())) {
			this.userData.remove(this.userData.size()-1);
		}
		fireTableDataChanged();
	}
	
	public void add(List<Sensor> sensors) {
		this.userData = sensors; 
		fireTableDataChanged();
	}
}