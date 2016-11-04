package br.iotpuc.consumo.util;

import android.util.Log;

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

/**
 * Created by Bernardo on 02/11/2016.
 */

public class HttpHandler {

        private static final String TAG = HttpHandler.class.getSimpleName();

        public HttpHandler() {
        }

    public String makeHttpPostCall(final String reqUrl, final Map<String,String> headers, final String body) throws IOException{
        URL url = new URL(reqUrl);
        Log.e(TAG, "vai enviar dados via POST para url: " + reqUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
            Log.e(TAG,"adicionou header : "+entry.getKey()+" - "+entry.getValue());
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

        wr.write(body);
        wr.flush();

        int httpResult = conn.getResponseCode();
        String response = null;
        if (httpResult == HttpURLConnection.HTTP_OK) {
            response = convertStreamToString(conn.getInputStream());
        } else {
            response = conn.getResponseMessage();
        }
        Log.e(TAG,"Resposta do servidor: "+response);
        return response;
    }

        public String makeServiceCall(String reqUrl) {
            String response = null;
            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-type", "application/json; charset=utf-8");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        private String convertStreamToString(final InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
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
