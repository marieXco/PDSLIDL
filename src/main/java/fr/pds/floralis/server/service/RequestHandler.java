package fr.pds.floralis.server.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
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
				JSONObject parameters = requestJson.getJSONObject("requested-parameters");

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

						toSend = json.get("sensorList").toString();
						break;

					case "FINDBYID":
						JSONObject sensorFound = sensorDao.find(parameters);

						toSend = sensorFound.getString("sensorFound");
						break;

					case "CREATE":
						JSONObject jsonSensorDaoCreate = sensorDao.create(parameters);

						toSend = jsonSensorDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonSensorDaoDelete = sensorDao.delete(parameters);

						toSend = jsonSensorDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonSensorDaoUpdate = sensorDao.update(parameters);

						toSend = jsonSensorDaoUpdate.get("successUpdate").toString();
						break;
						
					case "FINDBYCONFIG":
						JSONObject sensorFoundByConfig = sensorDao.find(parameters);

						toSend = sensorFoundByConfig.get("sensorFoundByConfig").toString();
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
						JSONObject locationFound = locationDao.find(parameters);

						toSend = locationFound.get("locationFound").toString();
						break;

					case "CREATE":
						JSONObject jsonLocationDaoCreate = locationDao.create(parameters);

						toSend = jsonLocationDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonLocationDaoDelete = locationDao.delete(parameters);

						toSend = jsonLocationDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonLocationDaoUpdate = locationDao.update(parameters);

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

						toSend = json.get("roomList").toString();
						break;

					case "FINDBYID":
						JSONObject roomFound = roomDao.find(parameters);

						toSend = roomFound.getString("roomFound");
						break;

					case "CREATE":
						JSONObject jsonRoomDaoCreate = roomDao.create(parameters);

						toSend = jsonRoomDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonRoomDaoDelete = roomDao.delete(parameters);

						toSend = jsonRoomDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonRoomDaoUpdate = roomDao.update(parameters);

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
						JSONObject buildingFound = buildingDao.find(parameters);

						toSend = buildingFound.getString("buildingFound");
						break;

					case "CREATE":
						JSONObject jsonBuildingDaoCreate = buildingDao.create(parameters);

						toSend = jsonBuildingDaoCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject jsonBuildingDaoDelete = buildingDao.delete(parameters);

						toSend = jsonBuildingDaoDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject jsonBuildingDaoUpdate = buildingDao.update(parameters);

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
						JSONObject floorFound = floorDao.find(parameters);

						toSend = floorFound.getString("floorFound");
						break;

					case "CREATE":
						JSONObject floorCreate = floorDao.create(parameters);

						toSend = floorCreate.get("successCreate").toString();
						break;

					case "DELETE":
						JSONObject floorDelete = floorDao.delete(parameters);

						toSend = floorDelete.get("successDelete").toString();
						break;

					case "UPDATE":
						JSONObject floorUpdate = floorDao.update(parameters);

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