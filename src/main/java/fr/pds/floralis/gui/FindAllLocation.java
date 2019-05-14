package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAllLocation  {
	
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllLocation(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Location> findAll() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Location> locationsList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("LOCATION");
		request.setFields(new JSONObject());
		
		ConnectionClient ccLocationfindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccLocationfindAll.run();
		
		Location[] locationsFoundTab =  objectMapper.readValue(ccLocationfindAll.getResponse(), Location[].class);
		locationsList = Arrays.asList(locationsFoundTab);
		
		return locationsList;
	}

}
