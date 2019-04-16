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


import fr.pds.floralis.commons.bean.entity.Patient;

public class PatientDao extends DAO<Patient> {
	
	//create a connection 
	Connection connect = null;
	
	//constructor
	public PatientDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect; 
	}

	@Override
	// for insert 
	public JSONObject create(JSONObject jsonObject) {
		int success = 0; 
		
		PGobject object = new PGobject();
		try {
			object.setValue(jsonObject.toString());
		}catch(SQLException e1){
			e1.printStackTrace();
		}
		object.setType("json");
		
		try {
			connect.setAutoCommit(false);
			
			String sql = "INSERT INTO patients (data) VALUES (?) ; ";
			
			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			success = statement.executeUpdate();
			connect.commit();
			statement.close();
			
		} catch(Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject patientCreated = new JSONObject();
		
		if (success > 0) {
			patientCreated.put("successCreate", "true");
		} else {
			patientCreated.put("successCreate", "false");
		}
			
		return patientCreated; 
	}

	@Override
	//to delete
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int patientId = jsonObject.getInt("id"); 
		
		
		try{ 
			connect.setAutoCommit(false);
		
			String sql = "DELETE FROM patients where (data -> 'id')::json::text = '"
						+ patientId + "'::json::text;";
	
			PreparedStatement statement = connect.prepareStatement(sql);
	
			success = statement.executeUpdate();
			connect.commit();
			statement.close();
	
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject patientDeleted = new JSONObject();
		
		if (success > 0) {
			patientDeleted.put("successDelete", "true");
		} else {
			patientDeleted.put("successDelete", "false");
		}
	
		return patientDeleted;
	}

	@Override
	// to update
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		int patientId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE patients SET data = '" + jsonObject
					+ "' WHERE (data -> 'id')::json::text = '" + patientId
					+ "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);
			
			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if (success > 0) {
			System.out.println("update success");
		}

		JSONObject patientUpdated = new JSONObject();
		
		if (success > 0) {
			patientUpdated.put("successUpdate", "true");
		} else {
			patientUpdated.put("successUpdate", "false");
		}
		
		return patientUpdated;
	}


	@Override
	//to select few lines
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Patient patient = new Patient(0, null, null, 0);

		int patientId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT data FROM patients where (data -> 'id')::json::text = '"
							+ patientId + "'::json::text;");

			while (rs.next()) {
				patient = mapper.readValue(rs.getObject(1).toString(),Patient.class);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject patientFound = new JSONObject();
		patientFound.put("patientFound", patient.toString());
		return patientFound;
	}

	@Override
	//to select all lines
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Patient> allPatient = new ArrayList<Patient>();
		Patient patient = new Patient(0, null, null, 0);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM location;");

			while (rs.next()) {
				patient = mapper.readValue(rs.getObject(1).toString(),Patient.class);
				allPatient.add(patient);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject patientList = new JSONObject();
		patientList.put("locationList", allPatient.toString());

		return patientList;
	}
	
	
}
