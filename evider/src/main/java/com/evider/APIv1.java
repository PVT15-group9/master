package com.evider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@RestController
public class APIv1 {
    
    private static final String version = "/api/v1/";

    @RequestMapping(version + "venues")
    public String getVenues() {
        return "{\"foo\" : \"bar\", \"from\" : \"venues\"}";
    }
}
