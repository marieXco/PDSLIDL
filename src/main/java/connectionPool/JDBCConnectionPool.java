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

	// att configuration dans le cnstructor

	// ajuoter statement et setresult dans l'ihm

	public JDBCConnectionPool() {

		Configuration configuration = new Configuration();
		// TODO : recupérer de xml
		this.driver = configuration.getCdriver();
		this.url = configuration.getCurl();
		this.password = configuration.getCpassword();
		this.login = configuration.getClogin();
		this.nbrConnexions = Integer
				.parseInt(configuration.getCnbrConnexions());
		// this.availableConnections = availableConnections;

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
			SQLException {
		for (int i = 0; i < nbrConnexions; i++) {
			addConnection();
		}
	}

	/*
	 * Methode pour créer n connexions et les ajouter à l'arrayList
	 */
	public Connection addConnection() throws SQLException {
		
		Connection connection = getConnection();
		synchroConnections.add(connection);
		
		return connection;
	}

	/*
	 * Methode pour récupérer une seule connexion dans la liste de connexions
	 * (récupérer = eneleber de la liste)
	 */
	public Connection getConnection() throws SQLException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, password,
				login);
		int last = 0;
		if (!(synchroConnections == null)) {
			while (!(synchroConnections == null)) {
				for (int i = 0; i < synchroConnections.size(); i++) {
					last++;
				}
			}
			connection = synchroConnections.remove(last);
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
