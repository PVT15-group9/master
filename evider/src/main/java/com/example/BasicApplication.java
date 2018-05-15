package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class BasicApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BasicApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
        logger.debug(" ** Application Started ** ");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<BasicApplication> applicationClass = BasicApplication.class;
}