package com.hammer.apps.boxchampbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	private User user;
	private DayOfWeek dayOfWeek;
	private ClassType type;
	private LocalTime time;

}
