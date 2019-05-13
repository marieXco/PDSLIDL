package fr.pds.floralis.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAlertByYear {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAlertByYear(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@SuppressWarnings("deprecation")
	public static int findByYear(Boolean refresh, Date date) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
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
		
		int alertAuGogoles = 0; 
		for (Alert alert :alertList) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
			String dateString = sdf.format(date);
	        String dateAlert = sdf.format(alert.getDate());
	        System.out.println("La date qui rentre en parametre de la session" +date);
	        System.out.println("Alerte" + alert);
	        // System.out.println(alertAuGogoles++);
	        Integer test = Integer.parseInt(dateString)- 1900 ;
	        
	        // System.out.println("Date String " +  );
	        // System.out.println("Date alerte " + dateAlert);
	        
			if(dateAlert.equals(test.toString())) {
				alertAuGogoles++; 
			}
			
		}
		
		if(refresh) { 
			Thread.sleep(6000);
			findByYear(true, date);
		}
		
		return alertAuGogoles;
	}
	

}
