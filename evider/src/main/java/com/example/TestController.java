package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/")
    public String defaultView() {
        return "This is the default view for group 09!";
    }
    
    @RequestMapping("/sayHello")
    public String greeting() {
        return "Hello from group 09!";
    }
    
    @RequestMapping("/sayGoodBye")
    public String goodBye() {
        return "Good bye from group 09!";
    }
}
