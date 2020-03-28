package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.model.Course;
import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BoxChampAdapterTest {

    @Mock
    BoxChampAuthenticationService boxChampAuthenticationService;

    @Mock
    BoxChampHttpClient boxChampHttpClient;
    Clock clock = Clock.fixed(LocalDateTime.of(2019, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());

    BoxChampAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new BoxChampAdapter(boxChampAuthenticationService, boxChampHttpClient, clock);
    }

    @Test
    @DisplayName("Should submit a booking successful to boxchamp")
    void submitBookingSuccessful() throws IOException, CourseNotFoundException {
        HttpEntity<HttpHeaders> authorizedEntity = new HttpEntity<>(new HttpHeaders());
        User testUser = setUpTestUser();
        when(boxChampAuthenticationService.authenticate(testUser)).thenReturn(authorizedEntity);

        String response = createResponseString();
        doReturn(ResponseEntity.of(Optional.of(response))).when(boxChampHttpClient).get("classlist?date=2019-01-15", authorizedEntity);
        doReturn(ResponseEntity.ok("success")).when(boxChampHttpClient).get("classlist/athlete/book/2977005", authorizedEntity);
        Booking booking = setUpTestBooking(testUser);

        boolean isSuccessful = sut.submitBooking(booking);

        assertThat(isSuccessful).isTrue();
    }

    @Test
    @DisplayName("Should return false when submit to boxchamp fails")
    void submitBookingFailed() throws IOException, CourseNotFoundException {
        HttpEntity<HttpHeaders> authorizedEntity = new HttpEntity<>(new HttpHeaders());
        User testUser = setUpTestUser();
        when(boxChampAuthenticationService.authenticate(eq(testUser))).thenReturn(authorizedEntity);

        String response = createResponseString();
        doReturn(ResponseEntity.of(Optional.of(response))).when(boxChampHttpClient).get("classlist?date=2019-01-15", authorizedEntity);
        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed");
        doReturn(responseEntity).when(boxChampHttpClient).get("classlist/athlete/book/2977005", authorizedEntity);
        Booking booking = setUpTestBooking(testUser);

        boolean isSuccessful = sut.submitBooking(booking);

        assertThat(isSuccessful).isFalse();
    }

    @Test
    @DisplayName("Should throw ClassIdNotFoundException when a class could not be found from the given booking fields")
    void submitBookingThrowsClassIdNotFoundException() {
        HttpEntity<HttpHeaders> authorizedEntity = new HttpEntity<>(new HttpHeaders());
        User testUser = setUpTestUser();
        when(boxChampAuthenticationService.authenticate(eq(testUser))).thenReturn(authorizedEntity);

        Booking booking = setUpTestBooking(testUser);

        assertThatExceptionOfType(CourseNotFoundException.class)
                .isThrownBy(() -> sut.submitBooking(booking));
    }

    private String createResponseString() throws IOException {
        File resourcesDirectory = new File("src/test/resources");
        String filePath = resourcesDirectory.getAbsolutePath() + "/class-search-masters-response.html";
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    private User setUpTestUser() {
        User testUser = new User();
        testUser.setUsername("username");
        testUser.setPassword("password");
        return testUser;
    }

    private Booking setUpTestBooking(User testUser) {
        return Booking.builder()
                .course(Course.MASTERS)
                .user(testUser)
                .startsAt("09:00")
                .build();
    }
}
