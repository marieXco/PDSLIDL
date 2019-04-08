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

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;
import fr.pds.floralis.server.dao.BuildingDao;
import fr.pds.floralis.server.dao.FloorDao;
import fr.pds.floralis.server.dao.LocationDao;
import fr.pds.floralis.server.dao.RoomDao;
import fr.pds.floralis.server.dao.SensorDao;

public class RequestHandler implements Runnable {

	// TODO : quand est-ce qu'on close la socket ?

	private Socket sock;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	private BufferedReader readerLine =  null;
	private JDBCConnectionPool jdbc;
	

	TimeServer ts = new TimeServer();
	
	DataSource ds = new DataSource();
	
	

	public RequestHandler(Socket pSock) {
		sock = pSock;
	}

	// Le traitement lancé dans un thread séparé
	public void run() {
		System.err.println("Lancement du traitement de la connexion cliente");
		
		
		try {
			setJdbc(DataSource.createPool());
		} catch (ClassNotFoundException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean closeConnexion = false;
		// tant que la connexion est active, on traite les demandes

		while (!sock.isClosed()) {

			try {

				// Ici, nous n'utilisons pas les mêmes objets que précédemment
				// Je vous expliquerai pourquoi ensuite
				writer = new PrintWriter(sock.getOutputStream());
				reader = new BufferedInputStream(sock.getInputStream());
				readerLine = new BufferedReader(new InputStreamReader(reader, StandardCharsets.UTF_8));

				String table = readerLine.readLine();
				String command = readerLine.readLine();
				String parameters = readerLine.readLine();
				String close = readerLine.readLine();

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
					Connection sensorConnection = DataSource.getConnectionFromPool(getJdbc());	
					SensorDao sensorDao = new SensorDao(sensorConnection);

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

				case "LOCATION":
					Connection locationConnection = DataSource.getConnectionFromPool(getJdbc());	
					LocationDao locationDao = new LocationDao(locationConnection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						JSONObject json = locationDao.findAll();

						toSend = json.get("locationList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject(parameters);

						JSONObject locationFound = locationDao.find(jsonId);

						toSend = locationFound.getString("locationFound");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject(parameters);

						JSONObject jsonLocationDaoCreate = locationDao.create(jsonCreate);

						toSend = jsonLocationDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						JSONObject jsonLocationDaoDelete = locationDao.delete(jsonDeleteId);

						toSend = jsonLocationDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject(parameters);

						JSONObject jsonLocationDaoUpdate = locationDao.update(jsonUpdate);

						toSend = jsonLocationDaoUpdate.get("successUpdate").toString();
						break;

					default:
						toSend = "Commande inconnue !";
						break;
					}

					break;
					
				case "ROOM":
					Connection roomConnection = DataSource.getConnectionFromPool(getJdbc());	
					RoomDao roomDao = new RoomDao(roomConnection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						JSONObject json = roomDao.findAll();
						System.out.println(json.toString());

						toSend = json.get("roomsList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject(parameters);

						JSONObject roomFound = roomDao.find(jsonId);

						toSend = roomFound.getString("roomFound");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject(parameters);

						JSONObject jsonRoomDaoCreate = roomDao.create(jsonCreate);

						toSend = jsonRoomDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						JSONObject jsonRoomDaoDelete = roomDao.delete(jsonDeleteId);

						toSend = jsonRoomDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject(parameters);

						JSONObject jsonRoomDaoUpdate = roomDao.update(jsonUpdate);

						toSend = jsonRoomDaoUpdate.get("successUpdate").toString();
						break;
						
					default:
						toSend = "Commande inconnue !";
						break;
					}
					break;
					
				case "BUILDING":
					Connection buildingConnection = DataSource.getConnectionFromPool(getJdbc());	
					BuildingDao buildingDao = new BuildingDao(buildingConnection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						JSONObject json = buildingDao.findAll();

						toSend = json.get("buildingList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject(parameters);

						JSONObject buildingFound = buildingDao.find(jsonId);

						toSend = buildingFound.getString("buildingFound");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject(parameters);

						JSONObject jsonBuildingDaoCreate = buildingDao.create(jsonCreate);

						toSend = jsonBuildingDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						JSONObject jsonBuildingDaoDelete = buildingDao.delete(jsonDeleteId);

						toSend = jsonBuildingDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject(parameters);

						JSONObject jsonBuildingDaoUpdate = buildingDao.update(jsonUpdate);

						toSend = jsonBuildingDaoUpdate.get("successUpdate").toString();
						break;
						
					default:
						toSend = "Commande inconnue !";
						break;
					}
					break;
					
				case "FLOOR":
					Connection floorConnection = DataSource.getConnectionFromPool(getJdbc());	
					FloorDao floorDao = new FloorDao(floorConnection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						JSONObject json = floorDao.findAll();

						toSend = json.get("floorList").toString();
						break;

					case "FINDBYID":
						JSONObject jsonId = new JSONObject(parameters);

						JSONObject floorFound = floorDao.find(jsonId);

						toSend = floorFound.getString("floorFound");
						break;

					case "CREATE":
						JSONObject jsonCreate = new JSONObject(parameters);

						JSONObject floorCreate = floorDao.create(jsonCreate);

						toSend = floorCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonDeleteId = new JSONObject(parameters);

						JSONObject floorDelete = floorDao.delete(jsonDeleteId);

						toSend = floorDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonUpdate = new JSONObject(parameters);

						JSONObject floorUpdate = floorDao.update(jsonUpdate);

						toSend = floorUpdate.get("successUpdate").toString();
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

				switch (close.toUpperCase()) {
				case "CLOSE" :
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

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
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
	
	public JDBCConnectionPool getJdbc() {
		return jdbc;
	}

	public void setJdbc(JDBCConnectionPool jdbc) {
		this.jdbc = jdbc;
	}
}