package connectionpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import configuration.Configuration;

/*
 * JDBCConnectionPool class
 * Class where we create a number of connections available and a number of connections that are ready to be used (synchroConnections)
 * Both stocked in an ArrayList of Connections
 * 
 * Contains the methods to add connections to our available connections,
 * to get one connection or to return a used connection from our available connections
 */

public class JDBCConnectionPool {
	private String driver;
	private String url, password, login;
	private int nbrConnexions;
	Connection connection = null;

	public Connection createConnection() throws 
	ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, login,
				password);

		return connection;
	}


	/*
	 * availableConnections : ArrayList that contains opened connections that are not used
	 * synchroConnections : ArrayList that contains the synchronized connections that are used
	 */

	private List<Connection> availableConnections; 
	private List<Connection> synchroConnections;


	public JDBCConnectionPool() throws ClassNotFoundException, SQLException {
		Configuration configuration = new Configuration();
		this.driver = configuration.getCdriver();
		this.url = configuration.getCurl();
		this.password = configuration.getCpassword();
		this.login = configuration.getClogin();
		this.nbrConnexions = Integer
				.parseInt(configuration.getCnbrConnexions());
		

		availableConnections = new ArrayList<Connection>();
		synchroConnections = Collections.synchronizedList(new ArrayList<Connection>());

	}


	/*
	 * Method to create the maximum number of available connections
	 */
	public void putConnection() throws
	SQLException, ClassNotFoundException {
		for (int i = 0; i < nbrConnexions; i++) {
			addConnection();
		}
	}
	

	/*
	 * Method to add a connection to our available connections
	 */
	public void addConnection() throws SQLException, ClassNotFoundException {
		availableConnections.add(createConnection());
		System.out.println("Connection created : " + availableConnections.size());
	}
	

	/*
	 * Method to get a connection from our available connections 
	 * and to put it on our synchronized connections
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		System.out.println("GetConnection");
		int last = 0;
		if (!(availableConnections.isEmpty())) {
			System.out.println("AvailableConnections is not empty, size : " + availableConnections.size());
			for (int i = 0; i < availableConnections.size(); i++) {
				last++;
			}


			System.out.println("AvailableConnections list : " + availableConnections);
			connection = availableConnections.get(last - 1);		
			System.out.println("Connection that we put into synchroConnections : " + connection);	

			synchroConnections.add(connection);
			System.out.println("SynchroConnections list : " + synchroConnections);

			availableConnections.remove(last - 1);
			System.out.println("AvailableConnections after remove : " + availableConnections);
		}
		else {
			System.out.println("No connections are available, please wait");
			throw new RuntimeException("Maximum pool size reached, no available connections!");

		}
		return connection;
	}
	

	/*
	 * Method to give back one connection
	 */
	public void backConnection(Connection connection) {
		System.out.println("BackConnection");
		System.out.println("Connection that we put back : " + connection);
		availableConnections.add(connection);
		System.out.println("AvailableConnections after backConnection : " + availableConnections);
		synchroConnections.remove(connection);
		System.out.println("SynchroConnections after backConnection : " + synchroConnections);
		System.out.println(" ");
	}
	

	/*
	 * Method to close every connection, from both of our lists
	 */
	public void closeAll() throws SQLException {
		int last = 0;
		
		while (!(synchroConnections.isEmpty())) {
			last = synchroConnections.size();
			backConnection(synchroConnections.get(last - 1));
		}
		
		System.out.println("availableConnections is not empty, size : " + availableConnections.size());
		System.out.println("Closing all connections");
		System.out.println("SynchroConnections after closeAll : " + synchroConnections);
		System.out.println("availableConnections after closeAll : " + availableConnections);
	}

}
