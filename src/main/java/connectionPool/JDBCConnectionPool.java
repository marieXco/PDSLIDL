package connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import configuration.Configuration;

/*
 * Classe JDBCConnectionPool
 * classe où on créer un certain nombre de connexions chaque matin
 * Stockées dans une ArrayList de Connection
 * Instancie Connection
 * Methodes pour les crées, pour donner une connexion, la rendre, les supprimer
 */

public class JDBCConnectionPool {

	private String driver;
	private String url, password, login;
	private int nbrConnexions;

	/*
	 * Instanciation de la liste qui va regrouper n connexions d'object java
	 * Connection
	 */
	private List<Connection> availableConnections;
	private List<Connection> synchroConnections;
	

	

	public JDBCConnectionPool() {
		System.out.println("ent");


		Configuration configuration = new Configuration();
		System.out.println("ent");

		
		this.driver = configuration.getCdriver();
		this.url = configuration.getCurl();
		this.password = configuration.getCpassword();
		this.login = configuration.getClogin();
		System.out.println("int");
		this.nbrConnexions = Integer
				.parseInt(configuration.getCnbrConnexions());
		System.out.println("int");

		

		availableConnections = new ArrayList<Connection>();
		synchroConnections = Collections.synchronizedList(availableConnections);

	}

	/*
	 * Instanciation du nombre de connexions estimées //TODO : modifier la
	 * valeur
	 */

	/*
	 * Methode pour créer n connexions et les ajouter à l'arrayList
	 */
	public void putConnection(int nbrConnexions) throws
			SQLException, ClassNotFoundException {
	
		for (int i = 0; i < nbrConnexions; i++) {
			addConnection();
		}
	}

	/*
	 * Methode pour créer n connexions et les ajouter à l'arrayList
	 */
	public Connection addConnection() throws SQLException, ClassNotFoundException {
		
		

		Connection connection = getConnection();
		
		return connection;
	}

	/*
	 * Methode pour récupérer une seule connexion dans la liste de connexions
	 * (récupérer = eneleber de la liste)
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, login,
				password);
		synchroConnections.add(connection);
		int last = 0;
		if ((synchroConnections != null)) {

				for (int i = 0; i < synchroConnections.size(); i++) {
					last++;
				}
		
			connection = synchroConnections.get(0);
		}
		if (synchroConnections == null){
			System.out.println("KO");
		}
		return connection;
	}

	/*
	 * Methode pour rendre une connexion à la liste (rendre = remettre dedans)
	 */
	public void backConnection(Connection connection) {

		
		synchroConnections.add(connection);

	}

	/*
	 * Methode pour fermer une connexions
	 */
	public void closeAll(List<Connection> synchroConnections)
			throws SQLException {
		for (int i = 0; i < synchroConnections.size(); i++) {
			Connection connection = ((List<Connection>) synchroConnections)
					.get(i);
			if (!(connection.isClosed())) {
				connection.close();
			}

		}

	}

}
