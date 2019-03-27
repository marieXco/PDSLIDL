package fr.pds.floralis.server.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.List;

import org.json.JSONObject;

import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;
import fr.pds.floralis.server.dao.SensorDao;

public class RequestHandler implements Runnable {

	private Socket sock;

	private PrintWriter writer = null;

	private BufferedInputStream reader = null;

	private String response;

	private JDBCConnectionPool jdb;
	private Connection connect;

	// en gros quand t'envoie un message à partir de ton client serveur il doit
	// le recevoir dans le client socket
	// dans le run faire les cas de requetes du crud
	// type requete
	// table

	public RequestHandler(Socket pSock) {

		sock = pSock;

	}

	// Le traitement lancé dans un thread séparé

	public void run() {

		System.err.println("Lancement du traitement de la connexion cliente");

		boolean closeConnexion = false;

		// tant que la connexion est active, on traite les demandes

		while (!sock.isClosed()) {

			try {

				// Ici, nous n'utilisons pas les mêmes objets que précédemment

				// Je vous expliquerai pourquoi ensuite

				writer = new PrintWriter(sock.getOutputStream());

				reader = new BufferedInputStream(sock.getInputStream());

				// On attend la demande du client

				response = read();

				InetSocketAddress remote = (InetSocketAddress) sock
						.getRemoteSocketAddress();

				switch (response.toUpperCase()) {

				case "CREATE":
					// vient de recevoir la commande CREATE
					System.out.println();
					writer.write("OK commande CREATE recue");
					writer.flush();
					response = read(); // en attente de la requete
					// il recoit la requete
					// traitement

					switch (response.toUpperCase()) {
					case "SENSOR":
						SensorDao sensorDaoCreate = new SensorDao(connect);
						JSONObject objCreate = new JSONObject(response);
						sensorDaoCreate.create(objCreate);
						break;
					}

					break;

				case "FINDALL":

					// vient de recevoir la commande UPDATE
					System.out.println();
					writer.write("OK commande UPDATE recue");
					writer.flush();
					response = read(); // en attente de la requete
					// il recoit la requete
					// traitement

					switch (response.toUpperCase()) {
					case "SENSOR":
						SensorDao sensorDaoFindAll = new SensorDao(connect);
						List<Sensor> sensorsFound = sensorDaoFindAll.findAll();
						// renvoyer sensorsFound
						JSONObject retours = new JSONObject(sensorsFound);
						writer.write(retours.toString());
						writer.flush();
						break;
					}

					break;

				case "FINDBYID":

					// vient de recevoir la commande UPDATE
					System.out.println();
					writer.write("OK commande UPDATE recue");
					writer.flush();
					response = read(); // en attente de la requete
					// il recoit la requete
					// traitemen

					switch (response.toUpperCase()) {
					case "SENSOR":
						SensorDao sensorDaoFindById = new SensorDao(connect);
						JSONObject objFindById = new JSONObject(response);
						Sensor sensorFound = sensorDaoFindById
								.find(objFindById);
						// renvoyer sensorFound
						JSONObject retour = new JSONObject(sensorFound);
						writer.write(retour.toString());
						writer.flush();
						break;
					}

					break;

				case "UPDATE":
					// vient de recevoir la commande UPDATE
					System.out.println();
					writer.write("OK commande UPDATE recue");
					writer.flush();
					response = read(); // en attente de la requete
					// il recoit la requete
					// traitement
					switch (response.toUpperCase()) {
					case "SENSOR":
						SensorDao sensorDaoUpdate = new SensorDao(connect);
						JSONObject objUpdate = new JSONObject(response);
						sensorDaoUpdate.update(objUpdate);
						break;
					}

					break;

				case "DELETE":
					// vient de recevoir la commande DELETE
					System.out.println();
					writer.write("OK commande DELETE recue");
					writer.flush();
					response = read(); // en attente de la requete
					// il recoit la requete
					// traitement

					switch (response.toUpperCase()) {
					case "SENSOR":
						SensorDao sensorDaoDelete = new SensorDao(connect);
						JSONObject objDelete = new JSONObject(response);
						sensorDaoDelete.delete(objDelete);
						break;
					}

					break;

				default:

					// toSend = "Commande inconnu !";

					break;

				}

				// On envoie la réponse au client

				// writer.write(toSend);

				// Il FAUT IMPERATIVEMENT UTILISER flush()

				// Sinon les données ne seront pas transmises au client

				// et il attendra indéfiniment

				writer.flush();

				if (closeConnexion) {

					System.err.println("COMMANDE CLOSE DETECTEE ! ");

					writer = null;

					reader = null;

					sock.close();

					break;

				}

			} catch (SocketException e) {

				System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");

				break;

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}

	// La méthode que nous utilisons pour lire les réponses

	private String read() throws IOException {

		String response = "";

		int stream;

		byte[] b = new byte[4096];

		stream = reader.read(b);

		response = new String(b, 0, stream);

		return response;

	}

}
