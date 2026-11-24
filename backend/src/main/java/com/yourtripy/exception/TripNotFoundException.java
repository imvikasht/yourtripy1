package com.yourtripy.exception;

/**
 * Core Java Concept: Custom Domain Exceptions
 */
public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String tripId) {
        super("Trip with ID '" + tripId + "' was not found.");
    }
}
