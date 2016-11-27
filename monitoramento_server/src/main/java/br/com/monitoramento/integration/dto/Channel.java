package br.com.monitoramento.integration.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

	private Long id;
	private String name;
	private String description;
	private String latitude;
	private String longitude;
	private String field1;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssX")
	private Date created_at;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssX")
	private Date updated_at;
	private Long last_entry_id;
	
	/**
	 * 
	 * @return The id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 *            The description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return The latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @return The longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 *            The longitude
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * 
	 * @return The field1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * 
	 * @param field1
	 *            The field1
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}

	public Long getLast_entry_id() {
		return last_entry_id;
	}

	public void setLast_entry_id(Long last_entry_id) {
		this.last_entry_id = last_entry_id;
	}


}