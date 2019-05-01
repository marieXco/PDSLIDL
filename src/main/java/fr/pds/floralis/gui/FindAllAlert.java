package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAllAlert {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllAlert(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Alert> findAll(Boolean refresh) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Alert> alertList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("HISTORY_ALERTS");
		request.setFields(new JSONObject());
		
		ConnectionClient ccAlertFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccAlertFindAll.run();
		
		Alert[] alertFoundTab =  objectMapper.readValue(ccAlertFindAll.getResponse(), Alert[].class);
		alertList = Arrays.asList(alertFoundTab);
		
		if(refresh) { 
			Thread.sleep(6000);
			findAll(true);
		}
		
		return alertList;
	}
}
