package app2;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
public class App2Main {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World From 2!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App2Main.class, args);
    }
}