package ma.ensa.messagingservice.Feign;


import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("QUESTIONSERVICE")
public interface UserInterface {



}
