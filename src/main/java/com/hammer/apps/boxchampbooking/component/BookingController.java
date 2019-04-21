package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.model.Booking;
import com.hammer.apps.boxchampbooking.model.ClassType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("bookings")
public class BookingController {
	private static final LocalTime EARLIEST_CLASS = LocalTime.of(6, 0);
	private static final LocalTime LATEST_CLASS = LocalTime.of(21, 0);
	private static final Collection<LocalTime> TIMES = new ArrayList<>();
	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public Iterable<Booking> getBookings() {
		return bookingService.findAll();
	}

	@PostMapping
	public Booking save(@Valid @RequestBody Booking booking) {
		return bookingService.save(booking);
	}

	@PutMapping
	public Booking update(@Valid @RequestBody Booking booking) {
		return save(booking);
	}

	@DeleteMapping
	public void delete(@Valid @RequestBody Booking booking) {
		bookingService.delete(booking);
	}


	@GetMapping("daysOfWeek")
	public DayOfWeek[] getDaysOfWeek() {
		return DayOfWeek.values();
	}

	@GetMapping("classTypes")
	public ClassType[] getClassTypes() {
		return ClassType.values();
	}

	@GetMapping("times")
	public Collection<LocalTime> getTimes() {

		if (TIMES.isEmpty()) {
			LocalTime currentClass = EARLIEST_CLASS;

			do {
				TIMES.add(currentClass);
				currentClass = currentClass.plusMinutes(30);
			}
			while (currentClass.isBefore(LATEST_CLASS));
		}

		return TIMES;
	}
}
