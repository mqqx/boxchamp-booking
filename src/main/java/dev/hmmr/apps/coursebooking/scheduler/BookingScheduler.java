package dev.hmmr.apps.coursebooking.scheduler;

import dev.hmmr.apps.coursebooking.adapter.boxchamp.BoxChampAdapter;
import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.repository.BookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingScheduler {

    BoxChampAdapter boxChampAdapter;
    BookingRepository bookingRepository;
    Clock clock;

    // Make with config and retry
    @Scheduled(cron = "${scheduled.cron.booking:0 1 0 * * *}")
    public void bookClasses() {
        log.info("Booking Scheduler started.");
        DayOfWeek dayOfWeek = LocalDate.now(clock).getDayOfWeek();
        Stream<Booking> currentDayOfWeekBookings = bookingRepository.findAllByDayOfWeekEquals(dayOfWeek);
        long createdEntries = currentDayOfWeekBookings.map(booking -> {
            try {
                // TODO extend to use multiple adapters
                boolean isSuccessful = boxChampAdapter.submitBooking(booking);
                if (!isSuccessful) {
                    log.error("Processing of booking {} failed, booking could not be submitted.", booking);
                }
                return isSuccessful;
            } catch (CourseNotFoundException e) {
                log.error(e.getMessage());
                return false;
            }
        }).filter(result -> result).count();
        log.info("Booking Scheduler finished, submitted {} bookings.", createdEntries);
    }
}
