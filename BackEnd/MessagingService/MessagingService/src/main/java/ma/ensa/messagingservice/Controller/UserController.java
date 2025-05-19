package ma.ensa.messagingservice.Controller;


import ma.ensa.messagingservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messaging")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/test")
    public String testCall(){
        return userService.test();
    }

}
