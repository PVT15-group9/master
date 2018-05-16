package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Component
public class Issuers {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    static Map<String, String> issuers;
    
    public static Map<String, String> getIssuers() {
        return issuers;
    }

    public static String getKey(String iss) {
        return issuers.get(iss);
    }
}
