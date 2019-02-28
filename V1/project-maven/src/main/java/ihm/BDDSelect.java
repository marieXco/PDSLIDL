package ihm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import connectionpool.JDBCConnectionPool;

public class BDDSelect {
	ArrayList<String> arr = new ArrayList<String>();
	JDBCConnectionPool jdb = null;
	Connection connection = null;
	Statement stmt = null;

	public BDDSelect(JDBCConnectionPool jdb, Connection connection) {
		super();
		this.jdb = jdb;
		this.connection = connection;
		

		try {			
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM PATIENTS;" );
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()) {          
				for(int i=1;i<=rsmd.getColumnCount();i++)
					arr.add(rs.getString(i));
			}
			
			rs.close();
			stmt.close();

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
