package fr.pds.floralis.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class Update {
	//TODO garder stmt ?
		public static void UpdateData(JDBCConnectionPool jdb, Connection connection, String databaseName, int id, PGobject jsonObject) throws SQLException {
			//Statement stmt = null; to keep ? 

			try {
				connection.setAutoCommit(false);
				//stmt = c.createStatement(); to keep ? 

				String sql =  "UPDATE " + databaseName  + " set data = (?) where id  = '" + id + "';";

				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setObject(1, jsonObject);

				statement.execute();
				connection.commit();
				statement.close();

			} catch (Exception e) {
				System.err.println( e.getClass().getName()+": "+ e.getMessage() );
				System.exit(0);
			}

			System.out.println("Update success");
		}
}
