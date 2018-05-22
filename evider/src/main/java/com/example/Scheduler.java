package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private TwitterConfig twitterConfig;

    @Scheduled(cron = "0 30 12 * * *") //sätta till kl 12:00 varje dag. 
    public void checkDbEvents() {
        LOGGER.info(this.tweetEvent());
    }

    // Should run every three hours
    @Scheduled(cron = "0 0 */3 * * *")
    public void checkDbLights() {
        LOGGER.info(this.tweetLights());
    }

    public void checkDbSensor() {
        LOGGER.info(this.tweetSensorValue());
    }

    public String tweetSensorValue() {

        return "";
    }

    public String tweetEvent() {
        cxn = db.connect();

        String sql = "SELECT e.name, e.venue_id, e.start_time, e.doors_time, e.event_url FROM events e, JOIN venues v ON e.venue_id = v.id";
        PreparedStatement stmt;
        ResultSet rs;
        String output = "";

        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("e.name");
                Timestamp startTime = rs.getTimestamp("start_time");
                String venue = rs.getString("v.name");
                String doorsTime = rs.getString("doors_time");

                System.out.println(name + " " + startTime + " " + venue + " " + doorsTime);

                Timestamp t = new Timestamp(System.currentTimeMillis());
                String t0 = t.toString();
                String[] parts = t0.split(" ");
                String t1 = parts[0];

                String s0 = startTime.toString();
                String[] parts2 = s0.split(" ");
                String s1 = parts2[0];

                if (t1.equals(s1)) {
                    output += "At " + venue + " today: " + name + ". Doors at " + doorsTime + ", events starts at: " + startTime;
                }

            }
            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
            //return "{\"error\" : \"error in sql\"}";
        }
        db.disconnect();

        Twitter twitter = new TwitterTemplate(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret(), twitterConfig.getAccessToken(), twitterConfig.getAccessTokenSecret());
        try {
            twitter.timelineOperations().updateStatus(output);
        } catch (RuntimeException ex) {
            //return "{\"error\" : \"Unable to tweet \"" + output + "\". Exception: " + ex + "\"}";
            return "Unable to tweet" + output + ". Error:<br>" + ex;
        }

        //return "{\"error\" : \"Tweeted: " + output + " (" + output.length() + ")\"}";
        return "Tweeted: " + output + " (" + output.length() + ")";

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

                output += " Get from " + venue + " to " + endpoint + " (" + transportType + ") by following the " + color + " lights - it's only " + distance + " meters!";
            }
            stmt.close();
        } catch (SQLException e) {
            return "Error in SQL : " + e;
            //return "{\"error\" : \"error in sql\"}";
        }
        db.disconnect();

        Twitter twitter = new TwitterTemplate(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret(), twitterConfig.getAccessToken(), twitterConfig.getAccessTokenSecret());
        try {
            twitter.timelineOperations().updateStatus(output);
        } catch (RuntimeException ex) {
            //return "{\"error\" : \"Unable to tweet \"" + output + "\". Exception: " + ex + "\"}";
            return "Unable to tweet" + output + ". Error:<br>" + ex;
        }

        //return "{\"error\" : \"Tweeted: " + output + " (" + output.length() + ")\"}";
        return "Tweeted: " + output + " (" + output.length() + ")";
    }
}
