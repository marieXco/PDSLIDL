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
	}


	public boolean create(PGobject jsonObject) {
		boolean success = false;
		
		try {
			connect.setAutoCommit(false);
			String sql = "INSERT INTO sensors (data) VALUES (?);";
			
			PreparedStatement statement = connect.prepareStatement(sql);
			statement.setObject(1, jsonObject);

			statement.execute();
			connect.commit();

			if(statement.executeUpdate() > 0) {
				success = true;
			}

			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		if(success == true) {
			System.out.println("Insert success");
		}
		
		return success;
	}

	@Override
	public boolean delete(JSONObject jsonObject) {
		boolean success = false;
		
		String identifiant = Integer.toString(jsonObject.getInt("id"));

		try {
			connect.setAutoCommit(false);

			//impossible à faire en JSON
			String sql = "DELETE FROM sensors where data ->> 'id' = " + identifiant + ";";
			PreparedStatement statement = connect.prepareStatement(sql);

			statement.execute();
			connect.commit();

			if(statement.executeUpdate() > 0) {
				return true;
			}

			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		if(success == true) {
			System.out.println("Delete success");
		}
		return success;

	}


	@Override
	public boolean update(Sensor s) {
		boolean success = false;
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
			
			//Pas compliqué, window à faire 
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors set data = (?) where data ->> id  = '" + s.getId() + "';";

			PreparedStatement statement = connect.prepareStatement(sql);
			statement.setObject(1, jsonObject);
			
			if(statement.executeUpdate() > 0) {
				success = true;
			}

			statement.execute();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success == true) {
			System.out.println("Update success");
		}
		
		return success;
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
				System.out.println(sensors.toString());
				sensors.add(sensor);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}		
		return sensors;
	}



}
