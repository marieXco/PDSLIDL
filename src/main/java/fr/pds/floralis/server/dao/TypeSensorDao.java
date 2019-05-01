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

import fr.pds.floralis.commons.bean.entity.TypeSensor;

public class TypeSensorDao implements DAO<TypeSensor>{
	Connection connect = null;
	Logger logger = Logger.getLogger(this.getClass().getName());

	public TypeSensorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	
	public boolean create(TypeSensor objToCreate) {
		JSONObject jsonObject = objToCreate.toJSON();
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

			String sql = "INSERT INTO ref_sensors (data) VALUES (?);";

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

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(int id, TypeSensor objToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TypeSensor find(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<TypeSensor> findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<TypeSensor> typeSensors = new ArrayList<TypeSensor>();
		TypeSensor type = new TypeSensor();
		
		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM ref_sensors;");

			while (rs.next()) {
				type = mapper.readValue(rs.getObject(1).toString(), TypeSensor.class);
				typeSensors.add(type);
			}
			rs.close();
			stmt.close();


		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		return typeSensors;

	}
	
	public TypeSensor findByType(String type) {
		ObjectMapper mapper = new ObjectMapper();
		TypeSensor typeSensor = new TypeSensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT data FROM ref_sensors where (data ->> 'type')::text = '" + type + "'::text;" );

			while (rs.next()) {
				typeSensor = mapper.readValue(rs.getObject(1).toString(), TypeSensor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		
		return typeSensor;
	}


	
}
