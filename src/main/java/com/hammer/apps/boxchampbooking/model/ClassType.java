package com.hammer.apps.boxchampbooking.model;

public enum ClassType {
	FITNESS("Fitness"),
	GYMNASTICS("Gymnastics"),
	OLYMPIC_WEIGHTLIFTING("Olympic Weightlifting"),
	ENDURANCE("Endurance");

	private String name;

	ClassType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
