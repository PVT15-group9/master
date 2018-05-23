package com.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.dto.EventDTO;
import com.example.model.Sensor;

@Component
public class SensorController {

    @Autowired
    private SensorDAO repo;

    /**
     * Skapar nya sensorer och sparar dem i databasen var 27: e minut.
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0/27 * 1/1 * ?")
    public void updateSensor() throws Exception {
        try {
			List<EventDTO> db_events = repo.findAllEvent();
			List<EventDTO> newEvents = new ArrayList<>();
			
			for(EventDTO event : db_events) {
				if(repo.findByStartTime(event.getEventStartTime()).isEmpty())
					newEvents.add(event);
			}
			if(!newEvents.isEmpty()) {
				for(EventDTO newEvent : newEvents) {
					Sensor newSensor = new Sensor(newEvent.getVenueId(), LocalDateTime.now(), newEvent.getEventStartTime(), newEvent.getEventEndTime(), newEvent.getEventDoorOpenTime(), 0);
					if(newSensor.isRegisterTime(newSensor.getSensorCreationTime(), newSensor.getDoorOpenTime())) {
						List<Integer> routeIndex = repo.findRouteByVenueId(newSensor.getEventVenueId());
						newSensor.setValues();
						newSensor.setRoutes(routeIndex);
						repo.insertSensor(newSensor);
					}
				}
			}
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Tar bort sensorer som har tomma listor, uppdaterar värden i listor och
     * sparar ett nytt värde för varje sensor, tar bort det gamla värdet från
     * varje sensorns listan, uppdaterar amount-värde i thresholds-tabellen. görs varje minut.
     *
     * @throws Exception
     */
    @Scheduled(fixedRate = 60000, initialDelay=1500)
    public void updateValue() throws Exception {
        try {
			List<Sensor> sensors = repo.findAllSensor();
			List<Integer> routes = new ArrayList<>();
			
			for(Sensor sensor : sensors) {
				if(sensor.getValues().size() == 0) {
					repo.deleteSensor(sensor);
				}
			}
			for(Sensor sensor : sensors) {
				int newValue = sensor.getValues().get(0).getSimulatedValue();
				sensor.setSensorValue(newValue);
				repo.updateSensor(sensor);
			}
			for(Sensor sensor : sensors) {
				repo.deleteSimulatedValue(sensor.getValues().get(0).getId());
			}
			for(Sensor sensor: sensors) {
				routes = sensor.getRoutes();
				int value = sensor.getSensorValue();
				for(int routeId : routes) {
					List<Integer> tresholdsId = repo.findAllThresholdbyRouteId(routeId);
					for(int tresholdId : tresholdsId) {
						double factor = repo.findXFactorByRouteId(routeId);
						int amount = (int) (value * factor);
						repo.updateTresholdAmount(ThreadLocalRandom.current().nextInt(amount, amount+30), tresholdId);
					}
				}
			}
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
