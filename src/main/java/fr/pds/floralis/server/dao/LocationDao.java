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

public class LocationDao implements DAO<Location> {
	Connection connect = null;

	public LocationDao(Connection connect) throws ClassNotFoundException,
			SQLException {
		this.connect = connect;
	}

	@Override
	public boolean create(Location locationToCreate) {
		JSONObject jsonObject = locationToCreate.toJSON();
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

		if (success > 0) {
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

			String sql = "DELETE FROM location where (data -> 'id')::json::text = '"
					+ id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if (success > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean update(int id, Location locationToUpdate) {
		int success = 0;

		JSONObject jsonObject = locationToUpdate.toJSON();

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE location SET data = '" + jsonObject
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

		if (success > 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Location find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		List<Integer> emptyList = new ArrayList<Integer>();
		emptyList.add(0);
		emptyList.add(0);
		Location location = new Location();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT data FROM location where (data -> 'id')::json::text = '"
							+ id + "'::json::text;");

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

		return location;
	}

	@Override
	public List<Location> findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Location> locationList = new ArrayList<Location>();
		Location location = new Location();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM location;");

			while (rs.next()) {
				location = mapper.readValue(rs.getObject(1).toString(),
						Location.class);
				locationList.add(location);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return locationList;
	}

	

}
