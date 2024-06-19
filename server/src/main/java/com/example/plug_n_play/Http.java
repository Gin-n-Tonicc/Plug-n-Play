package com.example.plug_n_play;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
	public static String get(String urlAddress) throws Exception {
		
		URL url = new URL(urlAddress);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(
				  new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();

		return content.toString();
	}
	
	
	
	public static void post(String urlAddress, String json) throws Exception {		
		URL url = new URL(urlAddress);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization", "Bearer CBWBo6JKFCmHL1jYzjbs9ybf");	
		con.connect();
		
		OutputStream os = null;
		try {
			os = con.getOutputStream();
		    byte[] input = json.getBytes("utf-8");
		    os.write(input, 0, input.length);	
		    os.flush();
		} finally {
			if (os != null)
				os.close();
		}

		System.out.println(con.getResponseCode());
		
		
		try(BufferedReader br = new BufferedReader(
				  new InputStreamReader(con.getInputStream(), "utf-8"))) {
				    StringBuilder response = new StringBuilder();
				    String responseLine = null;
				    while ((responseLine = br.readLine()) != null) {
				        response.append(responseLine.trim());
				    }
				    System.out.println(response.toString());
				}		
	}	

}
