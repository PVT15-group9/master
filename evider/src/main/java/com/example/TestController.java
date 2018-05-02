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

        ResultSet result = null;
        Statement statement = null;
        String sql = "SELECT * FROM events";

        try {
            statement = cxn.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            return IOHelper.writeException(e);
        }

        String json = IOHelper.serializeResultSet(result);

        db.disconnect();

        return json;
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
