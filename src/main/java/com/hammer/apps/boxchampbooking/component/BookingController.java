package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.model.Booking;
import com.hammer.apps.boxchampbooking.model.ClassType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("bookings")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
	static LocalTime EARLIEST_CLASS = LocalTime.of(6, 0);
	static LocalTime LATEST_CLASS = LocalTime.of(21, 0);
	static Collection<LocalTime> TIMES = new ArrayList<>();
	BookingService bookingService;

	@GetMapping
	public Iterable<Booking> getBookings(Principal principal) {
		return bookingService.findAllByUsername(principal.getName());
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
