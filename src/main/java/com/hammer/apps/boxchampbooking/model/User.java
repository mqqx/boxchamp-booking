package com.hammer.apps.boxchampbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@ToString(exclude = "password")
public class User {

	@Id
	@GeneratedValue
	private int userId;
	private String username;
	@JsonIgnore
	private String password;
	private boolean enabled;
}