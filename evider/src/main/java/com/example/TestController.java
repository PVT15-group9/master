package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    //private DBConnection db = new DBConnection();
    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;

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

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            return IOHelper.writeException(e);
        }

        return sw.toString();
    }

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
        String sql = "SELECT * FROM events";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    @RequestMapping("/jwtSharp")
    public String jwtTest(@RequestHeader("Authorization") String token) {
        return token;
        //JWTDecoder jwtDecoder = new JWTDecoder();
        //return jwtDecoder.decode(token);
    }

    @RequestMapping("/jwt")
    public String jwtTest() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpb25pYy1hcHAifQ.6USP3K3hKsmkU17W4u8iCuRHSXmL50P51vgLdDj8sLU";
        JWTDecoder jwtDecoder = new JWTDecoder();
        return jwtDecoder.decode(token);
        
        /*
        cxn = db.connect();

        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpb25pYy1hcHAifQ.6USP3K3hKsmkU17W4u8iCuRHSXmL50P51vgLdDj8sLU";

        try {
            DecodedJWT jwtUnverified = JWT.decode(token);
            String iss = jwtUnverified.getIssuer();

            System.out.print("The issuer from the JWT : " + iss + "<br><br>");

            String sql = "SELECT secret FROM api_secrets WHERE username = ?";
            PreparedStatement stmt = cxn.prepareStatement(sql);
            stmt.setString(1, iss);
            ResultSet rs = stmt.executeQuery();

            String secret = null;

            while (rs.next()) {
                secret = rs.getString("secret");
            }

            if (secret != null) {
                Algorithm algorithm = Algorithm.HMAC256(secret);
                JWTVerifier verifier = JWT.require(algorithm)
                        .build(); //Reusable verifier instance
                DecodedJWT jwt = verifier.verify(token);
            } else {
                // error msg
            }

            stmt.close();
        } catch (UnsupportedEncodingException e) {
            return "Unsupported encoding!<br>" + IOHelper.writeException(e);
        } catch (JWTVerificationException e) {
            return "JWT could not be verified!<br>" + IOHelper.writeException(e);
        } catch (SQLException e) {
            return "SQL went wrong!<br>" + IOHelper.writeException(e);
        }

        db.disconnect();
        return "All good! :)";
         */
    }
}
