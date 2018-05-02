package com.example;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.*;
import com.fasterxml.jackson.databind.node.*;
import java.io.*;
import java.sql.*;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //private DBConnection db = new DBConnection();
    private MySQLConnect db = new MySQLConnect();

    @RequestMapping("/events")
    public String getEvents() {

        Connection cxn = null;

        try {
            cxn = db.connect();
        } catch (Exception e) {
            String output = "Something went wrong<br>";
            output += "Thrown by " + e.getClass().getName();
            output += "<br><br><pre>";
            output += e.toString();
            output += "</pre>";
            return output;
        }

        ResultSet result = null;
        Statement statement = null;
        String sql = "SELECT * FROM events";

        try {
            statement = cxn.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            String output = "Something went wrong @44<br>";
            output += "Thrown by " + e.getClass().getName();
            output += "<br><br><pre>";
            output += e.toString();
            output += "</pre>";
            return output;
        }

        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        ObjectNode objectNode = objectMapper.createObjectNode();
        // put the resultset in a containing structure
        objectNode.putPOJO("results", result);

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            String output = "Something went wrong @67<br>";
            output += "Thrown by " + e.getClass().getName();
            output += "<br><br><pre>";
            output += e.toString();
            output += "</pre>";
            return output;
        }

        db.disconnect();

        return sw.toString();
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
