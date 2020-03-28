package dev.hmmr.apps.coursebooking.exception;

import dev.hmmr.apps.coursebooking.model.Booking;

import static java.text.MessageFormat.format;

public class CourseNotFoundException extends Exception {
    public CourseNotFoundException(Booking booking) {
        super(format("Course with booking {0} not found.", booking));
    }
}
