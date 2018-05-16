package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Service
public class IssuersConfig {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    private Map<String, String> issuers;
    
    public Map<String, String> getIssuers() {
        return issuers;
    }

    public String getKey(String iss) {
        return issuers.get(iss);
    }
}
