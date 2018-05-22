package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Component
public class TwitterHelper {

    @Autowired
    private TwitterConfig twitterConfig;

    public boolean makeTweet(String tweet) {
        Twitter twitter = new TwitterTemplate(twitterConfig.getConsumerKey(), twitterConfig.getConsumerSecret(), twitterConfig.getAccessToken(), twitterConfig.getAccessTokenSecret());
        try {
            twitter.timelineOperations().updateStatus(tweet);
        } catch (RuntimeException ex) {
            // log the error
            return false;
        }

        //return "{\"error\" : \"Tweeted: " + output + " (" + output.length() + ")\"}";
        //return "Tweeted: " + output + " (" + output.length() + ")";
        return true;
    }
}
