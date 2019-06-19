package Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("service")
public class ServiceController {
    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);
    private Map<String, User> users = new ConcurrentHashMap<>();


    /**
     * curl -X POST -i localhost:8080/service/login -d "name=Egor&surname=Sevastianov&birthday=11.05.1999&email=egor.egeg@yandex.ru&password=12345"
     */
    @RequestMapping(
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name,@RequestParam("surname") String surname,
                                        @RequestParam("birthday") String birthday,@RequestParam("email") String email,
                                        @RequestParam("password") String password) {
        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (name.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        if (users.containsKey(email)) {
            return ResponseEntity.badRequest().body("Already logged in:(");
        }
        User user = new User();
        user.setBirthday(birthday);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setSurname(surname);
        users.put(email,user);
        log.info("[" + email + "] logined");
        return ResponseEntity.ok().build();
    }



    /**
     * curl -X POST -i localhost:8080/service/remove -d "email=egor.egeg@yandex.ru"
     */
    @RequestMapping(
            path = "remove",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity remove(@RequestParam("email") String email) {
        if(!(users.containsKey(email))){
            return ResponseEntity.badRequest().body("not logged in");
        }
        users.remove(email);
        log.info("[" + email + "] removed");
        return ResponseEntity.ok().build();
    }
    /**
     * curl -i localhost:8080/service/egor.egeg@yandex.ru
     */
    @GetMapping("/{email}")
    @ResponseBody
    public String email(@PathVariable String email) {
        if(!(users.containsKey(email))){
            return "email not found";
        }
        String responseBody = " Name: " + users.get(email).getName() +
        ".      Surname: " + users.get(email).getSurname() + ".      Birthday: " + users.get(email).getBirthday() + ".     Email: " +
        users.get(email).getEmail() + "." ;

        return responseBody;
    }



}
