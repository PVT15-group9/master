package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;
    private static final String version = "/api/v1/";

    @Autowired
    private JWTDecoder jwtDecoder;

    @Autowired
    private TwitterConfig twitterConfig;

    // Base route
    @RequestMapping("/")
    public String troll() {
        return "All your scrum are belong to us";
    }

    // some helpers
    private String resultSetToJSON(ResultSet rs) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        ObjectNode objectNode = objectMapper.createObjectNode();
        // put the resultset in a containing structure
        objectNode.putPOJO("results", rs);
        // @TODO : How to get rid of the containing structure /JW

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            return IOHelper.writeException(e);
        }

        return sw.toString();
    }

    private String executeQueryAndPrintResult(String sql) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        ResultSet result = null;
        Statement statement = null;

        try {
            statement = cxn.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            return IOHelper.writeException(e);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        // put the resultset in a containing structure
        objectNode.putPOJO("results", result);
        // @TODO : How to get rid of the containing structure /JW

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            return IOHelper.writeException(e);
        }

        return sw.toString();
    }
    // end helpers

    /*
     Production routes
     */
    @CrossOrigin
    @RequestMapping(value = version + "route", method = RequestMethod.GET, produces = "application/json")
    public String getRouteById(@RequestHeader("Authorization") String authHeader, @RequestParam("user_value") int userValue) {
        if (authHeader.length() < 7) {
            return "{\"error\" : \"Invalid Authorization header!\"}";
        }
        if (!authHeader.substring(0, 7).equals("Bearer ")) {
            return "{\"error\" : \"Malformed Authorization header!\"}";
        }

        String token = authHeader.substring(7);

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return "{\"error\" : \"jwt was not verified : " + token + "\"}";
        }

        cxn = db.connect();
        String sql = "SELECT  r.id AS 'route_id', r.endpoint_id , e.transport_type,  e.name AS 'e_name', t.name AS 't_name', r.venue_id,  e.SL_SITE_ID, t.img_url AS 'icon', (SELECT url FROM crowd_indicators ORDER BY RAND() LIMIT 1) AS 'crowd_indicator', r.distance_in_meters, r.color, r.color_hex, CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), \" min\") AS 'time'  FROM routes r  JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id WHERE r.id = ?  ORDER BY r.distance_in_meters ASC";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = cxn.prepareStatement(sql);
            stmt.setInt(1, userValue);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            return "{\"error\" : \"error in sql\"}";
        }

        String output = this.resultSetToJSON(rs);
        db.disconnect();
        return output;
    }

    @CrossOrigin
    @RequestMapping(value = version + "routes", method = RequestMethod.GET, produces = "application/json")
    public String getRoutesByVenue(@RequestHeader("Authorization") String authHeader, @RequestParam("user_value") int userValue) {
        if (authHeader.length() < 7) {
            return "{\"error\" : \"Invalid Authorization header!\"}";
        }
        if (!authHeader.substring(0, 7).equals("Bearer ")) {
            return "{\"error\" : \"Malformed Authorization header!\"}";
        }

        String token = authHeader.substring(7);

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return "{\"error\" : \"jwt was not verified : " + token + "\"}";
        }

        cxn = db.connect();
        String sql = "SELECT  r.id AS 'route_id', r.endpoint_id , e.transport_type,  e.name AS 'e_name', t.name AS 't_name', r.venue_id,  e.SL_SITE_ID, t.img_url AS 'icon', (SELECT url FROM crowd_indicators ORDER BY RAND() LIMIT 1) AS 'crowd_indicator', r.distance_in_meters, r.color, r.color_hex, CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), \" min\") AS 'time'  FROM routes r  JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id WHERE r.venue_id = ?  ORDER BY r.distance_in_meters ASC";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = cxn.prepareStatement(sql);
            stmt.setInt(1, userValue);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            return "{\"error\" : \"error in sql\"}";
        }

        String output = this.resultSetToJSON(rs);
        db.disconnect();
        return output;
    }

    @CrossOrigin
    @RequestMapping(value = version + "venues", method = RequestMethod.GET, produces = "application/json")
    public String getVenuesProd(@RequestHeader("Authorization") String authHeader) {
        String token = jwtDecoder.validateHeader(authHeader);
        if (token == null) {
            return IOHelper.errorAsJSON("Invalid Authorization header! (#2)");
        }

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return IOHelper.errorAsJSON("JWT was not verified : " + token);
        }

        cxn = db.connect();
        String sql = "SELECT * FROM venues";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }
    // END production routes

    /*
        Test route for sensors
     */
    @RequestMapping("/sensors")
    public String getSensors() {
        cxn = db.connect();
        String sql = "SELECT * FROM sensor";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    /*
        The routes below are test routes...
     */
    @RequestMapping("/eventToday")
    public String eventToday() {
        //TwitterHelper th = new TwitterHelper();

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
                String startTime = rs.getString("doors_time");
                String endTime = rs.getString("doors_time");

                String eventUrl = rs.getString("event_url");

                String output = "At " + venueName + " today: " + eventName + ". Doors at " + doorsTime + ", events starts at: " + startTime;
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

    @RequestMapping("/venues")
    public String getVenues() {
        cxn = db.connect();
        String sql = "SELECT * FROM venues";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    @RequestMapping("/events")
    public String getEvents() {
        cxn = db.connect();
        String sql = "SELECT events.id, events.name, venues.name FROM events INNER JOIN venues ON events.venue_id = venues.id";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }
}
