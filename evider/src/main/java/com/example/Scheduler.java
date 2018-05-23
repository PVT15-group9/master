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

    @Autowired
    private TwitterHelper twitterHelper;

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
        TwitterHelper th = new TwitterHelper();
        Twitter twitter = new TwitterTemplate(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret(), twitterConfig.getAccessToken(), twitterConfig.getAccessTokenSecret());

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
                String endTime = rs.getString("end_time");

                String eventUrl = rs.getString("event_url");

                String output = "At " + venueName + " today: " + eventName + ". Doors at " + doorsTime + ", events starts at: " + startTime;
                if (!twitterHelper.makeTweet(output)) {
                    return "Error when making tweet!";
                }

                try {
                    twitter.timelineOperations().updateStatus(output);
                } catch (RuntimeException ex) {
                    // log the error
                    return "Could not tweet : " + ex.getMessage();
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
