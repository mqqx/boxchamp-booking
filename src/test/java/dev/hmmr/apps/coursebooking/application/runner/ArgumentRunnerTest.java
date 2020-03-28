package dev.hmmr.apps.coursebooking.application.runner;

import dev.hmmr.apps.coursebooking.adapter.boxchamp.BoxChampAdapter;
import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.exception.MissingArgumentException;
import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.model.Course;
import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ArgumentRunnerTest {
    @Mock
    BoxChampAdapter boxChampAdapter;

    ArgumentRunner sut;

    @BeforeEach
    void setUp() {
        sut = new ArgumentRunner(boxChampAdapter);
    }

    @Test
    @DisplayName("Should do nothing when application arguments are empty.")
    void run() throws CourseNotFoundException {
        ApplicationArguments args = mock(ApplicationArguments.class);
        when(args.getSourceArgs()).thenReturn(new String[]{});

        sut.run(args);

        verifyNoInteractions(boxChampAdapter);
    }

    @Test
    @DisplayName("Should submit booking when all required application arguments are set.")
    void runSubmitBooking() throws CourseNotFoundException {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String startsAt = "10:00";
        String username = "username";
        String password = "password";
        String courseArgument = "course";
        String startsAtArgument = "startsAt";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(singletonList(username));
        when(args.getOptionValues(password)).thenReturn(singletonList(password));
        when(args.getOptionValues(courseArgument)).thenReturn(singletonList("OLYMPIC_WEIGHTLIFTING"));
        when(args.getOptionValues(startsAtArgument)).thenReturn(singletonList(startsAt));

        sut.run(args);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Booking expectedBooking = Booking.builder()
                .user(user)
                .course(Course.OLYMPIC_WEIGHTLIFTING)
                .startsAt(startsAt)
                .build();
        verify(boxChampAdapter).submitBooking(expectedBooking);
    }

    @Test
    @DisplayName("Should throw exception when username argument is missing.")
    void runThrowsOnMissingUsername() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String username = "username";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(username);
    }

    @Test
    @DisplayName("Should throw exception when password argument is missing.")
    void runThrowsOnMissingPassword() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String username = "username";
        String password = "password";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(singletonList(username));
        when(args.getOptionValues(password)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(password);
    }

    @Test
    @DisplayName("Should throw exception when course argument is missing.")
    void runThrowsOnMissingCourse() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String username = "username";
        String password = "password";
        String courseArgument = "course";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(singletonList(username));
        when(args.getOptionValues(password)).thenReturn(singletonList(password));
        when(args.getOptionValues(courseArgument)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(courseArgument);
    }

    @Test
    @DisplayName("Should throw exception when starts at argument is missing.")
    void runThrowsOnMissingStartsAt() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String username = "username";
        String password = "password";
        String courseArgument = "course";
        String startsAtArgument = "startsAt";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(singletonList(username));
        when(args.getOptionValues(password)).thenReturn(singletonList(password));
        when(args.getOptionValues(courseArgument)).thenReturn(singletonList("OLYMPIC_WEIGHTLIFTING"));
        when(args.getOptionValues(startsAtArgument)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(startsAtArgument);
    }

    @Test
    @DisplayName("Should submit booking when course is sent as lowercase.")
    void runSubmitBookingWithUpperCasingCourse() throws CourseNotFoundException {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String startsAt = "10:00";
        String username = "username";
        String password = "password";
        String courseArgument = "course";
        String startsAtArgument = "startsAt";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(username)).thenReturn(singletonList(username));
        when(args.getOptionValues(password)).thenReturn(singletonList(password));
        when(args.getOptionValues(courseArgument)).thenReturn(singletonList("olympic_weightlifting"));
        when(args.getOptionValues(startsAtArgument)).thenReturn(singletonList(startsAt));

        sut.run(args);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Booking expectedBooking = Booking.builder()
                .user(user)
                .course(Course.OLYMPIC_WEIGHTLIFTING)
                .startsAt(startsAt)
                .build();
        verify(boxChampAdapter).submitBooking(expectedBooking);
    }
}
