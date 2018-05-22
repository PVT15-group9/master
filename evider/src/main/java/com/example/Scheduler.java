package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class Scheduler {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;

    private static final int RATE = 60 * 60 * 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private TwitterConfig twitterConfig;

    // should run every hour at minute zero
    @Scheduled(cron="0 0 * * * *")
    public void checkDbLights() {
        LOGGER.info(this.tweetLights());
    }
    
    @Scheduled(cron="0 0 12 * * *") //sätta till kl 12:00 varje dag. 
    public void checkDbEvents(){
        LOGGER.info(this.tweetEvent());
    }
    
    public void checkDbSensor(){
        LOGGER.info(this.tweetSensorValue());
    }
        
    
    public String tweetSensorValue(){
        
        
        return "";
    }
       
    
    public String tweetEvent(){
        cxn = db.connect();
      
        String sql = "SELECT e.name, e.venue_id, e.start_time, e.doors_time, e.event_url FROM events e, JOIN venues v ON e.venue_id = v.id" ;
        PreparedStatement stmt;
        ResultSet rs;
        String output = "";
        
        
        
        //check in db for event same day, get name, doors time and arena. 
        try {
            stmt = cxn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("e.name");
                Timestamp startTime = rs.getTimestamp("start_time");
                String venue = rs.getString("v.name");
                String doorsTime = rs.getString("doors_time");
                
                Date date = new Date(); 
                
                if (startTime.equals(date)){
                     output += "At " + venue + " today: " + name +". Doors at " + doorsTime + ", events starts at: " + startTime;
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
        /*
            SELECT r.color, r.distance_in_meters, v.name AS 'v_name', e.name AS 'e_name', t.name AS 't_name'
            FROM routes r
            JOIN venues v ON r.venue_id = v.id
            JOIN endpoints e ON r.endpoint_id = e.id
            JOIN transport_types t ON e.transport_type = t.id
            ORDER BY RAND()
            LIMIT 1
         */
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
