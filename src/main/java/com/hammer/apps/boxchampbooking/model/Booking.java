package com.hammer.apps.boxchampbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "dayOfWeek"})})
public class Booking {
	@Id
	@GeneratedValue
	private Integer id;

	@JsonIgnore
	@ManyToOne
	private User user;

	@NotNull
	private DayOfWeek dayOfWeek;

	@NotNull
	private ClassType classType;
	private LocalTime time;

}
