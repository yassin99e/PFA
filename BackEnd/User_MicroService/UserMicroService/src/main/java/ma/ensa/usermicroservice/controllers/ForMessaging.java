package ma.ensa.usermicroservice.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messaging")
public class ForMessaging {


    @GetMapping("/test")
    public String testCall(){
        return "Hello From User Microservice To Messaging Service";
    }


}
