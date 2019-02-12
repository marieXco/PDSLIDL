package connectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/*
 * Classe DataSource où le clien demande une connexion pour chaque requête
 * Instancie JDBCConnexionPool
 * Methodes statiques pour récupérer une connexion, la rendre et la fermer 
 */
public class DataSource {

	public static JDBCConnectionPool jcp; 

	/*
	 * Methode qui alloue au client une connexion à chaque requête
	 */
	public static Connection getConnectionFromPool(JDBCConnectionPool jcp) throws ClassNotFoundException, SQLException {
		Connection connection = jcp.addConnection();
		return connection;
	}

	/*
	 * Methode qui rend la connexion allouée à chaque fin de requête
	 */
	public static void backConnection(Connection connection) {
		jcp.backConnection(connection);
	}

	/*
	 * Methode pour fermer la connexion dans le sens la supprimer de la liste
	 * des connexions
	 */
	public static void close(List<Connection> connection) throws SQLException {
		jcp.closeAll(connection); 
	}

}
