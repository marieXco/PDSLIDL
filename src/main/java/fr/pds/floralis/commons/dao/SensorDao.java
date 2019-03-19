package fr.pds.floralis.commons.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.ResultSet;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.Sensors;

public class SensorDao extends DAO<Sensor> {

	public SensorDao(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override

	public boolean create(PGobject jsonObject) {
		try {
			connect.setAutoCommit(false);
			String sql = "INSERT INTO sensors (data) VALUES (?);";
			PreparedStatement statement = connect.prepareStatement(sql);
			statement.setObject(1, jsonObject);

			statement.execute();
			connect.commit();

			statement.close();
			// JSONRequest.JSONRequestPersonnels();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Insert success");
		return false;

	}

	@Override
	public boolean delete(PGobject jsonObject) {
		Statement stmt = null;
		try {
			connect.setAutoCommit(false);

			stmt = connect.createStatement();
			String sql = "SELECT data FROM personnels where data ->> 'id' = '" + jsonObject+ "';";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.execute();
			connect.commit();

			statement.close();
			// JSONRequest.JSONRequestPersonnels();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Delete success");
		return false;

	}


	@Override
	public boolean update(Sensor s) {
		Sensor sensor = new Sensor();
		sensor.setAlerts(s.getAlerts());
		sensor.setBrand(s.getBrand());
		sensor.setBreakdowns(s.getBreakdowns());
		sensor.setCaracteristics(s.getCaracteristics());
		sensor.setId(s.getId());
		sensor.setInstallation(s.getInstallation());
		sensor.setMacAdress(s.getMacAdress());
		sensor.setState(s.getState());
		sensor.setType(s.getType());

		JSONObject obj = new JSONObject(sensor);

		PGobject jsonObject = new PGobject();
		jsonObject.setType("json");

		try {
			jsonObject.setValue(obj.toString());
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		try {
			connect.setAutoCommit(false);
			// stmt = c.createStatement(); to keep ?

			String sql = "UPDATE sensors set data = (?) where id  = '" + s.getId() + "';";

			PreparedStatement statement = connect.prepareStatement(sql);
			statement.setObject(1, jsonObject);

			statement.execute();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		System.out.println("Update success");
		return false;
	}

	@Override
	public Sensor find(int id) {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM sensors;" );
			
			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(2).toString(), Sensor.class);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("find success");

		return sensor;
	}

	@Override
	public List<Sensor> findAll() {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = null;
		Sensor sensor = new Sensor();
		sensors = new ArrayList<Sensor>();

		try {
			connect.setAutoCommit(false);
			stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT id, data FROM sensors;");

			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(2).toString(), Sensor.class);
				// sensor.setId(Integer.parseInt(rs.getObject(1).toString()));
				System.out.println(sensors.toString());
				sensors.add(sensor);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Select success");
		return sensors;

	}



}
