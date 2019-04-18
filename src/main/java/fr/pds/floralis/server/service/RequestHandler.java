package fr.pds.floralis.server.service;

import java.util.List;
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

import fr.pds.floralis.commons.bean.entity.Building;
import fr.pds.floralis.commons.bean.entity.Floor;
import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.commons.bean.entity.Sensor;
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
						List<Sensor> sensorList = sensorDao.findAll();
						
						JSONArray sensorListToJson = new JSONArray();
						sensorListToJson.put(sensorList);

						toSend = sensorListToJson.toString();
						break;

					case "FINDBYID":
						Sensor sensorFound = sensorDao.find(parameters.getInt("id"));

						toSend = sensorFound.toJSON().toString();
						break;

					case "CREATE":
						// FIXME PLEASE, ToString but to Sensor, using objectMapper
						Boolean sensorCreation = sensorDao.create(new Sensor());

						toSend = sensorCreation.toString();
						break;

					case "DELETE":
						Boolean sensorErasing = sensorDao.delete(parameters.getInt("id"));

						toSend = sensorErasing.toString();
						break;

					case "UPDATE":
						// FIXME PLEASE, ToString but to Sensor, using objectMapper
						Boolean sensorUpdating = sensorDao.update(parameters.getInt("id"), new Sensor());

						toSend = sensorUpdating.toString();
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
						List<Location> locationList = locationDao.findAll();

						JSONArray locationListToJson = new JSONArray();
						locationListToJson.put(locationList);

						toSend = locationListToJson.toString();
						break;

					case "FINDBYID":
						Location locationFound = locationDao.find(parameters.getInt("id"));

						toSend = locationFound.toJSON().toString();
						break;

					case "CREATE":
						Boolean locationCreation = locationDao.create(new Location());

						toSend = locationCreation.toString();
						break;

					case "DELETE":
						Boolean locationDeleting = locationDao.delete(parameters.getInt("id"));

						toSend = locationDeleting.toString();
						break;

					case "UPDATE":
						Boolean locationUpdating = locationDao.update(parameters.getInt("id"), new Location());

						toSend = locationUpdating.toString();
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
						List<Room> roomList = roomDao.findAll();

						JSONArray roomListToJson = new JSONArray();
						roomListToJson.put(roomList);

						toSend = roomListToJson.toString();
						break;

					case "FINDBYID":
						Room roomFound = roomDao.find(parameters.getInt("id"));

						toSend = roomFound.toJSON().toString();
						break;

					case "CREATE":
						Boolean roomCreation = roomDao.create(new Room());

						toSend = roomCreation.toString();
						break;

					case "DELETE":
						Boolean roomErasing = roomDao.delete(parameters.getInt("id"));

						toSend = roomErasing.toString();
						break;

					case "UPDATE":
						Boolean roomUpdating = roomDao.update(parameters.getInt("id"), new Room());

						toSend = roomUpdating.toString();
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
						List<Building> buildingList = buildingDao.findAll();
						
						JSONArray buildingListToJson = new JSONArray();
						buildingListToJson.put(buildingList);

						toSend = buildingListToJson.toString();
						break;

					case "FINDBYID":
						//TODO : a modifier
						Building buildingFound = buildingDao.find(parameters.getInt("id"));

						toSend = buildingFound.toJSON().toString();
						break;

					case "CREATE":
						Boolean buildingCreation = buildingDao.create(new Building());

						toSend = buildingCreation.toString();
						break;

					case "DELETE":
						Boolean buildingErasing = buildingDao.delete(parameters.getInt("id"));

						toSend = buildingErasing.toString();
						break;

					case "UPDATE":
						Boolean buildingUpdating = buildingDao.update(parameters.getInt("id"), new Building());

						toSend = buildingUpdating.toString();
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
						List<Floor> floorList = floorDao.findAll();		
						JSONArray floorListToJson = new JSONArray();
						floorListToJson.put(floorList);

						toSend = floorListToJson.toString();
						break;

					case "FINDBYID":
						Floor floorFound = floorDao.find(parameters.getInt("id"));

						toSend = floorFound.toJSON().toString();
						break;

					case "CREATE":
						Boolean floorCreate = floorDao.create(new Floor());

						toSend = floorCreate.toString();
						break;

					case "DELETE":
						Boolean floorDelete = floorDao.delete(parameters.getInt("id"));

						toSend = floorDelete.toString();
						break;

					case "UPDATE":
						Boolean floorUpdate = floorDao.update(parameters.getInt("id"), new Floor());

						toSend = floorUpdate.toString();
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