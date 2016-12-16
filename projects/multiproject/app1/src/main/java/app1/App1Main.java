package app1;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app1")
public class App1Main {

	private String someProperty;

	public String getSomeProperty() {
		return someProperty;
	}

	public void setSomeProperty(String someProperty) {
		this.someProperty = someProperty;
	}

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World, from App1. PropertyValue=" + someProperty + ".";
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(App1Main.class, args);
	}
}