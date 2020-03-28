package dev.hmmr.apps.coursebooking.scheduler;

import dev.hmmr.apps.coursebooking.adapter.boxchamp.BoxChampAdapter;
import dev.hmmr.apps.coursebooking.exception.CourseNotFoundException;
import dev.hmmr.apps.coursebooking.model.Booking;
import dev.hmmr.apps.coursebooking.repository.BookingRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingSchedulerTest {

    // The 1.1.2019 is a Tuesday
    final Clock clock = Clock.fixed(LocalDateTime.of(2019, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    BookingScheduler sut;
    @Mock
    BoxChampAdapter boxChampAdapter;
    @Mock
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        sut = new BookingScheduler(boxChampAdapter, bookingRepository, clock);
    }

    @Test
    @DisplayName("Book classes should book all passed bookings")
    void triggerBookingClasses(CapturedOutput output) throws CourseNotFoundException {
        Booking booking = Booking.builder().id(1).build();
        Booking anotherBooking = Booking.builder().id(2).build();
        Stream<Booking> bookings = Stream.of(booking, anotherBooking);
        when(bookingRepository.findAllByDayOfWeekEquals(DayOfWeek.TUESDAY)).thenReturn(bookings);
        when(boxChampAdapter.submitBooking(any(Booking.class))).thenReturn(true);

        sut.bookClasses();

        verify(boxChampAdapter).submitBooking(booking);
        verify(boxChampAdapter).submitBooking(anotherBooking);

        assertThat(output).contains("Booking Scheduler finished, submitted 2 bookings.");
    }

    @Test
    @DisplayName("Book classes should book all passed bookings except the booking with an exception")
    void testBookingClassesBookingThrowsClassIdNotFoundException(CapturedOutput output) throws CourseNotFoundException {
        Booking booking = Booking.builder().id(1).build();
        Booking otherBooking = Booking.builder().id(2).build();
        Booking anotherBooking = Booking.builder().id(3).build();
        Stream<Booking> bookings = Stream.of(booking, otherBooking, anotherBooking);
        when(bookingRepository.findAllByDayOfWeekEquals(DayOfWeek.TUESDAY)).thenReturn(bookings);
        when(boxChampAdapter.submitBooking(booking)).thenReturn(true);
        when(boxChampAdapter.submitBooking(otherBooking)).thenThrow(new CourseNotFoundException(otherBooking));
        when(boxChampAdapter.submitBooking(anotherBooking)).thenReturn(true);

        sut.bookClasses();

        verify(boxChampAdapter).submitBooking(booking);
        verify(boxChampAdapter).submitBooking(otherBooking);
        verify(boxChampAdapter).submitBooking(anotherBooking);

        assertThat(output).contains(MessageFormat.format("Course with booking {0} not found.", otherBooking));
        assertThat(output).contains("Booking Scheduler finished, submitted 2 bookings.");
    }
}
