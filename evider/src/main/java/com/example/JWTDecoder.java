package com.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class JWTDecoder {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;

    public String decode(String token) {
        cxn = db.connect();
        DecodedJWT jwt = null;
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
                jwt = verifier.verify(token);
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

        return "All good! This is the token that was sent in:<br><pre>" + jwt + "</pre>";
    }
}
