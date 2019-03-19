package fr.pds.floralis.gui;

/** 
 * JSON Insert
 * This class is used for every insert
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PGobject;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class Delete {
	Statement stmt = null;

	public Delete(JDBCConnectionPool jdb, Connection connection, String databaseName, int id) throws SQLException {
		try {
			connection.setAutoCommit(false);
			
			stmt = connection.createStatement();
			String sql = "DELETE FROM " + databaseName + " WHERE id = (?);";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);

			statement.execute();
			connection.commit();

			statement.close();
			//JSONRequest.JSONRequestPersonnels();	
		} 
			catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}

		System.out.println("Delete success");
	}

}
