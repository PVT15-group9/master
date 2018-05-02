package com.example;

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
}
