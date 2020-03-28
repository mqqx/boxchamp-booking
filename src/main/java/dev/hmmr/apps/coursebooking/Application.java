package dev.hmmr.apps.coursebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Application.class, args);

		// close context as the only use case for arguments would be in the case of direct booking
		if (args.length != 0) {
			configurableApplicationContext.close();
		}
	}
}
