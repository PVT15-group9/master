package com.example.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * En data transfer object klass som hämtar icke-hanterade klasser från events-tabellen.
 * 
 * @author Dmitri
 *
 */
public class EventDTO {
	private int eventId;
	private int userId;
	private int venueId;
	private String eventname;
	private LocalDateTime eventStartTime;
	private LocalDateTime eventEndTime;
	private LocalDateTime eventDoorOpenTime;
	private String url;
	/**
	 * Getters and Setters
	 * @return
	 */
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getVenueId() {
		return venueId;
	}
	public void setVenueId(int venueId) {
		this.venueId = venueId;
	}
	public String getEventname() {
		return eventname;
	}
	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
	public LocalDateTime getEventStartTime() {
		return eventStartTime;
	}
	public void setEventStartTime(Timestamp eventStartTime) {
		this.eventStartTime = eventStartTime.toLocalDateTime();
	}
	public LocalDateTime getEventEndTime() {
		return eventEndTime;
	}
	public void setEventEndTime(Timestamp eventEndTime) {
		this.eventEndTime = eventEndTime.toLocalDateTime();
	}
	public LocalDateTime getEventDoorOpenTime() {
		return eventDoorOpenTime;
	}
	public void setEventDoorOpenTime(Timestamp eventDoorOpenTime) {
		this.eventDoorOpenTime = eventDoorOpenTime.toLocalDateTime();
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "EventDTO [eventId=" + eventId + ", userId=" + userId + ", venueId=" + venueId + ", eventname="
				+ eventname + ", eventStartTime=" + eventStartTime + ", eventEndTime=" + eventEndTime
				+ ", eventDoorOpenTime=" + eventDoorOpenTime + ", url=" + url + "]";
	}
}