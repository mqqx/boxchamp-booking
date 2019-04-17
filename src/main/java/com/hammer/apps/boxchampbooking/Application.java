package com.hammer.apps.boxchampbooking;

import com.hammer.apps.boxchampbooking.component.AuthenticationService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class Application {

	private static final String ARGUMENT_USERNAME = "username";
	private static final String ARGUMENT_PASSWORD = "password";
	private static final String ARGUMENT_CLASS_TYPE = "classType";

	private final BookingService bookingService;
	private final AuthenticationService authenticationService;

	public Application(BookingService bookingService, AuthenticationService authenticationService) {
		this.bookingService = bookingService;
		this.authenticationService = authenticationService;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args).close();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public ApplicationRunner run(RestTemplate restTemplate) {
		return args -> {
			String username = validateAndGetArgumentValue(args, ARGUMENT_USERNAME);
			String password = validateAndGetArgumentValue(args, ARGUMENT_PASSWORD);
			// convenient toUpperCase call to be more flexible with argument values
			ClassType classType = ClassType.valueOf(validateAndGetArgumentValue(args, ARGUMENT_CLASS_TYPE).toUpperCase());

			User user = new User();
			user.setUsername(username);
			user.setPassword(password);

			HttpEntity authorizedEntity = authenticationService.login(restTemplate, user);

			Booking booking = new Booking(user, classType);
			bookingService.bookClass(restTemplate, booking, authorizedEntity);
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
