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

import fr.pds.floralis.commons.bean.entity.Location;

public class LocationDao extends DAO<Location> {
	Connection connect = null;
	
	public LocationDao(Connection connect) throws ClassNotFoundException, SQLException {
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

			String sql = "INSERT INTO location (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			success = statement.executeUpdate();
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject locationCreated = new JSONObject();
		
		if (success > 0) {
			locationCreated.put("successCreate", "true");
		} else {
			locationCreated.put("successCreate", "false");
		}
		
		
		return locationCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int locationId = jsonObject.getInt("id"); 

		
		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM location where (data -> 'id')::json::text = '"
					+ locationId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject locationDeleted = new JSONObject();
		
		if (success > 0) {
			locationDeleted.put("successDelete", "true");
		} else {
			locationDeleted.put("successDelete", "false");
		}
	
		return locationDeleted;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;

		int locationId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE location SET data = '" + jsonObject
					+ "' WHERE (data -> 'id')::json::text = '" + locationId
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

		JSONObject locationUpdated = new JSONObject();
		
		if (success > 0) {
			locationUpdated.put("successUpdate", "true");
		} else {
			locationUpdated.put("successUpdate", "false");
		}
		
		return locationUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		List<Integer> emptyList = new ArrayList<Integer>();
		emptyList.add(0);
		emptyList.add(0);
		Location location = new Location(0, emptyList, null, null, null);


		int locationId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT data FROM location where (data -> 'id')::json::text = '"
							+ locationId + "'::json::text;");

			while (rs.next()) {
				location = mapper.readValue(rs.getObject(1).toString(),
						Location.class);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject locationFound = new JSONObject();
		locationFound.put("locationFound", location.toString());
		return locationFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Location> allLocation = new ArrayList<Location>();
		Location location = new Location();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM location;");

			while (rs.next()) {
				location = mapper.readValue(rs.getObject(1).toString(),
						Location.class);
				allLocation.add(location);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject locationList = new JSONObject();
		locationList.put("locationList", allLocation.toString());

		return locationList;
	}

}
