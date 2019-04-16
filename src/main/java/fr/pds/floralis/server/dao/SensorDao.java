package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;

public class SensorDao extends DAO<Sensor> {
	Connection connect = null;
	
	public SensorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	public JSONObject create(JSONObject jsonObject) {
		int success = 0;

		PGobject object1 = new PGobject();
		try {
			object1.setValue(jsonObject.toString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		object1.setType("json");

		try {
			connect.setAutoCommit(false);

			String sql = "INSERT INTO sensors (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object1);

			success = statement.executeUpdate(); 
			connect.commit();

			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject sensorCreated = new JSONObject();

		if (success > 0) {
			sensorCreated.put("successCreate", "true");
		} else {
			sensorCreated.put("successCreate", "false");
		}

		return sensorCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int sensorId = jsonObject.getInt("id");


		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			System.out.println();
			connect.commit();

			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject sensorDeleted = new JSONObject();	
		if (success > 0) {
			sensorDeleted.put("successDelete", "true");
		} else {
			sensorDeleted.put("successDelete", "false");
		}

		return sensorDeleted;
	}


	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		
		int idSensor = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + idSensor + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();


			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject sensorUpdated = new JSONObject();
		
		if (success > 0) {
			sensorUpdated.put("successUpdate", "true");
		} else {
			sensorUpdated.put("successUpdate", "false");
		}
		
		return sensorUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = new Sensor();

		int sensorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;" );

			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(1).toString(), Sensor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject sensorFound = new JSONObject();
		sensorFound.put("sensorFound", sensor.toString());
		
		return sensorFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM sensors;");

			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(1).toString(), Sensor.class);
				sensors.add(sensor);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	

		JSONObject sensorsList = new JSONObject();
		sensorsList.put("sensorList", sensors.toString());
		
		return sensorsList;
	}
}
