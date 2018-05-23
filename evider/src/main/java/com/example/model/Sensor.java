package com.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import java.time.*;
import java.time.LocalDateTime;

/**
 *
 * @author Dmitri
 *
 * En sensor som kopplas till ett event och har en livscykel som beräknas med
 * hjälp av insläppstiden för eventet och sluttiden. Med hjälp av livscykeln
 * skapas simulerade värdena. Antalet bestäms av uppdateringsfrekvensen. Jag
 * delade upp processen i fem steg för att slumpa fram värdena. De fem stegen
 * används för att följa ett mönster av flödet som kan återspegla verkligheten.
 * I verkligheten så ökas flödet av åskådarna som tar sig fram till eventet
 * proportionellt med tiden som återstår tills eventet börjar. Flödet minskas
 * rejält när eventet har startats och börjar öka igen när eventet närmar sig
 * slutet. Sensorns starttiden bestäms genom att vrida insläppstiden 30 minuter
 * tillbaka. Sensorns sluttiden bestäms genom att vrida sluttiden för eventet 90
 * minuter fram. Den första processen slumpar fram värden mellan 0-10. Den
 * används för att möjliggöra att skapa nya sensorer automatisk av API:et.
 * Tidsintervallet för denna processen är sensorns starttid minus 29 minuter och
 * fram till sensorns starttid. Den andra processen delas i 3 delar.
 * Tidsintervallet för denna processen är sensorns starttid och fram till
 * starttiden för eventet. Första delen producerar låga värden, andra delen
 * producerar medelvärden och tredje höga värden. Den tredje processen
 * producerar låga värden och tidsintervallet för denna processen är starttiden
 * för eventet och fram till sluttiden för eventet minus 30 minuter. Den fjärde
 * processen delas i två delar. Tidsintervallet för denna processen är sluttiden
 * för eventet minus 30 minuter och fram till sluttiden för eventet plus 30
 * minuter. Den första delen producerar låga värden och den andra producerar
 * höga värden. Den femte processen delas i två delar och tidsintervallet för
 * denna processen är sluttiden för eventet plus 30 minuter och fram till
 * sensorns sluttid. Den första delen producerar medelvärden och den andra
 * producerar låga värden. Slutligen skapas en List som innehåller alla värden.
 * API:et hämtar ett värde i taget och sparar värdet till sensorValue attribut
 * och tar bort det värdet från listan. När listan blir tom så tas sensorns
 * bort.
 *
 */
@Entity
@DynamicUpdate
@Table(name = "sensor")
public class Sensor {

    /**
     * Det maximala talet för den första processen.
     */
    private static final int MAX_PRESTART_VALUE = 10;
    /**
     * Det minimala talet för ett lågt värde typ.
     */
    private static final int MIN_LOW_VALUE = 0;
    /**
     * Det maximala talet för ett lågt värde typ.
     */
    private static final int MAX_LOW_VALUE = 99;
    /**
     * Det minimala talet för ett medel värde typ.
     */
    private static final int MIN_AVERAGE_VALUE = 100;
    /**
     * Det maximala talet för ett medel värde typ.
     */
    private static final int MAX_AVERAGE_VALUE = 399;
    /**
     * Det minimala talet för ett högt värde typ.
     */
    private static final int MIN_HIGH_VALUE = 400;
    /**
     * Det maximala talet för ett högt värde typ.
     */
    private static final int MAX_HIGH_VALUE = 1000;
    /**
     * Uppdateringsfrekvensen i sekunder
     */
    private static final int UPDATE_FREQUENCY = 60;
    /**
     * hur många minuter som ska vridas tillbaka för att bestämma sensorns
     * starttid
     */
    private static final int SENSOR_START_VALUE = 30;
    /**
     * hur många minuter som ska vridas framåt för att bestämma sensorns
     * sluttid
     */
    private static final int SENSOR_END_VALUE = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    private long id;

    @Column(name = "creation_time")
    private LocalDateTime sensorCreationTime;

    @Column(name = "event_start_time")
    private LocalDateTime eventStartTime;

    @Column(name = "event_end_time")
    private LocalDateTime eventEndTime;

    @Column(name = "door_open_time")
    private LocalDateTime doorOpenTime;

    @Column(name = "sensor_value")
    private int sensorValue;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulatedValue> values = new ArrayList<>();

    public Sensor() {
    }

    public Sensor(LocalDateTime sensorCreationTime, LocalDateTime eventStartTime, LocalDateTime eventEndTime, LocalDateTime doorOpenTime, int sensorValue) {
        this.sensorCreationTime = sensorCreationTime;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.doorOpenTime = doorOpenTime;
        this.sensorValue = sensorValue;
    }

    /**
     * Getters and Setters
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getSensorCreationTime() {
        return sensorCreationTime;
    }

    public void setSensorCreationTime(LocalDateTime sensorCreationTime) {
        this.sensorCreationTime = sensorCreationTime;
    }

    public int getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(int sensorValue) {
        this.sensorValue = sensorValue;
    }

    public List<SimulatedValue> getValues() {
        return values;
    }

    public void setValues(List<SimulatedValue> values) {
        this.values = values;
    }

    public LocalDateTime getEventStartTime() {
        return eventStartTime;
    }

    public LocalDateTime getEventEndTime() {
        return eventEndTime;
    }

    public LocalDateTime getDoorOpenTime() {
        return doorOpenTime;
    }

    public void setEventStartTime(LocalDateTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public void setEventEndTime(LocalDateTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public void setDoorOpenTime(LocalDateTime doorOpenTime) {
        this.doorOpenTime = doorOpenTime;
    }

    /**
     * Kontrollerar om tiden för när en sensor skapades är negativ mot sensorns
     * starttid.
     */
    public boolean isDateTimeNegative() {
        return Duration.between(sensorCreationTime, doorOpenTime.minusMinutes(SENSOR_START_VALUE)).isNegative();
    }

    /**
     * Om det är dags att registrera en sensor. Tar in parametrar från ett event
     *
     * @param timeNow
     * @param doorOpenTime
     * @return
     */
    public boolean isRegisterTime(LocalDateTime timeNow, LocalDateTime doorOpenTime) {
        if (timeNow.isBefore(doorOpenTime.plusMinutes(30)) && timeNow.isAfter(doorOpenTime.minusMinutes(30))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Bestämmer starttiden för en sensor
     *
     * @return
     */
    public LocalDateTime getSensorStartTime() {
        if (isDateTimeNegative()) {
            long minutes = Duration.between(sensorCreationTime, doorOpenTime).toMinutes();
            return doorOpenTime.minusMinutes(minutes);
        } else {
            return doorOpenTime.minusMinutes(SENSOR_START_VALUE);
        }
    }

    /**
     * Bestämmer sluttiden för en sensor
     *
     * @return
     */
    public LocalDateTime getSensorEndTime() {
        return eventEndTime.plusMinutes(SENSOR_END_VALUE);
    }

    /**
     * Slumpar fram ett värde för första processen
     *
     * @return
     */
    private SimulatedValue getPreStartValue() {
        return new SimulatedValue(ThreadLocalRandom.current().nextInt(MIN_LOW_VALUE, MAX_PRESTART_VALUE), this);
    }

    /**
     * Slumpar fram ett lågt värde typ
     *
     * @return
     */
    private SimulatedValue getLowValue() {
        return new SimulatedValue(ThreadLocalRandom.current().nextInt(MIN_LOW_VALUE, MAX_LOW_VALUE), this);
    }

    /**
     * Slumpar fram ett medel värde typ
     *
     * @return
     */
    private SimulatedValue getAverageValue() {
        return new SimulatedValue(ThreadLocalRandom.current().nextInt(MIN_AVERAGE_VALUE, MAX_AVERAGE_VALUE), this);
    }

    /**
     * Slumpar fram ett högt värde typ
     *
     * @return
     */
    private SimulatedValue getHighValue() {
        return new SimulatedValue(ThreadLocalRandom.current().nextInt(MIN_HIGH_VALUE, MAX_HIGH_VALUE), this);
    }

    /**
     * Bestämmer längden på första processen
     *
     * @return
     */
    private Duration getPreStartTimeDuration() {
        if (!isDateTimeNegative()) {
            return Duration.between(sensorCreationTime, getSensorStartTime());
        } else {
            return Duration.ofMinutes(0);
        }
    }

    /**
     * Bestämmer längden på andra processen
     *
     * @return
     */
    private Duration getFirstCycleDuration() {
        return Duration.between(getSensorStartTime(), getEventStartTime());
    }

    /**
     * Bestämmer längden på tredje processen
     *
     * @return
     */
    private Duration getSecondCycleDuration() {
        return Duration.between(getEventStartTime(), getEventEndTime().minusMinutes(30));
    }

    /**
     * Bestämmer längden på fjärde processen
     *
     * @return
     */
    private Duration getThirdCycleDuration() {
        return Duration.between(getEventEndTime().minusMinutes(30), getEventEndTime().plusMinutes(30));
    }

    /**
     * Bestämmer längden på femte processen
     *
     * @return
     */
    private Duration getFourthCycleDuration() {
        return Duration.between(getEventEndTime().plusMinutes(30), getSensorEndTime());
    }

    /**
     * Bestämmer den totala längden på varje process i sekunder där varje tid
     * för processen delas med talet för uppdateringsfrekvensen
     *
     * @param cycleDuration
     * @return
     */
    private int getTotalCycle(Duration cycleDuration) {
        return (int) (cycleDuration.getSeconds() / UPDATE_FREQUENCY);
    }

    /**
     * Bestämmer längden för den första delen av den andra processen
     *
     * @return
     */
    private int getLowFirstCycle() {
        return (getTotalCycle(getFirstCycleDuration()) / 3);
    }

    /**
     * Bestämmer när det är dags att producera ett medel värde för den andra
     * processen
     *
     * @return
     */
    private int getAverageFirstCycle() {
        return (getLowFirstCycle() * 2);
    }

    /**
     * Bestämmer längden för den första delen av den fjärde processen
     *
     * @return
     */
    private int getLowThirdCycle() {
        return (getTotalCycle(getThirdCycleDuration()) / 2);
    }

    /**
     * Bestämmer längden för den första delen av den femte processen
     *
     * @return
     */
    private int getAverageFourthCycle() {
        return (getTotalCycle(getFourthCycleDuration()) / 2);
    }

    /**
     * Simulerar alla värde för den första processen osh sparar dem i listan
     */
    private void setPreCycle() {
        if (!isDateTimeNegative()) {
            for (int i = 0; i < getTotalCycle(getPreStartTimeDuration()); i++) {
                values.add(getPreStartValue());
            }
        }
    }

    /**
     * Simulerar alla värde för den andra processen osh sparar dem i listan
     */
    private void setFirstCycle() {
        for (int i = 0; i < getTotalCycle(getFirstCycleDuration()); i++) {
            if (i < getLowFirstCycle()) {
                values.add(getLowValue());
            } else if (i >= getLowFirstCycle() && i < getAverageFirstCycle()) {
                values.add(getAverageValue());
            } else {
                values.add(getHighValue());
            }
        }
    }

    /**
     * Simulerar alla värde för den tredje processen osh sparar dem i listan
     */
    private void setSecondCycle() {
        for (int i = 0; i < getTotalCycle(getSecondCycleDuration()); i++) {
            values.add(getLowValue());
        }
    }

    /**
     * Simulerar alla värde för den fjärde processen osh sparar dem i listan
     */
    private void setThirdCycle() {
        for (int i = 0; i < getTotalCycle(getThirdCycleDuration()); i++) {
            if (i < getLowThirdCycle()) {
                values.add(getLowValue());
            } else {
                values.add(getHighValue());
            }
        }
    }

    /**
     * Simulerar alla värde för den femte processen osh sparar dem i listan
     */
    private void setFourthCycle() {
        for (int i = 0; i < getTotalCycle(getFourthCycleDuration()); i++) {
            if (i < getAverageFourthCycle()) {
                values.add(getAverageValue());
            } else {
                values.add(getLowValue());
            }
        }
    }

    /**
     * Simulerar alla värde för alla processerna osh sparar dem i listan
     */
    public void setValues() {
        setPreCycle();
        setFirstCycle();
        setSecondCycle();
        setThirdCycle();
        setFourthCycle();
    }

    /**
     * auto simulerad toString-metod
     */
    @Override
    public String toString() {
        return "Sensor [id=" + id + ", sensorCreationTime=" + sensorCreationTime + ", eventStartTime=" + eventStartTime
                + ", eventEndTime=" + eventEndTime + ", doorOpenTime=" + doorOpenTime + ", sensorValue=" + sensorValue
                + ", values size=" + values.size() + "]";
    }

    /**
     * auto simulerad hashCode metod
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((doorOpenTime == null) ? 0 : doorOpenTime.hashCode());
        result = prime * result + ((eventEndTime == null) ? 0 : eventEndTime.hashCode());
        result = prime * result + ((eventStartTime == null) ? 0 : eventStartTime.hashCode());
        result = prime * result + ((sensorCreationTime == null) ? 0 : sensorCreationTime.hashCode());
        return result;
    }

    /**
     * auto simulerad equals metod
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Sensor other = (Sensor) obj;
        if (doorOpenTime == null) {
            if (other.doorOpenTime != null) {
                return false;
            }
        } else if (!doorOpenTime.equals(other.doorOpenTime)) {
            return false;
        }
        if (eventEndTime == null) {
            if (other.eventEndTime != null) {
                return false;
            }
        } else if (!eventEndTime.equals(other.eventEndTime)) {
            return false;
        }
        if (eventStartTime == null) {
            if (other.eventStartTime != null) {
                return false;
            }
        } else if (!eventStartTime.equals(other.eventStartTime)) {
            return false;
        }
        if (sensorCreationTime == null) {
            if (other.sensorCreationTime != null) {
                return false;
            }
        } else if (!sensorCreationTime.equals(other.sensorCreationTime)) {
            return false;
        }
        return true;
    }
}
