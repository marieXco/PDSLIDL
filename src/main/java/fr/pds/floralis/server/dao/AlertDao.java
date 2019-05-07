package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.type.TypeLevel;

public class AlertDao implements DAO<Alert> {
	
	//create a connection 
		Connection connect = null;
		
		//constructor
		public AlertDao(Connection connect) throws ClassNotFoundException, SQLException {
			this.connect = connect;  
		}

	@Override
	public boolean create(Alert alertToCreate) {
		JSONObject jsonObject = alertToCreate.toJSON();
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
			String sql = "INSERT INTO history_alerts (data) VALUES (?);";
 
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
 
			String sql = "DELETE FROM hystory_alerts where (data -> 'id')::json::text = '"+ id + "'::json::text;";
 
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
	public boolean update(int id, Alert alertToUpdate) {
		int success = 0; 

		JSONObject jsonObject = alertToUpdate.toJSON();
 
		try {  
			connect.setAutoCommit(false); 
			String sql = "UPDATE history_alerts SET data = '" + jsonObject+ "' WHERE (data -> 'id')::json::text = '" + id + "'::json::text;"; 
			
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
	public Alert find(int id) {
		ObjectMapper mapper = new ObjectMapper(); 
		List<Integer> emptyList = new ArrayList<Integer>(); 
		emptyList.add(0); 
		emptyList.add(0); 
		Alert alert = new Alert();

		try {  
			connect.setAutoCommit(false); 
			Statement stmt = connect.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT data FROM history_alerts where (data -> 'id')::json::text = '"+ id + "'::json::text;");
			
			while (rs.next()) {
				alert = mapper.readValue(rs.getObject(1).toString(), Alert.class);
			} 

			rs.close(); 
			stmt.close(); 
 
		} catch (Exception e) { 
			System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
			System.exit(0); 
		} 
  
		return alert;

	}

	@Override
	public List<Alert> findAll() {
		ObjectMapper mapper = new ObjectMapper(); 
		List<Alert> alertList = new ArrayList<Alert>(); 
		Alert alert = new Alert();
 
		try { 
			connect.setAutoCommit(false); 
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM history_alerts;");
 
			while (rs.next()) { 
				alert = mapper.readValue(rs.getObject(1).toString(), Alert.class);
				alertList.add(alert);
			} 
 
			rs.close(); 
			stmt.close(); 
 
		} catch (Exception e) { 
			System.err.println(e.getClass().getName() + ": " + e.getMessage()); 
			System.exit(0); 
		} 
 
		return alertList;
	}
	
	
	
}
