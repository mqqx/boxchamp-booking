package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.model.Booking;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(BoxChampProperties.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoxChampAdapter {
    BoxChampAuthenticationService boxChampAuthenticationService;
    BoxChampHttpClient boxChampHttpClient;
    BoxChampProperties boxChampProperties;
    Clock clock;

    public boolean submitBooking(Booking booking) throws CourseNotFoundException {
        HttpEntity<HttpHeaders> authorizedEntity = boxChampAuthenticationService.authenticate(booking.getUser());

        String courseId = selectCourseId(authorizedEntity, booking);
        String bookingPath = boxChampProperties.getBookingPath();
        String bookingPathWithCourseId = format(bookingPath, courseId);

        ResponseEntity<String> response = boxChampHttpClient.get(bookingPathWithCourseId, authorizedEntity);

        return response.getStatusCode().is2xxSuccessful();
    }

    private String selectCourseId(HttpEntity<HttpHeaders> authorizedEntity, Booking booking) throws CourseNotFoundException {
        ResponseEntity<String> courseSearchHttpResponse = boxChampHttpClient.get(buildCourseSearchUrl(), authorizedEntity);

        String httpResponse = String.valueOf(courseSearchHttpResponse);

        return extractCourseId(booking, httpResponse)
                .orElseThrow(() -> new CourseNotFoundException(booking));
    }

    private Optional<String> extractCourseId(Booking booking, String responseHtml) {
        Pattern pattern = buildCourseIdExtractPattern(booking);
        Matcher matcher = pattern.matcher(responseHtml);

        return matcher.find() ?
                Optional.of(matcher.group(2).trim()) :
                Optional.empty();
    }

    private Pattern buildCourseIdExtractPattern(Booking booking) {
        String course = booking.getCourse().getName();
        String listRegex = boxChampProperties.getListRegex();
        String courseIdExtractRegex = format(listRegex, booking.getStartsAt(), course);
        return Pattern.compile(courseIdExtractRegex, Pattern.DOTALL);
    }

    private String buildCourseSearchUrl() {
        int weeksInAdvance = boxChampProperties.getWeeksInAdvance();
        LocalDate weeksInAdvanceFromToday = LocalDate
                .now(clock)
                .plusWeeks(weeksInAdvance);
        String formattedDate = weeksInAdvanceFromToday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String listPath = boxChampProperties.getListPath();
        return format(listPath, formattedDate);
    }

}
