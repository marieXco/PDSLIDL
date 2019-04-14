package fr.pds.floralis.server.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;
import fr.pds.floralis.server.dao.BuildingDao;
import fr.pds.floralis.server.dao.FloorDao;
import fr.pds.floralis.server.dao.LocationDao;
import fr.pds.floralis.server.dao.RoomDao;
import fr.pds.floralis.server.dao.SensorDao;

/**
 * RequestHandler 
 * This class is made to read the client request and to answer it
 * @author alveslaura
 *
 */

public class RequestHandler implements Runnable {

	private Socket sock;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;
	private Connection connection;
	private JDBCConnectionPool jdbc;

	/**
	 * Constructor
	 * @param pSock
	 * @param jdbc
	 * @param connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public RequestHandler(Socket pSock, JDBCConnectionPool jdbc, Connection connection) throws ClassNotFoundException, SQLException {
		this.sock = pSock;
		this.jdbc = jdbc;
		this.connection = connection;
	}

	/**
	 * run()
	 * Class that does the request traitment, called on a thread
	 */
	public void run() {
		System.err.println("Lancement du traitement de la connexion cliente");

		while (!sock.isClosed()) {
			try {
				writer = new PrintWriter(sock.getOutputStream());
				reader = new BufferedInputStream(sock.getInputStream());

				String request = read();
				JSONObject requestJson = new JSONObject(request);
				
				/**
				 * See the Request class on the entity
				 */
				String table = requestJson.getString("requested-view-entity");
				String command = requestJson.getString("type");
				JSONArray parameters = requestJson.getJSONArray("requested-values");

				InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

				/**
				 * Debug infos
				 * remote contains the port and the ip address that made the request
				 */
				String debug = "";
				debug = "Address asking : " + remote.getAddress().getHostAddress() + ".";
				debug += " On port : " + remote.getPort() + ".\n";
				debug += "\t -> Command : " + command + " on table : " + table + "\n";
				debug += "\t with parameters : " + parameters + "\n";
				System.err.println("\n" + debug);

				String toSend = "";

				switch (table.toUpperCase()) {
				case "SENSOR":
					SensorDao sensorDao = new SensorDao(connection);
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
						toSend = "Unkwown command for the Sensors table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				case "LOCATION":
					LocationDao locationDao = new LocationDao(connection);

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
						toSend = "Unkwown command for the Locations table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				case "ROOM":
					RoomDao roomDao = new RoomDao(connection);

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
						toSend = "Unkwown command for the Rooms table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				case "BUILDING":
					BuildingDao buildingDao = new BuildingDao(connection);

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
						toSend = "Unkwown command for the Buildings table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				case "FLOOR":
					FloorDao floorDao = new FloorDao(connection);

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
						toSend = "Unkwown command for the Floors table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				default:
					toSend = "Unkwown table !";
					break;
				}

				/**
				 * We write the result and send it to the ConnectionClient with .flush
				 */
				writer.write(toSend);
				writer.flush();

				System.err.println("Closing the RequestHandler !");
				sock.close();

			} catch (SocketException e) {
				System.err.println("Interrupted connection");
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

	private String read() throws IOException{      
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream + 1);      
		return response;
	}   
}