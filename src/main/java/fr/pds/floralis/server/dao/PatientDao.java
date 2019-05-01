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

public class PatientDao implements DAO<Patient> {
	
	//create a connection 
	Connection connect = null;
	
	//constructor
	public PatientDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect; 
	}

	@Override
	public boolean create(Patient patientToCreate) {
		JSONObject jsonObject = patientToCreate.toJSON();
		int success = 0;

		PGobject object = new PGobject(); 
		try { 
			object.setValue(jsonObject.toString()); 
		} catch (SQLException e1) { 
			e1.printStackTrace(); 
		}
		object.setType("json"); 

		try { 
			connect.setAutoCommit(false); 
			String sql = "INSERT INTO patients (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql); 
			statement.setObject(1, object);
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


	@Override
	public boolean delete(int id) {
		int success = 0; 
		 
		try { 
			connect.setAutoCommit(false); 
 
			String sql = "DELETE FROM patients where (data -> 'id')::json::text = '"
					+ id + "'::json::text;";
 
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

	@Override
	public boolean update(int id, Patient patientToUpdate) {
		int success = 0; 

		JSONObject jsonObject = patientToUpdate.toJSON();
 
		try {  
			connect.setAutoCommit(false); 
			String sql = "UPDATE patients SET data = '" + jsonObject
					+ "' WHERE (data -> 'id')::json::text = '" + id
					+ "'::json::text;"; 
			
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

	@Override
	public Patient find(int id) {
		ObjectMapper mapper = new ObjectMapper(); 
		List<Integer> emptyList = new ArrayList<Integer>(); 
		emptyList.add(0); 
		emptyList.add(0); 
		Patient patient = new Patient(0, null, null, 0);

		try {  
			connect.setAutoCommit(false); 
			Statement stmt = connect.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT data FROM patients where (data -> 'id')::json::text = '"+ id + "'::json::text;");

			while (rs.next()) {
				patient = mapper.readValue(rs.getObject(1).toString(), Patient.class);
			} 

			rs.close(); 
			stmt.close(); 
 
		} catch (Exception e) { 
			System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
			System.exit(0); 
		} 
  
		return patient;
	}  
 
	@Override
	public List<Patient> findAll() {
		ObjectMapper mapper = new ObjectMapper(); 
		List<Patient> patientList = new ArrayList<Patient>(); 
		Patient patient = new Patient(0, null, null, 0);
 
		try { 
			connect.setAutoCommit(false); 
			Statement stmt = connect.createStatement(); 
 
			ResultSet rs = stmt.executeQuery("SELECT data FROM patients;"); 
 
			while (rs.next()) { 
				patient = mapper.readValue(rs.getObject(1).toString(),Patient.class); 
				patientList.add(patient);
			} 
 
			rs.close(); 
			stmt.close(); 
 
		} catch (Exception e) { 
			System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
			System.exit(0); 
		} 
 
		return patientList;
	} 
	
}
