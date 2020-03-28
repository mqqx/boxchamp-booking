package dev.hmmr.apps.coursebooking.runner;

import dev.hmmr.apps.coursebooking.adapter.boxchamp.BoxChampAdapter;
import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.exception.MissingArgumentException;
import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.model.Course;
import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ArgumentRunner implements ApplicationRunner {
    private static final String ARGUMENT_USERNAME = "username";
    private static final String ARGUMENT_PASSWORD = "password";
    private static final String ARGUMENT_COURSE = "course";
    private static final String ARGUMENT_STARTS_AT = "startsAt";

    BoxChampAdapter boxChampAdapter;

    @Override
    public void run(ApplicationArguments args) throws CourseNotFoundException {
        if (args.getSourceArgs().length > 0) {
            String username = validateAndGetArgumentValue(args, ARGUMENT_USERNAME);
            String password = validateAndGetArgumentValue(args, ARGUMENT_PASSWORD);
            // convenient toUpperCase call to be more flexible with argument values
            Course course = Course.valueOf(validateAndGetArgumentValue(args, ARGUMENT_COURSE).toUpperCase());
            String startsAt = validateAndGetArgumentValue(args, ARGUMENT_STARTS_AT);

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            Booking booking = Booking.builder()
                    .user(user)
                    .course(course)
                    .startsAt(startsAt)
                    .build();
            boxChampAdapter.submitBooking(booking);
        }
    }

    private String validateAndGetArgumentValue(ApplicationArguments args, String argumentKey) {
        final String argumentValue;
        List<String> values = args.getOptionValues(argumentKey);

        if (CollectionUtils.isEmpty(values)) {
            throw new MissingArgumentException(argumentKey);
        }

        argumentValue = values.stream()
                .findFirst()
                .orElseThrow(() -> new MissingArgumentException(argumentKey));

        return argumentValue;
    }
}
