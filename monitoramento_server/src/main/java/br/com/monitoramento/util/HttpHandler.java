package br.com.monitoramento.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.http.HttpMethod;


public class HttpHandler {
	Logger logger = Logger.getLogger(HttpHandler.class);

    public HttpHandler() {
    }
    
    public String makeHttpPostCall(final String reqUrl,final Map<String,String> headers,final String body) throws IOException{
    	URL url = new URL(reqUrl);
    	
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(HttpMethod.POST.toString());

		for (Entry<String, String> entry : headers.entrySet()) {
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		
		conn.setDoOutput(true);
		conn.setDoInput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		
		wr.write(body);
		wr.flush();
		
		int HttpResult = conn.getResponseCode(); 
		String response = null;
		if (HttpResult == HttpURLConnection.HTTP_OK) {
			response = convertStreamToString(conn.getInputStream());
		} else {
		    response = conn.getResponseMessage();  
		}
		
    	return response;
    }
    
    public String makeHttpGetCall(final String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-type", "application/json; charset=utf-8");
            System.out.println(conn.getResponseMessage());
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
        	System.out.println("ProtocolException: " + e.getMessage());
        } catch (IOException e) {
        	System.out.println( "IOException: " + e.getMessage());
        } catch (Exception e) {
        	System.out.println("Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
//    	BufferedReader in = new BufferedReader(
//    			   new InputStreamReader(
//    	                      new FileInputStream(fileDir), "UTF8"));
    	
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
