package ihm;

import java.sql.Connection;
import java.sql.Statement;
import connectionpool.JDBCConnectionPool;

public class BDDEraseAll {
	JDBCConnectionPool jdb = null;
	Connection connection = null;
	Statement stmt = null;

	public BDDEraseAll(JDBCConnectionPool jdb, Connection connection) {
		super();
		this.jdb = jdb;
		this.connection = connection;
		Statement stmt = null;

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			String sql = "DELETE FROM PATIENTS;";
			stmt.executeUpdate(sql);
			connection.commit();

			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("EraseAll done successfully");
	}
}
