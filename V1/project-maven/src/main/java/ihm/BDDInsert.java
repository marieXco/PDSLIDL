package ihm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import connectionpool.JDBCConnectionPool;

public class BDDInsert {
	String firstname;
	String lastname;
	JDBCConnectionPool jdb = null;
	Connection connection = null;
	Statement stmt = null;

	public BDDInsert(JDBCConnectionPool jdb, Connection connection, String firstname, String lastname) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.jdb = jdb;
		this.connection = connection;
		
		try {
			connection.setAutoCommit(false);
			String sql = "INSERT INTO PATIENTS (FIRSTNAME,LASTNAME) "
					+ "VALUES (?,?);";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, firstname);
			statement.setString(2, lastname);
			
			statement.execute();
			connection.commit();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		System.out.println("Insert created successfully");
	}
}
