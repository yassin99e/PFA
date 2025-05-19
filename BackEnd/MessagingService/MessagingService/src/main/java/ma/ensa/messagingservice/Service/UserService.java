package ma.ensa.messagingservice.Service;


import ma.ensa.messagingservice.Feign.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserInterface userInterface;

    public String test(){
        return userInterface.testCall();
    }

}
