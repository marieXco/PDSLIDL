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

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.*;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;
import fr.pds.floralis.server.dao.*;

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
	public ObjectMapper objectMapper;

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
				ObjectMapper objectMapper = new ObjectMapper();
				
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
						
						JSONArray sensorListToJson = new JSONArray(sensorList);

						toSend = sensorListToJson.toString();
						break;

					case "FINDBYID":
						Sensor sensorFound = sensorDao.find(parameters.getInt("id"));

						toSend = sensorFound.toJSON().toString();
						break;

					case "CREATE":
						Sensor sensorToCreate = objectMapper.readValue(parameters.toString(), Sensor.class);
						Boolean sensorCreation = sensorDao.create(sensorToCreate);

						toSend = sensorCreation.toString();
						break;

					case "DELETE":
						Boolean sensorErasing = sensorDao.delete(parameters.getInt("id"));

						toSend = sensorErasing.toString();
						break;

					case "UPDATE":
						Sensor sensorToUpdate = objectMapper.readValue(parameters.get("sensorToUpdate").toString(), Sensor.class);
						
						Boolean sensorUpdating = sensorDao.update(parameters.getInt("id"), sensorToUpdate);

						toSend = sensorUpdating.toString();
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
						List<Location> locationList = locationDao.findAll();

						JSONArray locationListToJson = new JSONArray(locationList);

						toSend = locationListToJson.toString();
						break;

					case "FINDBYID":
						Location locationFound = locationDao.find(parameters.getInt("id"));

						toSend = locationFound.toJSON().toString();
						break;

					case "CREATE":
						Location locationToCreate = objectMapper.readValue(parameters.toString(), Location.class);
						
						Boolean locationCreation = locationDao.create(locationToCreate);

						toSend = locationCreation.toString();
						break;

					case "DELETE":
						Boolean locationDeleting = locationDao.delete(parameters.getInt("id"));

						toSend = locationDeleting.toString();
						break;

					case "UPDATE":
						Location locationToUpdate= objectMapper.readValue(parameters.get("locationToUpdate").toString(), Location.class);
						
						Boolean locationUpdating = locationDao.update(parameters.getInt("id"), locationToUpdate);

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

						JSONArray roomListToJson = new JSONArray(roomList);

						toSend = roomListToJson.toString();
						break;

					case "FINDBYID":
						Room roomFound = roomDao.find(parameters.getInt("id"));

						toSend = roomFound.toJSON().toString();
						break;

					case "CREATE":
						Room roomToCreate = objectMapper.readValue(parameters.toString(), Room.class);
						
						Boolean roomCreation = roomDao.create(roomToCreate);

						toSend = roomCreation.toString();
						break;

					case "DELETE":
						Boolean roomErasing = roomDao.delete(parameters.getInt("id"));

						toSend = roomErasing.toString();
						break;

					case "UPDATE":
						Room roomToUpdate = objectMapper.readValue(parameters.get("roomToUpdate").toString(), Room.class);
						
						Boolean roomUpdating = roomDao.update(parameters.getInt("id"), roomToUpdate);

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
						
						JSONArray buildingListToJson = new JSONArray(buildingList);

						toSend = buildingListToJson.toString();
						break;

					case "FINDBYID":
						Building buildingFound = buildingDao.find(parameters.getInt("id"));

						toSend = buildingFound.toJSON().toString();
						break;

					case "CREATE":
						Building buildingToCreate = objectMapper.readValue(parameters.toString(), Building.class);
						
						Boolean buildingCreation = buildingDao.create(buildingToCreate);

						toSend = buildingCreation.toString();
						break;

					case "DELETE":
						Boolean buildingErasing = buildingDao.delete(parameters.getInt("id"));

						toSend = buildingErasing.toString();
						break;

					case "UPDATE":
						Building buildingToUpdate = objectMapper.readValue(parameters.get("buildingToUpdate").toString(), Building.class);
						
						Boolean buildingUpdating = buildingDao.update(parameters.getInt("id"), buildingToUpdate);

						toSend = buildingUpdating.toString();
						break;

					default:
						toSend = "Unkwown command for the Buildings table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;

				case "PATIENTS":
					PatientDao patientDao = new PatientDao(connection);
 
					switch (command.toUpperCase()) {
					case "FINDALL":
						List<Patient> patientList = patientDao.findAll();		
						JSONArray patientListToJson = new JSONArray(patientList);
						toSend = patientListToJson.toString();
						break; 

					case "FINDBYID":
						Patient patientFound = patientDao.find(parameters.getInt("id"));
						toSend = patientFound.toJSON().toString();
						break;

					case "CREATE":
						Patient patientToCreate = objectMapper.readValue(parameters.toString(), Patient.class);
						Boolean patientCreate = patientDao.create(patientToCreate);
						toSend = patientCreate.toString();
						break;

					case "DELETE":
						Boolean patientDelete = patientDao.delete(parameters.getInt("id"));
						toSend = patientDelete.toString();
						break;

					case "UPDATE":
						Patient patientToUpdate = objectMapper.readValue(parameters.get("patientToUpdate").toString(), Patient.class);
						Boolean patientUpdate = patientDao.update(parameters.getInt("id"), patientToUpdate);
						toSend = patientUpdate.toString();
						break;

					default:
						toSend = "Unkwown command for the Floors table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;
					
				case "HISTORY_ALERTS":
					AlertDao alertDao = new AlertDao(connection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						List<Alert> alertList = alertDao.findAll();		
						JSONArray alertListToJson = new JSONArray(alertList);
						toSend = alertListToJson.toString();
						break;

					case "FINDBYID":
						Alert alertFound = alertDao.find(parameters.getInt("id"));
						toSend = alertFound.toJSON().toString();
						break;

					case "CREATE":
						Alert alertToCreate = objectMapper.readValue(parameters.toString(), Alert.class);
						Boolean alertCreate = alertDao.create(alertToCreate);
						toSend = alertCreate.toString();
						break;

					case "DELETE":
						Boolean alertDelete = alertDao.delete(parameters.getInt("id"));
						toSend = alertDelete.toString();
						break;

					case "UPDATE":
						Alert alertToUpdate = objectMapper.readValue(parameters.get("alertToUpdate").toString(), Alert.class);
						Boolean alertUpdate = alertDao.update(parameters.getInt("id"), alertToUpdate);
						toSend = alertUpdate.toString();
						break;

					default:
						toSend = "Unkwown command for the Floors table !";
						break;
					}
					DataSource.backConnection(jdbc, connection);
					break;


				case "FLOOR":
					FloorDao floorDao = new FloorDao(connection);

					switch (command.toUpperCase()) {
					case "FINDALL":
						List<Floor> floorList = floorDao.findAll();		
						
						JSONArray floorListToJson = new JSONArray(floorList);

						toSend = floorListToJson.toString();
						break;

					case "FINDBYID":
						Floor floorFound = floorDao.find(parameters.getInt("id"));

						toSend = floorFound.toJSON().toString();
						break;

					case "CREATE":
						Floor floorToCreate = objectMapper.readValue(parameters.toString(), Floor.class);
						
						Boolean floorCreate = floorDao.create(floorToCreate);

						toSend = floorCreate.toString();
						break;

					case "DELETE":
						Boolean floorDelete = floorDao.delete(parameters.getInt("id"));

						toSend = floorDelete.toString();
						break;

					case "UPDATE":
						Floor floorToUpdate = objectMapper.readValue(parameters.get("floorToUpdate").toString(), Floor.class);
						
						Boolean floorUpdate = floorDao.update(parameters.getInt("id"), floorToUpdate);

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