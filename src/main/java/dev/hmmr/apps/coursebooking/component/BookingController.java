package dev.hmmr.apps.coursebooking.component;

import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.model.Course;
import dev.hmmr.apps.coursebooking.repository.BookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
    //TODO make configurable
    private static final LocalTime EARLIEST_COURSE = LocalTime.of(6, 0);
    private static final LocalTime LATEST_COURSE = LocalTime.of(21, 0);
    private static final Collection<LocalTime> TIMES;
    BookingRepository bookingRepository;

    static {
        TIMES = new HashSet<>();
        LocalTime currentCourse = EARLIEST_COURSE;

        do {
            TIMES.add(currentCourse);
            currentCourse = currentCourse.plusMinutes(30);
        }
        while (currentCourse.isBefore(LATEST_COURSE));
    }

    @GetMapping
    public List<Booking> getBookings(Principal principal) {
        String username = principal.getName();
        return bookingRepository.findAllByUserUsername(username);
    }

    @GetMapping("daysOfWeek")
    public DayOfWeek[] getDaysOfWeek() {
        return DayOfWeek.values();
    }

    @GetMapping("courses")
    public Course[] getCourses() {
        return Course.values();
    }

    @GetMapping("times")
    public Collection<LocalTime> getTimes() {
        return TIMES;
    }
}
