package fr.pds.floralis.commons.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.Sensors;

public class SensorDao extends DAO<Sensor> {

	public SensorDao(Connection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean create(Sensor obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Sensor obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Sensor obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Sensor find(int id) {
		Statement stmt = null;
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery( "SELECT id, data FROM sensors;" );
			
			while (rs.next()) {
				sensor = mapper.readValue(rs.getObject(2).toString(), Sensor.class);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("find success");
		
		return sensor;
	}

}
