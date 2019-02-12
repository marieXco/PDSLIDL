package ihm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;


public class BDDSelectName {
	ArrayList arr = new ArrayList();
	Connection c = null;
	Statement stmt = null;
	public BDDSelectName() {
		{
			try {
				Class.forName("org.postgresql.Driver");
				c = DriverManager
						.getConnection("jdbc:postgresql://localhost:5432/test",
								"postgres", "root");
				c.setAutoCommit(false);
				System.out.println("Opened database successfully");

				Statement stmt = c.createStatement();
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
				c.close();

			} catch ( Exception e ) {
				System.err.println( e.getClass().getName()+": "+ e.getMessage() );
				System.exit(0);
			}
		}
	}
	public String toString() {
		return "BDD [v=" + arr + "]";
	}

}
