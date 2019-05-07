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

import fr.pds.floralis.commons.bean.entity.Breakdown;
import fr.pds.floralis.commons.bean.entity.Location;

public class BreakdownDao implements DAO<Breakdown> {
	Connection connect = null;
	
	public BreakdownDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public boolean create(Breakdown breakdownToCreate) {
		JSONObject jsonObject = breakdownToCreate.toJSON();
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

			String sql = "INSERT INTO history_breakdown (data) VALUES (?);";

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

			String sql = "DELETE FROM history_breakdow where (data -> 'id')::json::text = '"
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
	public boolean update(int id, Breakdown breakdownToUpdate) {
		int success = 0;

		JSONObject jsonObject = breakdownToUpdate.toJSON();

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE history_breakdown SET data = '" + jsonObject
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
	public Breakdown find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		List<Integer> emptyList = new ArrayList<Integer>();
		emptyList.add(0);
		emptyList.add(0);
		Breakdown breakdown = new Breakdown(0, null,0, null, null );

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT data FROM history_breakdown where (data -> 'id')::json::text = '"
							+ id + "'::json::text;");

			while (rs.next()) {
				breakdown = mapper.readValue(rs.getObject(1).toString(),
						Breakdown.class);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return breakdown;
	}

	@Override
	public List<Breakdown> findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Breakdown> breakdownList = new ArrayList<Breakdown>();
		Breakdown breakdown = new Breakdown(0, null,0, null, null );

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM history_breakdown;");

			while (rs.next()) {
				breakdown = mapper.readValue(rs.getObject(1).toString(),
						Breakdown.class);
				breakdownList.add(breakdown);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return breakdownList;
	}

}
