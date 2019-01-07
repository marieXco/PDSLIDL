package connectionPool;

/*
 * Classe DataSource où le clien demande une connexion pour chaque requête
 * Instancie JDBCConnexionPool
 * Methodes statiques pour récupérer une connexion, la rendre et la fermer 
 */
public class DataSource {

	static JDBCConnectionPool jcp = new JDBCConnectionPool(); // pas rester là

	/*
	 * Methode qui alloue au client une connexion à chaque requête
	 */
	static Connection getConnection() {
		Connection connection = jcp.getConnection();
		return connection;
	}

	/*
	 * Methode qui rend la connexion allouée à chaque fin de requête
	 */
	static void backConnection(Connection connection) {
		jcp.backConnection(connection);
	}

	/*
	 * Methode pour fermer la connexion dans le sens la supprimer de la liste
	 * des connexions
	 */
	static void close(Connection connection) {
		connection = null;
	}

}
