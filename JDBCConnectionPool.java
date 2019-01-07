package connectionPool;

import java.util.ArrayList;

/*
 * Classe JDBCConnectionPool
 * classe où on créer un certain nombre de connexions chaque matin
 * Stockées dans une ArrayList de Connection
 * Instancie Connection
 * Methodes pour les crées, pour donner une connexion, la rendre, les supprimer
 */
public class JDBCConnectionPool {

	/*
	 * Instanciation de la liste qui va regrouper n connexions
	 */
	ArrayList<Connection> connections = new ArrayList<Connection>();
	/*
	 * Instanciation du nombre de connexions estimées //TODO : modifier la
	 * valeur
	 */
	int nbrConnexions = 10;

	/*
	 * Methode pour créer n connexions et les ajouter à l'arrayList
	 */
	public void putConnection(int nbrConnexions) {
		for (int i = 0; i < nbrConnexions; i++) {
			addConnection();
		}
	}

	/*
	 * Methode pour créer n connexions et les ajouter à l'arrayList
	 */
	public void addConnection() {
		Connection connection = new Connection();
		connections.add(connection);
	}

	/*
	 * Methode pour récupérer une seule connexion dans la liste de connexions
	 * (récupérer = eneleber de la liste)
	 */
	public Connection getConnection() {
		Connection connection = null;
		int last = 0;
		if (!(connections == null)) {
			while (!(connections == null)) {
				for (int i = 0; i < connections.size(); i++) {
					last++;
				}
			}
			connection = connections.remove(last);
		}
		return connection;
	}

	/*
	 * Methode pour rendre une connexion à la liste (rendre = remettre dedans)
	 */
	public void backConnection(Connection connection) {
		connections.add(connection);

	}

	/*
	 * Methode pour fermer toutes les connexions (=vider la liste)
	 */
	public void closeAll() {
		connections.removeAll(connections);
	}

}
