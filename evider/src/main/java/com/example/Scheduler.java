package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;
    String output;

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private TwitterHelper twitterHelper;

    //Runs at noon
    @Scheduled(cron = "0 35 12 * * *")
    public void checkDbEvents() {
        LOGGER.info(this.tweetEvent());
    }

    // Should run every three hours
    @Scheduled(cron = "0 0 */3 * * *")
    public void checkDbLights() {
        LOGGER.info(this.tweetLights());
    }

    // Runs every minute
    @Scheduled(cron = "*/10 * * * * *")
    public void addFauxSensorValues() {
        LOGGER.info(this.makeFauxSensorValues());
    }

    // Runs every hour
    @Scheduled(cron = "0 * * * * *")
    public void removeFauxSensorValues() {
        LOGGER.info(this.clearFauxSensorValues());
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void checkDbSensor() {
        LOGGER.info(this.tweetSensor());

    }

    public String tweetSensor() {
        cxn = db.connect();
        String sql = "SELECT * FROM routes WHERE latestTweet < (NOW() - INTERVAL 30 MINUTE) OR latestTweet IS NULL ";
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");

                String sql2 = "SELECT v.name AS 'v_name', e.name AS 'e_name', t.name AS 't_name', ( SELECT ( CASE WHEN (SELECT value FROM faux_sensor_values WHERE route_id = ? ORDER BY timestamp DESC LIMIT 1) > (SELECT amount FROM thresholds WHERE route_id = ? AND type = \"YELLOW\") THEN \"RED\" WHEN 1=1 THEN \"GREEN\" END ) ) AS 'crowd_indicator'  FROM routes r  JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id WHERE r.id = ?";
                PreparedStatement stmt2;
                ResultSet rs2;
                stmt2 = cxn.prepareStatement(sql2);
                stmt2.setInt(1, id);
                stmt2.setInt(2, id);
                stmt2.setInt(3, id);

                rs2 = stmt2.executeQuery();

                while (rs2.next()) {
                    String ci = rs2.getString("crowd_indicator");
                    if (ci.equals("RED")) {

                        String vName = rs.getString("v_name");
                        String eName = rs.getString("e_name");
                        String tName = rs.getString("t_name");

                        String output = "Now there are many people walking between " + vName + " and " + eName + " " + tName + "station";
                        if (!twitterHelper.makeTweet(output)) {
                            return "Error when making tweet!";
                        }
                        PreparedStatement stmt3;
                        String update = "UPDATE routes SET latestTweet = NOW() WHERE id = ?";
                        stmt3 = cxn.prepareStatement(update);
                        stmt3.setInt(1, id);
                        int rows = stmt3.executeUpdate();
                    }

                }
            }

            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
        }
        db.disconnect();
        return "Done";
    }

    public String makeFauxSensorValues() {
        cxn = db.connect();
        ArrayList<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM routes";
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
        }

        for (Integer id : ids) {
            String insert = "INSERT INTO faux_sensor_values (route_id, value) VALUES (?, (SELECT ( ROUND(((RAND() * (2-0.2))+0.2) * AVG(amount)) ) AS 'value' FROM thresholds WHERE route_id = ? GROUP BY route_id));";
            try {
                stmt = cxn.prepareStatement(insert);
                stmt.setInt(1, id);
                stmt.setInt(2, id);
                int insertedRows = stmt.executeUpdate();
            } catch (SQLException e) {
                return "Error in SQL : " + e;
            }
        }

        db.disconnect();
        return "Done";
    }

    public String clearFauxSensorValues() {
        cxn = db.connect();
        String sql = "DELETE FROM faux_sensor_values WHERE timestamp < (NOW() - INTERVAL 1 MINUTE)";
        PreparedStatement stmt;
        try {
            stmt = cxn.prepareStatement(sql);
            int insertedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
        }
        db.disconnect();
        return "Done";
    }

    public String tweetEvent() {
        cxn = db.connect();
        String sql = "SELECT v.name AS 'venue_name', e.name AS 'event_name', e.doors_time, e.start_time, e.end_time, e.event_url FROM events e JOIN venues v ON e.venue_id = v.id WHERE DATE(start_time) = CURRENT_DATE() OR DATE(doors_time) = CURRENT_DATE()";
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String eventName = rs.getString("event_name");
                String venueName = rs.getString("venue_name");

                String doorsTime = rs.getString("doors_time");
                String startTime = rs.getString("start_time");
                String eventUrl = rs.getString("event_url");

                String[] d = doorsTime.split(" ");
                String d1 = d[1];
                String[] ds = d1.split(":");
                String ds1 = ds[0];
                String ds2 = ds[1];

                String[] s = startTime.split(" ");
                String s1 = s[1];
                String[] ss = s1.split(":");
                String ss1 = ss[0];
                String ss2 = ss[1];

                output = "At " + venueName + " today: " + eventName + ".\nDoors open at " + ds1 + ":" + ds2 + " and the events starts at " + ss1 + ":" + ss2;
                if (!twitterHelper.makeTweet(output)) {
                    return "Error when making tweet!";
                }
            }
            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
        }
        db.disconnect();
        return "Done";
    }

    public String tweetLights() {
        cxn = db.connect();

        String sql = "SELECT r.color, r.distance_in_meters, v.name AS 'v_name', e.name AS 'e_name', t.name AS 't_name' FROM routes r JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id ORDER BY RAND() LIMIT 1";
        PreparedStatement stmt;
        ResultSet rs;
        String output = "";

        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String color = rs.getString("color");
                int distance = rs.getInt("distance_in_meters");
                String venue = rs.getString("v_name");
                String endpoint = rs.getString("e_name");
                String transportType = rs.getString("t_name");

                output = " Get from " + venue + " to " + endpoint + " (" + transportType + ") by following the " + color + " lights \n - it's only " + distance + " meters!";
                if (!twitterHelper.makeTweet(output)) {
                    return "Error when making tweet!";
                }
            }
            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
        }
        db.disconnect();
        return "Done";
    }
}
