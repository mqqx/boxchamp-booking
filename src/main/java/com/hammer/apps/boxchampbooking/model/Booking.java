package com.hammer.apps.boxchampbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	@ManyToOne
	private User user;
	private DayOfWeek dayOfWeek;
	private ClassType classType;
	private LocalTime time;

}
