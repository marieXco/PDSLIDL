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

import fr.pds.floralis.commons.bean.entity.Building;

public class BuildingDao implements DAO<Building> {
	Connection connect = null;

	public BuildingDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public boolean create(Building buildingToCreate) {
		JSONObject jsonObject = buildingToCreate.toJSON();
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

			String sql = "INSERT INTO buildings (data) VALUES (?);";

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
		
		// Works like create
		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM buildings where (data -> 'id')::json::text = '" + id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.executeUpdate();
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
	public boolean update(int id, Building buildingToCreate) {
		JSONObject jsonObject = buildingToCreate.toJSON();
		int success = 0;

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE buildings SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.executeUpdate();
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
	public Building find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		Building building = new Building(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM buildings where (data -> 'id')::json::text = '" + id + "'::json::text;" );

			while (rs.next()) {
				building = mapper.readValue(rs.getObject(1).toString(), Building.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}		
		return building;
	}

	@Override
	public List<Building> findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Building> buidings = new ArrayList<Building>();
		Building buiding = new Building(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM buildings;");


			while (rs.next()) {
				buiding = mapper.readValue(rs.getObject(1).toString(), Building.class);
				buidings.add(buiding);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	

		return buidings;
	}


}
