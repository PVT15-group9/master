package com.example.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
/**
 * En register klass som registrerar ett simulerad värde för varje route med extra information.
 * 
 * @author Dmitri
 *
 */
@Entity
@DynamicUpdate
@Table(name="route_value_register")
public class RouteValueRegister {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="register_id")
	private long registerId;
	
	@Column(name="route_id")
	private long routeId;
	
	@Column(name="sensor_id")
	private long sensorId;
	
	@Column(name="sensor_value")
	private int sensorValue;
	
	@Column(name="time_stamp")
	private LocalDateTime timeStamp;
	
	public RouteValueRegister(){}
	
	public RouteValueRegister(long routeId, long sensorId, int sensorValue, LocalDateTime timeStamp) {
		this.routeId = routeId;
		this.sensorId = sensorId;
		this.sensorValue = sensorValue;
		this.timeStamp = timeStamp;
	}

	public long getSensorRegisterId() {
		return registerId;
	}

	public void setSensorRegisterId(long registerId) {
		this.registerId = registerId;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public int getSensorValue() {
		return sensorValue;
	}

	public void setSensorValue(int sensorValue) {
		this.sensorValue = sensorValue;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "RouteValueRegister [registerId=" + registerId + ", routeId=" + routeId + ", sensorId=" + sensorId
				+ ", sensorValue=" + sensorValue + ", timeStamp=" + timeStamp + "]";
	}
}
