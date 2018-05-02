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

    private DBConnection db = new DBConnection();

    @RequestMapping("/events")
    public String getEvents() {

        try {
            Connection cxn = db.connect();

            SimpleModule module = new SimpleModule();
            module.addSerializer(new ResultSetSerializer());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            String query = "SELECT * FROM events";
            Statement statement = cxn.createStatement();
            ResultSet result = statement.executeQuery(query);

            ObjectNode objectNode = objectMapper.createObjectNode();
            // put the resultset in a containing structure
            objectNode.putPOJO("results", result);

            // generate all
            StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, objectNode);

            result.close();
            statement.close();
            db.disconnect();

            return sw.toString();

        } catch (SQLException | IOException | IllegalStateException ex) {
            StringWriter sw = new StringWriter(); // create a StringWriter
            PrintWriter pw = new PrintWriter(sw); // create a PrintWriter using this string writer instance
            ex.printStackTrace(pw); // print the stack trace to the print writer(it wraps the string writer sw
            
            String output = "Something went wrong<br>";
            output += "Thrown by " + ex.getClass().getName();
            output += "<br><br><pre>";
            output += sw.toString();
            output += "</pre>";
            return output;
        }
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
