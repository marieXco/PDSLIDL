package fr.pds.floralis.gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensors;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class Selects {
	public static List<Patients> SelectPatients(JDBCConnectionPool jdb, Connection connection) throws SQLException {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		List<Patients> patients = null;
		Patients patient = new Patients();
		patients = new ArrayList<Patients>();

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM patients ;" );
			
			while (rs.next()) {
				patient = mapper.readValue(rs.getObject(2).toString(), Patients.class);
				patient.setId(Integer.parseInt(rs.getObject(1).toString()));
				System.out.println(patients.toString());
				patients.add(patient);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("Select success");	
		return patients;
	}
	
	public static List<Sensors> SelectSensors(JDBCConnectionPool jdb, Connection connection) throws SQLException {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		List<Sensors> sensors = null;
		Sensors sensor = new Sensors();
		sensors = new ArrayList<Sensors>();

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM sensors;" );
			
			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(2).toString(), Sensors.class);
				//sensor.setId(Integer.parseInt(rs.getObject(1).toString()));
				System.out.println(sensors.toString());
				sensors.add(sensor);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("Select success");	
		return sensors;
	}


	public static ArrayList SelectPersonnelForConnection(JDBCConnectionPool jdb, Connection connection, String firstValue, String secondValue) throws SQLException {
		JSONObject obj = new JSONObject();
		Statement stmt = null;
		ArrayList toto = new ArrayList();

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			System.out.println("Opened database successfully");

			ResultSet rs = stmt.executeQuery( "SELECT data FROM personnels where data ->> 'username' = '" + firstValue + "' and data ->> 'password' = '" + secondValue + "';" );

			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				toto.add(rs.getString("data"));	
			}
			System.out.println(toto.size());

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("SelectPersonnelForConnection successfully");
		return toto;
	}


	public static List<Patients> SelectPatientWithValues(JDBCConnectionPool jdb, Connection connection, int id) throws SQLException {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		List<Patients> patients = null;
		Patients patient = new Patients();
		patients = new ArrayList<Patients>();

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM patients where id  = '" + id + "';" );

			while (rs.next()) {
				patient = mapper.readValue(rs.getObject(2).toString(), Patients.class);
				patient.setId(Integer.parseInt(rs.getObject(1).toString()));
				System.out.println(patients.toString());
				patients.add(patient);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}

		System.out.println("Select success");
		return patients;
	}
}
