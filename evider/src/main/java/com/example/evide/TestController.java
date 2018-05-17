package com.example.evide;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;
    private static final String version = "/api/v1/";

    @Autowired
    private JWTDecoder jwtDecoder;

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

    /*
     The following two routes should be how we do it in production!
     */
    @RequestMapping(value = "/error", produces = "application/json")
    public String error() {
        return "{\"error\" : \"This is a generic error!\"}";
    }
    
    @CrossOrigin
    @RequestMapping(value = version + "routes", method = RequestMethod.GET, produces = "application/json")
    public String getRoutesByVenue(@RequestHeader("Authorization") String authHeader) {
        if (authHeader.length() < 7) {
            return "{\"error\" : \"Invalid Authorization header!\"}";
        }
        if (!authHeader.substring(0, 7).equals("Bearer ")) {
            return "{\"error\" : \"Malformed Authorization header!\"}";
        }
        
        String token = authHeader.substring(7);
        
        boolean decoded = jwtDecoder.decode(token);
        if(!decoded) {
            return "{\"error\" : \"jwt was not verified\"}";
        }
        
        return jwtDecoder.getPayload(token);
    }

    @RequestMapping(value = version + "venues", method = RequestMethod.GET, produces = "application/json")
    public String getVenuesProd() {
        cxn = db.connect();
        String sql = "SELECT * FROM venues";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    @RequestMapping(value = version + "endpoints", method = RequestMethod.GET, produces = "application/json")
    public String getEndpointsProd() {
        // Currently hard coded, always venue id 1  (Globen)
        /*
            SELECT 
            r.id AS 'route_id',
            r.venue_id, 
            r.endpoint_id ,
            e.transport_type, 
            e.name AS 'e_name',
            t.name AS 't_name',
            e.SL_SITE_ID,
            t.img_url AS 'icon',
            'https://res.cloudinary.com/pvt-group09/image/upload/v1525786167/sensor-red.png' AS 'crowd_indicator',
            r.distance_in_meters,
            r.color,
            r.color_hex,
            CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), " min") AS 'time'

            FROM routes r

            JOIN venues v ON r.venue_id = v.id
            JOIN endpoints e ON r.endpoint_id = e.id
            JOIN transport_types t ON e.transport_type = t.id
            WHERE r.venue_id = 1
        
            ORDER BY r.distance_in_meters ASC
         */
        cxn = db.connect();
        //String sql = "SELECT e.id, e.transport_type, e.name, t.img_url AS 't_img_url' FROM endpoints e JOIN transport_types t ON e.transport_type = t.id WHERE e.transport_type = 1 ";
        String sql = "SELECT  r.id AS 'route_id', r.endpoint_id , e.transport_type,  e.name AS 'e_name', t.name AS 't_name', r.venue_id,  e.SL_SITE_ID, t.img_url AS 'icon', 'https://res.cloudinary.com/pvt-group09/image/upload/v1525786167/sensor-red.png' AS 'crowd_indicator', r.distance_in_meters, r.color, r.color_hex, CONCAT(ROUND(((r.distance_in_meters / 1000) / 5) * 60), \" min\") AS 'time'  FROM routes r  JOIN venues v ON r.venue_id = v.id JOIN endpoints e ON r.endpoint_id = e.id JOIN transport_types t ON e.transport_type = t.id WHERE r.venue_id = 1  ORDER BY r.distance_in_meters ASC";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    /*
    The routes below are test routes...
     */
    @RequestMapping("/")
    public String troll() {
        return "All your scrum are belong to us";
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

    @CrossOrigin
    @RequestMapping("/jwtSharp")
    public String jwtTest(@RequestHeader("Authorization") String authHeader) {
        if (authHeader.length() < 7) {
            return "Invalid Authorization header!";
        }
        if (!authHeader.substring(0, 7).equals("Bearer ")) {
            return "Malformed Authorization header";
        }
        boolean decoded = jwtDecoder.decode(authHeader.substring(7));
        return (decoded) ? "It worked!" : "JWT was not accepted!";
    }

    @RequestMapping("/jwt")
    public String jwtTest() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpb25pYy1hcHAifQ.6USP3K3hKsmkU17W4u8iCuRHSXmL50P51vgLdDj8sLU";
        boolean decoded = jwtDecoder.decode(token);
        return (decoded) ? "It worked!" : "JWT was not accepted!";
    }
}
