package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Component
public class JWTDecoder {

    private Issuers issuers = new Issuers();

    @Value("${evide.version}")
    private String apiVersion;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public boolean decode(String token) {
        DecodedJWT jwtUnverified = JWT.decode(token);
        String iss = jwtUnverified.getIssuer();
        String secret = issuers.getKey(iss);

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

    public String issuer() {
        return apiVersion;
        /*
        String output = "";
        for (Map.Entry<String, String> entry : issuers.getIssuers().entrySet()) {
            output += "Key : " + entry.getKey() + " Value : " + entry.getValue() + "<br>";
        }
        return output;
         */
    }
}
