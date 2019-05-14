package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAllRoom {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllRoom(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Room> findAll() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Room> roomList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("ROOM");
		request.setFields(new JSONObject());
		
		ConnectionClient ccRoomFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccRoomFindAll.run();
		
		Room[] roomFoundTab =  objectMapper.readValue(ccRoomFindAll.getResponse(), Room[].class);
		roomList = Arrays.asList(roomFoundTab);
		
		return roomList;
	}

}

