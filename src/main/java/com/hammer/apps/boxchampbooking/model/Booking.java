package com.hammer.apps.boxchampbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	private User user;
	private DayOfWeek dayOfWeek;
	private ClassType type;
	private LocalTime time;

}
