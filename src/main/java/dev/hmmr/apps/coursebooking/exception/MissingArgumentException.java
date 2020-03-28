package dev.hmmr.apps.coursebooking.exception;


import static java.text.MessageFormat.format;

public class MissingArgumentException extends IllegalArgumentException {
    public MissingArgumentException(String missingArgumentKey) {
        super(format("Missing argument {0}", missingArgumentKey));
    }
}
