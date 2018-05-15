package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class Issuers {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    private Map<String, String> issuers;
    
    public Map<String, String> getIssuers() {
        return issuers;
    }
    
    public String getKey(String iss) {
        return issuers.get(iss);
    }
}
