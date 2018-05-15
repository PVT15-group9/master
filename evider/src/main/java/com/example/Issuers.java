package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
public class Issuers {

    @Autowired
    private Environment env;
    
    private Map<String, String> issuers;

    //@Value("#{PropertySplitter.map('${evide.issuers}')}")
    //private Map<String, String> issuers;

    public Issuers() {
        String issuersFromFile = env.getProperty("evide.issuers");
        PropertySplitter ps = new PropertySplitter();
        issuers = ps.map(issuersFromFile);
    }
    
    public Map<String, String> getIssuers() {
        return issuers;
    }

    public String getKey(String iss) {
        return issuers.get(iss);
    }
}
