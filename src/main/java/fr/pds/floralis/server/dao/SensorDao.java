package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;

public class SensorDao extends DAO<Sensor> {
	Connection connect = null;
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	public SensorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#create(org.json.JSONObject)
	 */
	@Override
	public JSONObject create(JSONObject jsonObject) {
		int success = 0;

		PGobject object1 = new PGobject();
		try {
			object1.setValue(jsonObject.toString()); // TODO Move in other try block
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
		} catch (SQLException e) {
			logger.severe(e.getMessage());
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//System.exit(0);
			success = 0;
		} finally {
			// TODO Release resources
		}
		
		JSONObject sensorCreated = new JSONObject();
		if (success > 0) {
			sensorCreated.put("successCreate", "true");
			return sensorCreated;
//		} else {
//			sensorCreated.put("successCreate", "false");
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#delete(org.json.JSONObject)
	 */
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


	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#update(org.json.JSONObject)
	 */
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

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#find(org.json.JSONObject)
	 */
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

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#findAll()
	 */
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
