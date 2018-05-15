package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class JWTDecoder {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    Map<String, String> issuers;

    private HashMap<String, HashMap<Integer, String>> retMap = new HashMap<>();

    private void setError(int code, String msg) {
        HashMap<Integer, String> errMsg = new HashMap<>();
        errMsg.put(code, msg);
        retMap.put("response", errMsg);
    }

    public HashMap<String, HashMap<Integer, String>> decode(String token) {

        DecodedJWT jwt;
        try {
            DecodedJWT jwtUnverified = JWT.decode(token);
            String iss = jwtUnverified.getIssuer();
            String secret = issuers.get(iss);

            if (secret != null) {
                Algorithm algorithm = Algorithm.HMAC256(secret);
                JWTVerifier verifier = JWT.require(algorithm)
                        //.acceptExpiresAt(5)
                        .build(); //Reusable verifier instance
                jwt = verifier.verify(token);
            } else {
                this.setError(1, "Could not verify the issuer of the token!");
                return retMap;
            }

        } catch (UnsupportedEncodingException e) {
            this.setError(2, "The token was not in UTF-8!");
            return retMap;
        } catch (JWTVerificationException e) {
            this.setError(3, "The JWT could not be verified!");
            return retMap;
        }

        this.setError(0, "JWT was verified!");
        return retMap;
    }
}
