package ma.ensa.messagingservice.Feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient("USERMICROSERVICE")
public interface UserInterface {


    @GetMapping("/messaging/test")
    public String testCall();

}
