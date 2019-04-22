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

public class FloorDao implements DAO<Floor> {
	Connection connect = null;
	
	public FloorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public boolean create(Floor floorToCreate) {
		JSONObject jsonObject = floorToCreate.toJSON();
		int success = 0;
		
		// FIXME : trying without using PGObject
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

			String sql = "DELETE FROM floors where (data -> 'id')::json::text = '" + id + "'::json::text;";

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
	public boolean update(int id, Floor floorToUpdate) {
		JSONObject jsonObject = floorToUpdate.toJSON();
		int success = 0;

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE floors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + id + "'::json::text;";

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
	public Floor find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		Floor floor = new Floor(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM floors where (data -> 'id')::json::text = '" + id + "'::json::text;" );

			while (rs.next()) {
				floor = mapper.readValue(rs.getObject(1).toString(), Floor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		return floor;
	}

	@Override
	public List<Floor> findAll() {
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
		
		return floors;
	}
	

}
