package ihm;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import connectionPool.DataSource;
import connectionPool.JDBCConnectionPool;

public class BDDInsert {
	String firstname;
	String lastname;
	int id;

	public BDDInsert(String firstname, String lastname, int id) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		JDBCConnectionPool jdbc;
		Statement stmt = null;

		try {
			jdbc = new JDBCConnectionPool();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			assertNotNull(connection);
			connection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = connection.createStatement();
			String sql = "INSERT INTO PATIENTS (ID_PATIENTS,FIRSTNAME,LASTNAME) "
					+ "VALUES (?,?,?);";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, firstname);
			statement.setString(3, lastname);

			statement.execute();
			connection.commit();
			connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Records created successfully");

	}

}
