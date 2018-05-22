package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author Dmitri
 *
 * Representerar ett simulerad värde som produceras fram av en sensor.
 *
 */
@Entity
@DynamicUpdate
@Table(name = "simulated_value")
public class SimulatedValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "simulated_value")
    private int simulatedValue;
    /**
     * Binds med en sensor. En sensor kan ha flera värde.
     */
    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    public SimulatedValue() {
    }

    public SimulatedValue(int simulatedValue, Sensor sensor) {
        this.simulatedValue = simulatedValue;
        this.sensor = sensor;
    }

    /**
     * Getters och Setters.
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSimulatedValue() {
        return simulatedValue;
    }

    public void setSimulatedValue(int simulatedValue) {
        this.simulatedValue = simulatedValue;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * auto simulerad hashCode metod
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
        result = prime * result + simulatedValue;
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
        SimulatedValue other = (SimulatedValue) obj;
        if (id != other.id) {
            return false;
        }
        if (sensor == null) {
            if (other.sensor != null) {
                return false;
            }
        } else if (!sensor.equals(other.sensor)) {
            return false;
        }
        if (simulatedValue != other.simulatedValue) {
            return false;
        }
        return true;
    }

    /**
     * auto simulerad toString-metod
     */
    @Override
    public String toString() {
        return "SimulatedValue [id=" + id + ", simulatedValue=" + simulatedValue + ", sensorID=" + sensor.getId() + "]";
    }
}
