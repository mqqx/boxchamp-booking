package dev.hmmr.apps.coursebooking.runner;

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
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String COURSE_ARGUMENT = "course";
    private static final String STARTS_AT_ARGUMENT = "startsAt";

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

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(singletonList(USERNAME));
        when(args.getOptionValues(PASSWORD)).thenReturn(singletonList(PASSWORD));
        when(args.getOptionValues(COURSE_ARGUMENT)).thenReturn(singletonList("OLYMPIC_WEIGHTLIFTING"));
        when(args.getOptionValues(STARTS_AT_ARGUMENT)).thenReturn(singletonList(startsAt));

        sut.run(args);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

        Booking expectedBooking = Booking.builder()
                .user(user)
                .course(Course.OLYMPIC_WEIGHTLIFTING)
                .startsAt(startsAt)
                .build();
        verify(boxChampAdapter).submitBooking(expectedBooking);
    }

    @Test
    @DisplayName("Should submit booking when course is sent as lowercase.")
    void runSubmitBookingWithUpperCasingCourse() throws CourseNotFoundException {
        ApplicationArguments args = mock(ApplicationArguments.class);

        String startsAt = "10:00";

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(singletonList(USERNAME));
        when(args.getOptionValues(PASSWORD)).thenReturn(singletonList(PASSWORD));
        when(args.getOptionValues(COURSE_ARGUMENT)).thenReturn(singletonList("olympic_weightlifting"));
        when(args.getOptionValues(STARTS_AT_ARGUMENT)).thenReturn(singletonList(startsAt));

        sut.run(args);

        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);

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

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(USERNAME);
    }

    @Test
    @DisplayName("Should throw exception when password argument is missing.")
    void runThrowsOnMissingPassword() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(singletonList(USERNAME));
        when(args.getOptionValues(PASSWORD)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(PASSWORD);
    }

    @Test
    @DisplayName("Should throw exception when course argument is missing.")
    void runThrowsOnMissingCourse() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(singletonList(USERNAME));
        when(args.getOptionValues(PASSWORD)).thenReturn(singletonList(PASSWORD));
        when(args.getOptionValues(COURSE_ARGUMENT)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(COURSE_ARGUMENT);
    }

    @Test
    @DisplayName("Should throw exception when starts at argument is missing.")
    void runThrowsOnMissingStartsAt() {
        ApplicationArguments args = mock(ApplicationArguments.class);

        when(args.getSourceArgs()).thenReturn(new String[]{""});
        when(args.getOptionValues(USERNAME)).thenReturn(singletonList(USERNAME));
        when(args.getOptionValues(PASSWORD)).thenReturn(singletonList(PASSWORD));
        when(args.getOptionValues(COURSE_ARGUMENT)).thenReturn(singletonList("OLYMPIC_WEIGHTLIFTING"));
        when(args.getOptionValues(STARTS_AT_ARGUMENT)).thenReturn(emptyList());

        Assertions.assertThatExceptionOfType(MissingArgumentException.class)
                .isThrownBy(() -> sut.run(args))
                .withMessageContaining(STARTS_AT_ARGUMENT);
    }
}
