package dev.hmmr.apps.coursebooking.repository;

import dev.hmmr.apps.coursebooking.model.Booking;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.stream.Stream;

@Repository
public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
	Iterable<Booking> findAllByUserUsername(String username);

	Stream<Booking> findAllByDayOfWeekEquals(DayOfWeek dayOfWeek);
}
