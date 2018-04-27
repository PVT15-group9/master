package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestController {

    @RequestMapping("/")
    public String defaultRoute() {
        return "This is the default route for group 09!";
    }
    
    @RequestMapping("/sayHello")
    public String greeting() {
        String ret = "Hello from Group 9!;
        return ret;
    }
    
    @RequestMapping("/sayGoodBye")
    public String goodBye() {
        return "Good bye from group 09!";
    }
    
    @RequestMapping("/test")
    public String test() {
        return "A late night test!";
    }
}
