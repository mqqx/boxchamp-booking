package com.hammer.apps.boxchampbooking.model;

public enum ClassType {
	ENDURANCE("Endurance"),
	FITNESS("Fitness"),
	GYMNASTICS("Gymnastics"),
	MASTERS("Masters"),
	MOBILITY("Mobility"),
	OLYMPIC_WEIGHTLIFTING("Olympic Weightlifting"),
	OPEN_BOX("Open Box"),
	PERFORMANCE("Performance"),
	RUNNING("Running");

	private String name;

	ClassType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
