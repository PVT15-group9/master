package com.example;

import java.time.LocalDateTime;
import java.util.List;
import com.example.model.Sensor;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LocalDateTimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Innehåller metoder för att hämta, uppdatera, spara, ta bort data från
 * databasen.
 *
 * @author Dmitri
 *
 */
@Repository
@Transactional
public class SensorDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Hämtar alla sensorer.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Sensor> findAllSensor() {
        return getSession().createQuery("FROM Sensor").list();
    }

    /**
     * Hämtar alla sensor med det angivna id:et.
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Sensor> findById(long id) {
        return getSession().createQuery("SELECT s FROM Sensor s WHERE s.id = :id").setParameter("id", id).list();
    }

    /**
     * Hämtar sensorer med den angivna starttiden.
     *
     * @param startTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Sensor> findByStartTime(LocalDateTime startTime) {
        return getSession().createQuery("SELECT s FROM Sensor s WHERE s.eventStartTime = :startTime").setParameter("startTime", startTime).list();
    }

    /**
     * Sparar en ny sensor i databasen.
     *
     * @param sensor
     * @return
     */
    public boolean insertSensor(Sensor sensor) {
        getSession().save(sensor);
        return true;
    }

    /**
     * Hämtar alla events från databasen
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllEvent() {
        return getSession()
                .createNativeQuery("SELECT start_time, end_time, doors_time FROM events").addScalar("start_time", LocalDateTimeType.INSTANCE)
                .addScalar("end_time", LocalDateTimeType.INSTANCE)
                .addScalar("doors_time", LocalDateTimeType.INSTANCE).list();
    }

    /**
     * Tar bort ett simulerad värde med det angivna id:et.
     *
     * @param id
     * @return
     */
    public boolean deleteSimulatedValue(long id) {
        getSession().createQuery("DELETE FROM SimulatedValue WHERE id = :id").setParameter("id", id).executeUpdate();
        return true;
    }

    /**
     * Uppdaterar en sensor genom att simulera alla värden och lägga till dem i
     * listan.
     *
     * @param sensor
     * @return
     */
    public boolean updateValues(Sensor sensor) {
        sensor.setValues();
        getSession().update(sensor);
        getSession().clear();
        return true;
    }

    /**
     * Uppdaterar en sensor i databasen.
     *
     * @param sensor
     * @return
     */
    public boolean updateSensor(Sensor sensor) {
        getSession().update(sensor);
        return true;
    }

    /**
     * Tar bort en sensor från databasen.
     *
     * @param sensor
     * @return
     */
    public boolean deleteSensor(Sensor sensor) {
        getSession().delete(sensor);
        return true;
    }

}
