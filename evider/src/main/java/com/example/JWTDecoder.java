package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Service
public class JWTDecoder {

    @Autowired
    private IssuersConfig issuers;

    /**
     *
     * @param token A JWT to be verified.
     * @return true if the token can be verified, false if not
     */
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

    /**
     *
     * @param token A valid JWT
     * @return The payload of the token
     */
    public String getPayload(String token) {
        return JWT.decode(token).getPayload();
    }

    /**
     *
     * @param header The authorization header
     * @return null if the header is malformed or the token if it's correct
     */
    public String validateHeader(String header) {
        return (header.length() < 7 || !header.substring(0, 7).equals("Bearer ")) ? null : header.substring(7);
    }
}
