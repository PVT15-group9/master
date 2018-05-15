package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Configuration
public class Issuers {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    private Map<String, String> issuers;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    public Map<String, String> getIssuers() {
        return issuers;
    }

    public String getKey(String iss) {
        return issuers.get(iss);
    }
}
