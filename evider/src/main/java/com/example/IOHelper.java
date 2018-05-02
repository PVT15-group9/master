package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.sql.*;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class IOHelper {

    public static String writeException(Exception e) {
        String output = "Something went wrong<br>";
        output += "Thrown by " + e.getClass().getName();
        output += "<br><br><pre>";
        output += e.toString();
        output += "</pre>";
        return output;
    }

    public static String serializeResultSet(ResultSet result) {
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
            return IOHelper.writeException(e);
        }
        
        return sw.toString();
    }

}
