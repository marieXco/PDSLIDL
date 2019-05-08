package fr.pds.floralis.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.pds.floralis.commons.bean.entity.Sensor;

/**
 * SensorTableModel
 * Class made to create the Table Model for the MainWindow GUI for the sensors
 * 
 * @author alveslaura
 *
 */

public class SensorTableModel extends AbstractTableModel {
	// Adding a serialVersionUID as the AbstractTableModel class extend Serializable
	private static final long serialVersionUID = 4371698137427718534L;

	private List<Sensor> userData = new ArrayList<Sensor>();
	private String[] columnNames = {"Id" ,"Marque", "Type", "Adresse Mac", "Date Installation", "Etat",
			"Premier Seuil", "Deuxième Seuil", "Alerte", "Configuration"};

	public SensorTableModel() {
	}

	public SensorTableModel(List<Sensor> sensorsList) {
		this.userData = sensorsList;
	}

	/**
	 * Mandatory because the class extends AbstractTableModel
	 * It isn't used in the code
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * Mandatory because the class extends AbstractTableModel
	 * It isn't used in the code
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Mandatory because the class extends AbstractTableModel
	 * It isn't used in the code
	 */
	@Override
	public int getRowCount() {
		return userData.size();
	}


	/**
	 * Puts the values at their good place
	 */
	public Object getValueAt(int row, int column) {
		Object userAttribute = null;
		Sensor userObject = userData.get(row);
		String on = "";

		if(userObject.getState()){
			on = "Allumé";
		} else {
			if(userObject.getConfigure()) on = "Eteint";
			else on = "";
		}

		String state = "";

		if(userObject.getAlert()) {
			state = "Alerte";
		} else {
			state = "RAS";
		}

		if(userObject.getBreakdown()) {
			state = "En panne";
		}

		String configure = "";
		String firstWarningLevel;
		String secondWarningLevel;

		if(userObject.getConfigure()) {
			configure = "Configuré";
			firstWarningLevel = Integer.toString(userObject.getMin());
			secondWarningLevel = Integer.toString(userObject.getMax());
		} else {
			configure = "A configurer";
			firstWarningLevel = "";
			secondWarningLevel = "";
		}




		switch (column) {
		case 0: 
			userAttribute = userObject.getId();
			break;
		case 1:
			userAttribute = userObject.getBrand();
			break;
		case 2:
			userAttribute = userObject.getType();
			break;
		case 3:
			userAttribute = userObject.getMacAddress();
			break;
		case 4:
			userAttribute = userObject.getInstallation();
			break;
		case 5:
			userAttribute = on;
			break;
		case 6:
			userAttribute = firstWarningLevel;
			break;
		case 7:
			userAttribute = secondWarningLevel;
			break;
		case 8:
			userAttribute = state;
			break;
		case 9:
			userAttribute = configure;
			break;
		default:
			break;
		}
		return userAttribute;
	}
}