package com.hammer.apps.boxchampbooking.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue
	private int userId;
	private String username;
	private String password;
	private boolean enabled;
}