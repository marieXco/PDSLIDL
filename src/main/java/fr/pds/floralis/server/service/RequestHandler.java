package fr.pds.floralis.server.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.OpenDatabase;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;
import fr.pds.floralis.server.dao.SensorDao;

public class RequestHandler implements Runnable {

	// TODO : nettoyer les private inutiles, gérer les getters et setters
	// TODO : cntr + shift + O, F, S
	
	private Socket sock;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	public RequestHandler(Socket pSock) {
		sock = pSock;
	}

	// Le traitement lancé dans un thread séparé
	public void run() {
		System.err.println("Lancement du traitement de la connexion cliente");

		boolean closeConnexion = false;
		// tant que la connexion est active, on traite les demandes

		// TODO : ou mettre le connectionPool et le JDBC
		JDBCConnectionPool jdbc = null;

		try {
			jdbc = OpenDatabase.database();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}

		// On récupère une connection
		Connection connect = null;
		try {
			connect = OpenDatabase.databaseConnection(jdbc);
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}

		while (!sock.isClosed()) {

			try {

				// Ici, nous n'utilisons pas les mêmes objets que précédemment
				// Je vous expliquerai pourquoi ensuite
				writer = new PrintWriter(sock.getOutputStream());
				reader = new BufferedInputStream(sock.getInputStream());
				//TODO : a remonter
				BufferedReader r = new BufferedReader(new InputStreamReader(reader, StandardCharsets.UTF_8));

				String table = r.readLine();
				String command = r.readLine();
				String parameters = r.readLine();

				// TODO : enlever les sysout inutiles
				System.out.println(table + " " + command + " " + parameters);

				InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

				// On affiche quelques infos, pour le débuggage
				String debug = "";
				debug = "Thread : " + Thread.currentThread().getName() + ". ";
				debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
				debug += " Sur le port : " + remote.getPort() + ".\n";
				debug += "\t -> Commande reçue : " + command + " sur la table : " + table + "\n";
				System.err.println("\n" + debug);

				// On traite la demande du client en fonction de la commande envoyée
				String toSend = "";

				switch (table.toUpperCase()) {

				case "SENSOR":
					SensorDao sensorDao = new SensorDao(connect);

					switch (command.toUpperCase()) {
					case "FINDALL":
						List<Sensor> sensorsList = sensorDao.findAll();
						JSONObject json = new JSONObject();
						json.put("sensorsList", sensorsList);

						toSend = json.get("sensorsList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject();
						jsonId.put("id", Integer.parseInt(parameters));

						Sensor sensorFind = new Sensor();
						sensorFind = sensorDao.find(jsonId);

						JSONObject jsonSensorFindById = new JSONObject();

						jsonSensorFindById.put("sensor", sensorFind.toString());
						toSend = jsonSensorFindById.getString("sensor");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject();
						jsonCreate.put("sensor", parameters);
						// TODO: faire comme pour DELETE JSONOBJECT

						Boolean sensorCreate = sensorDao.create(jsonCreate);
						JSONObject jsonSensorDaoCreate = new JSONObject();
						jsonSensorDaoCreate.put("successCreate", sensorCreate);

						toSend = jsonSensorDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						Boolean sensorDelete = sensorDao.delete(jsonDeleteId);
						JSONObject jsonSensorDaoDelete = new JSONObject();
						jsonSensorDaoDelete.put("successDelete", sensorDelete);

						toSend = jsonSensorDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject();
						System.out.println("totto");
						jsonUpdate.put("sensor", parameters);
						System.out.println("tata");
						Boolean sensorUpdate = sensorDao.update(jsonUpdate);
						System.out.println("tatu");
						System.out.println(sensorUpdate);
						System.out.println("tut");
						JSONObject jsonSensorDaoUpdate = new JSONObject();
						jsonSensorDaoUpdate.put("successUpdate", sensorUpdate);

						toSend = jsonSensorDaoUpdate.get("successUpdate").toString();
						break;
					default:
						toSend = "Commande inconnue !";
						break;
					}

					break;
				default:
					toSend = "Commande inconnue !";
					break;
				}

				// On envoie la réponse au client
				writer.write(toSend);
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
}