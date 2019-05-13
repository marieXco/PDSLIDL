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
import fr.pds.floralis.commons.bean.entity.TypeSensor;


public class SensorDao implements DAO<Sensor> {
	Connection connect = null;
	Logger logger = Logger.getLogger(this.getClass().getName());

	public SensorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}


	public boolean create(Sensor sensorToCreate) {
		JSONObject jsonObject = sensorToCreate.toJSON();
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

		//		JSONObject sensorCreated = new JSONObject();
		//		if (success > 0) {
		//			sensorCreated.put("successCreate", "true");
		//			return sensorCreated;
		//		} else {
		//			sensorCreated.put("successCreate", "false");
		//		}
		//
		//		return null;
		if(success > 0) {
			return true;
		} else {
			return false;
		}

	}

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#delete(org.json.JSONObject)
	 */
	@Override
	public boolean delete(int id) {
		int success = 0;

		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM sensors where (data -> 'id')::json::text = '" + id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			System.out.println();
			connect.commit();

			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			return true;
		} else {
			return false;
		}

	}


	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#update(org.json.JSONObject)
	 */
	@Override
	public boolean update(int id, Sensor sensorToUpdate) {
		int success = 0;
		JSONObject jsonObject = sensorToUpdate.toJSON();

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();


			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			return true;
		} else {
			return false;
		}

	}
	
	public Boolean updateLocation(int idLocation, Sensor sensorToConfigure) {
		int success = 0;
		JSONObject jsonObject = sensorToConfigure.toJSON();

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors SET data = '" + jsonObject + "' WHERE (data -> 'idLocation')::json::text = '" + idLocation + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();


			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#find(org.json.JSONObject)
	 */
	@Override
	public Sensor find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data -> 'id')::json::text = '" + id + "'::json::text;" );

			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(1).toString(), Sensor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return sensor;
	}

	/* (non-Javadoc)
	 * @see fr.pds.floralis.server.dao.TestDAO#findAll()
	 */
	@Override
	public List<Sensor> findAll() {
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
		
		return sensors;
	}
	

	public List<Sensor> findByConfig(Boolean configure) {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM sensors where (data -> 'configure')::json::text = '" + configure + "'::json::text;");

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
		System.out.println();
		
		return sensors;
	}
	
	public List<Sensor> findByType (String type) {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data ->> 'type')::text = '" + type + "'::text;" );

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
		System.out.println();
		
		return sensors;
		
	}
	
	public List<Sensor> findByState (Boolean state) {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data -> 'state')::text = '" + state + "'::text;");
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
		System.out.println();
		
		return sensors;
	}
	
	public List<Sensor> findByBreakdown (Boolean breakdown) {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data ->> 'breakdown')::text = '" + breakdown + "'::text;");
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
		System.out.println();
		
		return sensors;
	}


	public List<Sensor> findByLocation(int idLocation) {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();
		
		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data -> 'idLocation')::json::text = '" + idLocation + "'::json::text;");
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
		System.out.println();
		
		return sensors;
	}




}
