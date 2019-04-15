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

import fr.pds.floralis.commons.bean.entity.Floor;

public class FloorDao extends DAO<Floor> {
	Connection connect = null;
	
	public FloorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public JSONObject create(JSONObject jsonObject) {
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

			String sql = "INSERT INTO floors (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			success = statement.executeUpdate(); 
			
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject floorCreated = new JSONObject();
		
		if(success > 0) {  
			floorCreated.put("successCreate", "true");
		} else {
			floorCreated.put("successCreate", "false");
		}
		
		return floorCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		
		int success = 0;
		int floorId = jsonObject.getInt("id"); 

		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM floors where (data -> 'id')::json::text = '" + floorId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject floorDeleted = new JSONObject();
		
		if(success > 0) {
			floorDeleted.put("successDelete", "true");
		} else {
			floorDeleted.put("successDelete", "false");
		}

		return floorDeleted;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		
		int floorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE floors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + floorId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();


			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject floorUpdated = new JSONObject();

		if(success > 0) {
			floorUpdated.put("successUpdate", "true");
		} else {
			floorUpdated.put("successUpdate", "false");
		}

		return floorUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Floor floor = new Floor(0, null);

		int floorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM floors where (data -> 'id')::json::text = '" + floorId + "'::json::text;" );

			while (rs.next()) {
				floor = mapper.readValue(rs.getObject(1).toString(), Floor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject floorFound = new JSONObject();
		floorFound.put("floorFound", floor.toString());
		return floorFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Floor> floors = new ArrayList<Floor>();
		Floor floor = new Floor(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM floors;");


			while (rs.next()) {
				floor = mapper.readValue(rs.getObject(1).toString(), Floor.class);
				floors.add(floor);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	


		JSONObject floorList = new JSONObject();
		floorList.put("floorList", floors.toString());
		
		return floorList;
	}
	

}
