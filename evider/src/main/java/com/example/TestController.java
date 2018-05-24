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
    private static final String API_VERSION = "/api/v1/";

    @Autowired
    private JWTDecoder jwtDecoder;

    //@Autowired
    //private TwitterConfig twitterConfig;
    //@Autowired
    //private TwitterHelper twitterHelper;
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
    @RequestMapping(value = API_VERSION + "venueHasEvent", method = RequestMethod.GET, produces = "application/json")
    public String venueHasEvent(@RequestHeader("Authorization") String authHeader, @RequestParam("user_value") int userValue) {
        String token = jwtDecoder.validateHeader(authHeader);
        if (token == null) {
            return IOHelper.errorAsJSON("Invalid Authorization header! (#2)");
        }

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return IOHelper.errorAsJSON("JWT was not verified : " + token);
        }

        cxn = db.connect();
        String sql = "SELECT * FROM events WHERE venue_id = ? AND ((DATE(start_time) = SUBDATE(CURRENT_DATE(), 1) OR DATE(doors_time) = SUBDATE(CURRENT_DATE(), 1)) OR (DATE(start_time) = CURRENT_DATE() OR DATE(doors_time) = CURRENT_DATE()))";
        PreparedStatement stmt;
        ResultSet rs;

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
    @RequestMapping(value = API_VERSION + "route", method = RequestMethod.GET, produces = "application/json")
    public String getRouteById(@RequestHeader("Authorization") String authHeader, @RequestParam("user_value") int userValue) {
        String token = jwtDecoder.validateHeader(authHeader);
        if (token == null) {
            return IOHelper.errorAsJSON("Invalid Authorization header! (#2)");
        }

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return IOHelper.errorAsJSON("JWT was not verified : " + token);
        }

        cxn = db.connect();
        String sql = "SELECT "
                + "r.id AS 'route_id', "
                + "r.endpoint_id , "
                + "e.transport_type,  "
                + "e.name AS 'e_name', "
                + "t.name AS 't_name', "
                + "r.venue_id, "
                + "e.SL_SITE_ID, "
                + "t.img_url AS 'icon', "
                + "("
                + "SELECT url FROM crowd_indicators WHERE name = "
                + "("
                + "CASE "
                + "WHEN (SELECT sensor_value FROM route_value_register WHERE route_id = ? ORDER BY time_stamp DESC LIMIT 1) <= (SELECT amount FROM thresholds WHERE route_id = ? AND type = \"GREEN\") THEN \"GREEN\" "
                + "WHEN (SELECT sensor_value FROM route_value_register WHERE route_id = ? ORDER BY time_stamp DESC LIMIT 1) <= (SELECT amount FROM thresholds WHERE route_id = ? AND type = \"YELLOW\") THEN \"YELLOW\" "
                + "WHEN (SELECT sensor_value FROM route_value_register WHERE route_id = ? ORDER BY time_stamp DESC LIMIT 1) >  (SELECT amount FROM thresholds WHERE route_id = ? AND type = \"YELLOW\") THEN \"RED\" "
                + "WHEN 1=1 THEN \"GREEN\" "
                + "END"
                + ") AS 'crowd_indicator', "
                + "r.distance_in_meters, "
                + "r.color, "
                + "r.color_hex, "
                + "CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), \" min\") AS 'time' "
                + "FROM routes r "
                + "JOIN venues v ON r.venue_id = v.id "
                + "JOIN endpoints e ON r.endpoint_id = e.id "
                + "JOIN transport_types t ON e.transport_type = t.id "
                + "WHERE r.id = ? "
                + "ORDER BY r.distance_in_meters ASC";

        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = cxn.prepareStatement(sql);
            // we need to set the route id seven times
            int i = 1;
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            stmt.setInt(i++, userValue);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            return "{\"error\" : \"error in sql : " + e.getMessage() + "\"}";
        }

        String output = this.resultSetToJSON(rs);
        db.disconnect();
        return output;
    }

    @CrossOrigin
    @RequestMapping(value = API_VERSION + "routes", method = RequestMethod.GET, produces = "application/json")
    public String getRoutesByVenue(@RequestHeader("Authorization") String authHeader, @RequestParam("user_value") int userValue) {
        String token = jwtDecoder.validateHeader(authHeader);
        if (token == null) {
            return IOHelper.errorAsJSON("Invalid Authorization header! (#2)");
        }

        boolean decoded = jwtDecoder.decode(token);
        if (!decoded) {
            return IOHelper.errorAsJSON("JWT was not verified : " + token);
        }

        cxn = db.connect();
        String sql = "SELECT r.id AS 'route_id', r.endpoint_id , e.transport_type,  e.name AS 'e_name', t.name AS 't_name', r.venue_id,  e.SL_SITE_ID, t.img_url AS 'icon', (SELECT url FROM crowd_indicators ORDER BY RAND() LIMIT 1) AS 'crowd_indicator', r.distance_in_meters, r.color, r.color_hex, CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), \" min\") AS 'time'  FROM routes r  JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id WHERE r.venue_id = ?  ORDER BY r.distance_in_meters ASC";

        PreparedStatement stmt;
        ResultSet rs;

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
    @RequestMapping(value = API_VERSION + "venues", method = RequestMethod.GET, produces = "application/json")
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
}
