package fr.pds.floralis.commons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;

public class SensorDao extends DAO<Sensor> {

	public SensorDao(Connection conn) {
		super(conn);
	}


	public boolean create(PGobject jsonObject) {
		boolean success = false;

		//TODO modifier et mettre un vrai object JSON

		try {
			//TODO vérifier que l'id n'existe pas déja
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
			System.out.println("create success");
		}

		return success;
	}

	@Override
	public boolean delete(JSONObject jsonObject) {
		boolean success = false;

		int sensorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.execute();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success == false) {
			System.out.println("delete success");
		}

		return true;
	}


	@Override
	public boolean update(JSONObject jsonObject) {
		boolean success = false;

		int sensorId = jsonObject.getInt("id");

		try {
			
			//TODO vérifier que l'id n'existe déja pas
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + sensorId + "'::json::text;;";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.execute();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success == true) {
			System.out.println("update success");
		}

		return success;
	}

	@Override
	public Sensor find(JSONObject jsonObject) {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = null;

		int sensorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;" );

			if (rs.next()) {
				sensor = new Sensor();
			}

			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(2).toString(), Sensor.class);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		if (sensor != null) {
			System.out.println("find success");
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

		if (sensors != null) {
			System.out.println("findAll success");
		}

		return sensors;
	}



}
