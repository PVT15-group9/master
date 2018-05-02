package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //private DBConnection db = new DBConnection();
    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;

    public TestController() {
        cxn = db.connect();
    }

    @RequestMapping("/venues")
    public String getVenues() {

        return "";
    }

    @RequestMapping("/events")
    public String getEvents() {

        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        ResultSet result = null;
        Statement statement = null;
        String sql = "SELECT * FROM events";

        try {
            statement = cxn.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            return IOHelper.writeException(e);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        // put the resultset in a containing structure
        objectNode.putPOJO("results", result);

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            return IOHelper.writeException(e);
        }
        
        db.disconnect();
        return sw.toString();
        // return IOHelper.serializeResultSet(result);
    }

    @RequestMapping("/")
    public String defaultRoute() {
        return "This is the default route for group 09!";
    }

    @RequestMapping("/sayHello")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        String hello = "Hello ";
        String from = " from group 09!";
        String retString = hello + name + from;
        return retString;
    }

    @RequestMapping("/sayGoodBye")
    public String goodBye() {
        return "Good bye from group 09!";
    }

    @RequestMapping("/test")
    public String test() {
        return "A late night test!";
    }
}
