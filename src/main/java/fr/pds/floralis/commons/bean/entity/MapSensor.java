package fr.pds.floralis.commons.bean.entity;


import java.util.ArrayList;

/*
 * Entite pour la map des capteurs
 */
public class MapSensor {

	//Liste des alertes 
	ArrayList<Alert> alerts = new ArrayList<Alert>();

	// Liste des Sensors
	ArrayList<Sensor> sensors = new ArrayList<Sensor>();

	private int id;

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MapSensor(ArrayList<Sensor> sensors, int id) {
		super();
		this.sensors = sensors;
		this.id = id;
	}

	// TODO : voir avec Laure si methodes == ok

//	public void addSensor(Sensor sensor) {
//		this.sensors.add(sensor);
//	}
//
//	public void removeSensor(Sensor sensor) {
//		this.sensors.remove(sensor);
//	}

}
