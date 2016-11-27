package br.com.monitoramento.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.monitoramento.request.NotificationRequest;

public class NotificacaoUtil {
	Logger logger = Logger.getLogger(NotificacaoUtil.class);
	private static final String API_KEY = "key=AIzaSyBckGEahwLNAi3kvsp2SQP-LtBGrJCd-Bk";
	private static final String URL_GOOGLE_FMC = "https://fcm.googleapis.com/fcm/send";
	private static final String DESTINO_ALL_DEVICES = "/topics/allDevices";
	
	public void sendPushNotification(String titulo,String mensagem){
		try {
			NotificationRequest nr = createNotificationRequest(mensagem, titulo);
			Map<String, String> headers = new HashMap<String,String>();
			headers.put("content-type", "application/json; charset=utf-8");
			headers.put("Authorization", API_KEY);
			HttpHandler hh = new HttpHandler();
			String response = hh.makeHttpPostCall(URL_GOOGLE_FMC, headers, nr.asJson());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private NotificationRequest createNotificationRequest(String mensagem,String titulo){
		NotificationRequest nr = new NotificationRequest();
		
		nr.setTo(DESTINO_ALL_DEVICES);
		nr.setText(mensagem);
		nr.setTitle(titulo);
		
		return nr;
	}
}
