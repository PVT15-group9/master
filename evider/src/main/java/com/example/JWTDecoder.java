package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class JWTDecoder {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    Map<String, String> issuers;

    public boolean decode(String token) {
        try {
            DecodedJWT jwtUnverified = JWT.decode(token);
            String iss = jwtUnverified.getIssuer();
            String secret = null;
            secret = issuers.get(iss);
            if (secret == null) {
                return false;
            }

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
}
