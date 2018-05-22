package com.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
            List<Object[]> events = repo.findAllEvent();
            List<Event> temp_events = new ArrayList<>();
            List<Event> newEvents = new ArrayList<>();
            /**
             * hämtar datan och omvandlar till Ett event objekt
             */
            for (Object[] dates : events) {
                LocalDateTime startTime = (LocalDateTime) dates[0];
                LocalDateTime endTime = (LocalDateTime) dates[1];
                LocalDateTime doorTime = (LocalDateTime) dates[2];
                Event newEvent = new Event(startTime, endTime, doorTime);
                temp_events.add(newEvent);
            }
            /**
             * kontrollerar om det finns en sensor med den starttiden. om den
             * inte finns så sparas den i listan för nya events
             */
            for (Event event : temp_events) {
                if (repo.findByStartTime(event.getEST()).isEmpty()) {
                    newEvents.add(event);
                }
            }
            /**
             * om listan är ej tom så skapas en ny sensor och sparas i
             * databasen.
             */
            if (!newEvents.isEmpty()) {
                for (Event newEvent : newEvents) {
                    Sensor newSensor = new Sensor(LocalDateTime.now(), newEvent.getEST(), newEvent.getEET(), newEvent.getDOT(), 0);
                    if (newSensor.isRegisterTime(newSensor.getSensorCreationTime(), newSensor.getDoorOpenTime())) {
                        newSensor.setValues();
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
     * varje sensorns listan. görs varje minut.
     *
     * @throws Exception
     */
    @Scheduled(fixedRate = 60000)
    public void updateValue() throws Exception {
        try {
            List<Sensor> sensors = repo.findAllSensor();
            for (Sensor sensor : sensors) {
                if (sensor.getValues().size() == 0) {
                    repo.deleteSensor(sensor);
                }
            }
            for (Sensor sensor : sensors) {
                int newValue = sensor.getValues().get(0).getSimulatedValue();
                sensor.setSensorValue(newValue);
                repo.updateSensor(sensor);
            }
            for (Sensor s : sensors) {
                repo.deleteSimulatedValue(s.getValues().get(0).getId());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * En Event klass för kunna hämta events från databasen då uppbygnaden är
     * lite annorlunda. events tabellen hårdkodas och sensor tabellen skapas
     * dynamiskt.
     *
     * @author Dmitri
     *
     */
    private class Event {

        LocalDateTime eventStartTime;
        LocalDateTime eventEndTime;
        LocalDateTime doorOpenTIme;

        private Event(LocalDateTime eventStartTime, LocalDateTime eventEndTime, LocalDateTime doorOpenTIme) {
            this.eventStartTime = eventStartTime;
            this.eventEndTime = eventEndTime;
            this.doorOpenTIme = doorOpenTIme;
        }

        private LocalDateTime getEST() {
            return eventStartTime;
        }

        private LocalDateTime getEET() {
            return eventEndTime;
        }

        private LocalDateTime getDOT() {
            return doorOpenTIme;
        }
    }
}
