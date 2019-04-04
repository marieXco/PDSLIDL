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

import org.json.JSONObject;

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
						JSONObject json = sensorDao.findAll();

						toSend = json.get("sensorsList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject(parameters);

						JSONObject sensorFound = sensorDao.find(jsonId);

						toSend = sensorFound.getString("sensorFound");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject(parameters);

						JSONObject jsonSensorDaoCreate = sensorDao.create(jsonCreate);

						toSend = jsonSensorDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						JSONObject jsonSensorDaoDelete = sensorDao.delete(jsonDeleteId);

						toSend = jsonSensorDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject(parameters);

						JSONObject jsonSensorDaoUpdate = sensorDao.update(jsonUpdate);

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

	public Socket getSock() {
		return sock;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public BufferedInputStream getReader() {
		return reader;
	}

	public void setReader(BufferedInputStream reader) {
		this.reader = reader;
	}
}