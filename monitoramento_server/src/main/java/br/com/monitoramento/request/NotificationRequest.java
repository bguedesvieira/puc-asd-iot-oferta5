package br.com.monitoramento.request;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationRequest {
	public NotificationRequest() {
		this.notification = new Notification();
	}
	
	public void setTitle(String title){
		this.notification.setTitle(title);
	}
	
	public void setText(String text){
		this.notification.setText(text);
	}
	
	private class Notification{
		private String title;
		private String text;
		private String sound = "notification_sound";
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}
		/**
		 * @param text the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}
		/**
		 * @return the sound
		 */
		public String getSound() {
			return sound;
		}
		/**
		 * @param sound the sound to set
		 */
		public void setSound(String sound) {
			this.sound = sound;
		}
	}
	private String to;
	private Notification notification;
	
	
	
	public String asJson() throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(this);
		return jsonInString;
	}
	
	public static NotificationRequest asObject(String jsonString) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		NotificationRequest request = mapper.readValue(jsonString, NotificationRequest.class);
		
		return request;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the notification
	 */
	public Notification getNotification() {
		return notification;
	}

	/**
	 * @param notification the notification to set
	 */
	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
