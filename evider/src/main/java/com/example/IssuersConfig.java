package com.example;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * @author johe2765 Jonathan Heikel (Wening)
 */
@Configuration
@PropertySource("classpath:application.properties")
public class IssuersConfig {

    @Value("#{PropertySplitter.map('${evide.issuers}')}")
    private Map<String, String> issuers;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    public Map<String, String> getIssuers() {
        return issuers;
    }

    public String getKey(String iss) {
        return issuers.get(iss);
    }
}
