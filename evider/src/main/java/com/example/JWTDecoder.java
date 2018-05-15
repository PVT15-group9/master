package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
public class JWTDecoder {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    private Map<String, String> issuers;

    @Value("${evide.issuers}")
    private String evideIssuers;

    public boolean decode(String token) {
        DecodedJWT jwtUnverified = JWT.decode(token);
        String iss = jwtUnverified.getIssuer();
        String secret = issuers.get(iss);

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    //.acceptExpiresAt(5)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

        } catch (UnsupportedEncodingException | JWTVerificationException e) {
            return false;
        }

        return true;
    }

    public String issuer(String token) {
        return evideIssuers;
        /*
        String output = "";
        for (Map.Entry<String, String> entry : issuers.entrySet()) {
            output += "Key : " + entry.getKey() + " Value : " + entry.getValue() + "<br>";
        }
        return output;
         */
    }
}
