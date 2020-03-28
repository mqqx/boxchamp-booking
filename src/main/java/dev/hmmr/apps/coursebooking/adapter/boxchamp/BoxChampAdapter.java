package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.model.Booking;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoxChampAdapter {

    //TODO make configurable
    private static final int WEEKS_BOOKING_SHOULD_BE_DONE_IN_ADVANCE = 2;
    private static final String COURSE_BOOKING_PATH = "classlist/athlete/book/";
    private static final String GREEDY_DOT_REGEX = ".*?";
    private static final String GREEDY_COURSE_LIST_REGEX = ".{0,200}?classlist/(athlete/view|home/progress_tooltip)/(" + GREEDY_DOT_REGEX + ")\"";
    private static final String COURSE_LIST_PATH = "classlist?date=";

    BoxChampAuthenticationService boxChampAuthenticationService;
    BoxChampHttpClient boxChampHttpClient;
    Clock clock;

    public boolean submitBooking(Booking booking) throws CourseNotFoundException {
        HttpEntity<HttpHeaders> authorizedEntity = boxChampAuthenticationService.authenticate(booking.getUser());

        String bookingPath = COURSE_BOOKING_PATH + selectCourseId(authorizedEntity, booking);

        ResponseEntity<String> response = boxChampHttpClient.get(bookingPath, authorizedEntity);

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
        return Pattern.compile(booking.getStartsAt() + GREEDY_DOT_REGEX + course + GREEDY_COURSE_LIST_REGEX, Pattern.DOTALL);
    }

    private String buildCourseSearchUrl() {
        LocalDate twoWeeksFromToday = LocalDate.now(clock)
                .plusWeeks(WEEKS_BOOKING_SHOULD_BE_DONE_IN_ADVANCE);
        String formattedDate = twoWeeksFromToday.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return COURSE_LIST_PATH + formattedDate;
    }

}
