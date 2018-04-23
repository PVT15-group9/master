package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/sayHello")
    public String greeting() {
        return "Hello from group 09!";
    }
    
    @RequestMapping("/sayGoodBye")
    public String goodBye() {
        return "Good bye from group 09!";
    }
}
