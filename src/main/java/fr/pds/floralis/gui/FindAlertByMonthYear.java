package fr.pds.floralis.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

public class FindAlertByMonthYear {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAlertByMonthYear(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	
	public static int findByMonthYear(Date date) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
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
		int alertCount = 0;
		for (Alert alert :alertList) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMM");
			String dateString = sdf.format(date);
	        String dateAlert = sdf.format(alert.getDate());
	        System.out.println("EN STRING CA DONNE CA " + dateString);
	        Integer test = Integer.parseInt(dateString)- 190001 ;
	        System.out.println( "LE RESULTAT DU  TEST" + test);
	        
			if(dateAlert.equals(test.toString())) {
				alertCount++; 
			}
			
		}
		
		return alertCount;
	}
	

}

