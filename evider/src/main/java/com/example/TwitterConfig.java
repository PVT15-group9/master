package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Configuration
@PropertySource({"classpath:twitter.properties"})
public class TwitterConfig {

    @Value("${oauth.consumerKey}")
    private String consumerKey;
    
    @Value("${oauth.consumerSecret}")
    private String consumerSecret;
    
    @Value("${oauth.accessToken}")
    private String accessToken;
    
    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret;
    
    public String getConsumerKey() {
        return consumerKey;
    }
    
    public String getConsumerSecret() {
        return consumerSecret;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }
}
