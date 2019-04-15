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

public class findAllLocation  {
	
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public findAllLocation(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Location> findAll(Boolean refresh) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Location> locationsList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("LOCATION");
		request.setFields(new JSONObject());
		
		ConnectionClient ccLocationfindAll = new ConnectionClient(host, port, request.toString());
		ccLocationfindAll.run();
		
		String retoursCcLocationFindAll = ccLocationfindAll.getResponse();
		JSONObject locationsFound = new JSONObject();
		locationsFound.put("locationsFound", retoursCcLocationFindAll);
		System.out.println(locationsFound.get("sensorsFound"));
		
		Location[] sensorsFoundTab =  objectMapper.readValue(locationsFound.get("sensorsFound").toString(), Location[].class);
		locationsList = Arrays.asList(sensorsFoundTab);
		
		if(refresh) {
			Thread.sleep(6000);
			findAll(true);
		}
		
		return locationsList;
	}

}
