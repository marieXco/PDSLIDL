package ihm;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import connectionPool.DataSource;
import connectionPool.JDBCConnectionPool;

public class BDDSelect {
	ArrayList arr = new ArrayList();
	JDBCConnectionPool jdbc;
	Statement stmt = null;


	{
		try {
			System.out.println("ok");
			jdbc = new JDBCConnectionPool();
			System.out.println("ko");
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			System.out.println("dd");
			assertNotNull(connection);
			System.out.println("dd");
			connection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM PATIENTS;" );
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {          
				for(int i=1;i<=rsmd.getColumnCount();i++)
					arr.add(rs.getString(i));
			}
			for(int i=0; i<arr.size(); i++)
			{
				System.out.println (i+"=>"+arr.get(i));
			}
			rs.close();
			stmt.close();
			connection.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
	}


	@Override
	public String toString() {
		return "BDD [v=" + arr + "]";
	}
}
