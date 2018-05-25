package com.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.dto.EventDTO;
import com.example.dto.RouteDTO;
import com.example.dto.ThresholdDTO;
import com.example.model.RouteValueRegister;
import com.example.model.Sensor;
import com.example.model.SimulatedValue;

import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
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
    public Sensor findById(long id) {
        return getSession().byId(Sensor.class).load(id);
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
     * Sparar ett nytt registrerat värde för sensorn.
     *
     * @param newValue
     * @return
     */
    public boolean registerValue(RouteValueRegister newValue) {
        getSession().save(newValue);
        return true;
    }

    /**
     * Hämtar alla registrerade värdena från db.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<RouteValueRegister> getRegister() {
        return getSession().createQuery("FROM RouteValueRegister").list();
    }

    /**
     * Hämtar alla events från databasen.
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<EventDTO> findAllEvent() {
        return getSession()
                .createNativeQuery("SELECT event.id as \"eventId\", event.user_id as \"userId\", event.venue_id as \"venueId\", "
                        + "event.name as \"eventname\", event.start_time as \"eventStartTime\", event.end_time as \"eventEndTime\", "
                        + "event.doors_time as \"eventDoorOpenTime\", event.event_url as \"url\" "
                        + "FROM events event")
                .setResultTransformer(Transformers.aliasToBean(EventDTO.class))
                .list();
    }

    /**
     * Hämtar alla routes från databasen.
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<RouteDTO> findAllRoute() {
        return getSession().createNativeQuery("SELECT route.id as \"routeId\", route.venue_id as \"venueId\", "
                + "route.endpoint_id as \"endpointId\", route.color as \"color\", route.color_hex as \"colorHex\", "
                + "route.distance_in_meters as \"distanceInMeter\", route.x_faktor as \"xFaktor\" "
                + "FROM routes route")
                .setResultTransformer(Transformers.aliasToBean(RouteDTO.class))
                .list();
    }

    /**
     * Hämtar routes med det angivna id:et.
     *
     * @param id
     * @return
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<RouteDTO> findRoutById(long id) {
        return getSession().createNativeQuery("SELECT route.id as \"routeId\", route.venue_id as \"venueId\", "
                + "route.endpoint_id as \"endpointId\", route.color as \"color\", route.color_hex as \"colorHex\", "
                + "route.distance_in_meters as \"distanceInMeter\", route.x_faktor as \"xFaktor\" "
                + "FROM routes route WHERE route.venue_id = :id")
                .setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(RouteDTO.class))
                .list();
    }

    /**
     * Hämtar en lista med id för alla routes med det angivna id:et för en
     * eventplats.
     *
     * @param id
     * @return
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<Integer> findRouteByVenueId(int id) {
        List<RouteDTO> routes = getSession().createNativeQuery("SELECT route.id as \"routeId\" "
                + "FROM routes route WHERE route.venue_id = :id")
                .setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(RouteDTO.class))
                .list();

        return routes.stream().map(RouteDTO::getRouteId).collect(Collectors.toList());
    }

    /**
     * Hämtar faktorn multiplikator för route med det angivna id:et.
     *
     * @param id
     * @return om finns ingen returnerar 1.
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public Double findXFactorByRouteId(int id) {
        List<RouteDTO> routes = getSession().createNativeQuery("SELECT route.x_faktor as \"xFaktor\" "
                + "FROM routes route WHERE route.id = :id")
                .setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(RouteDTO.class))
                .list();

        return routes.stream().map(RouteDTO::getxFaktor).findFirst().orElse(1.0);
    }

    /**
     * Hämtar en lista med id för thresholds med det angivna id:et för route.
     *
     * @param id
     * @return
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public List<Integer> findAllThresholdbyRouteId(int id) {
        List<ThresholdDTO> thresholds = getSession().createNativeQuery("SELECT t.id as \"thresholdId\", t.route_id as \"routeId\", "
                + "t.type as \"thresholdType\", t.amount as \"thresholdAmount\" "
                + "FROM thresholds t WHERE t.route_id = :id").setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(ThresholdDTO.class))
                .list();
        return thresholds.stream().map(ThresholdDTO::getThresholdId).collect(Collectors.toList());
    }

    /**
     * Uppdaterar amount i thresholds-tabellen med det angivna id:et för
     * thresholdId.
     *
     * @param amount
     * @param thresholdId
     * @return
     */
    public boolean updateTresholdAmount(int amount, int thresholdId) {
        getSession().createNativeQuery("UPDATE thresholds SET amount = :amount WHERE thresholds.id = :thresholdId")
                .setParameter("amount", amount)
                .setParameter("thresholdId", thresholdId)
                .executeUpdate();
        return true;
    }

    /**
     * Tar bort ett simulerad värde.
     *
     * @param oldValue
     * @return
     */
    public boolean deleteSimulatedValue(SimulatedValue oldValue) {
        getSession().delete(oldValue);
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
