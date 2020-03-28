package dev.hmmr.apps.coursebooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
@ToString(exclude = "password")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	Integer id;

	@NotNull
	@Column(unique = true)
	String username;

	@JsonIgnore
	String password;

	// TODO play around with (lazy) loading bookings
//	@OneToMany(targetEntity = Booking.class, mappedBy = "booking", cascade = CascadeType.ALL)
//	List<Booking> bookings = new ArrayList<>();
}
