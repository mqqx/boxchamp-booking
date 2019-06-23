package com.hammer.apps.boxchampbooking;

import com.hammer.apps.boxchampbooking.component.BookingService;
import com.hammer.apps.boxchampbooking.exception.MissingArgumentException;
import com.hammer.apps.boxchampbooking.model.Booking;
import com.hammer.apps.boxchampbooking.model.ClassType;
import com.hammer.apps.boxchampbooking.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.List;

@SpringBootApplication
public class Application {

	private static final String ARGUMENT_USERNAME = "username";
	private static final String ARGUMENT_PASSWORD = "password";
	private static final String ARGUMENT_CLASS_TYPE = "classType";
	private static final String ARGUMENT_TIME = "time";

	private final BookingService bookingService;

	public Application(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Application.class, args);

		// close context as the only use case for arguments would be in the case of direct booking
		if (args.length != 0) {
			configurableApplicationContext.close();
		}
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public ApplicationRunner run(RestTemplate restTemplate) {
		return args -> {
			if (args.getSourceArgs().length != 0) {
				String username = validateAndGetArgumentValue(args, ARGUMENT_USERNAME);
				String password = validateAndGetArgumentValue(args, ARGUMENT_PASSWORD);
				// convenient toUpperCase call to be more flexible with argument values
				ClassType classType = ClassType.valueOf(validateAndGetArgumentValue(args, ARGUMENT_CLASS_TYPE).toUpperCase());
				LocalTime time = LocalTime.parse(validateAndGetArgumentValue(args, ARGUMENT_TIME));

				User user = new User();
				user.setUsername(username);
				user.setPassword(password);

				Booking booking = new Booking();
				booking.setUser(user);
				booking.setClassType(classType);
				booking.setTime(time);
				bookingService.authenticateAndBookClass(restTemplate, booking);
			}
		};
	}

	private String validateAndGetArgumentValue(ApplicationArguments args, String argumentKey) {
		String argumentValue;
		String errorMessage = "Missing required argument " + argumentKey;

		List<String> userList = args.getOptionValues(argumentKey);

		if (CollectionUtils.isEmpty(userList)) {
			throw new MissingArgumentException(errorMessage);
		}

		argumentValue = userList.stream().findFirst().orElseThrow(() -> new MissingArgumentException(errorMessage + " value"));
		return argumentValue;
	}

}
