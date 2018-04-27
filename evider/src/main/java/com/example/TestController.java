package com.example;
import org.springframework.web.bind.annotation.RequestParam;
 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/")
    public String defaultRoute() {
        return "This is the default route for group 09!";
    }
    
    @RequestMapping("/sayHello")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        String hello = "Hello ";
        String from = " from group 09!";
        String retString = hello+name+from;
        return retString;
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
