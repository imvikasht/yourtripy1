package com.yourtripy.patterns.factory;

/**
 * Core Java OOP: Abstraction & Polymorphism
 * Base class for all bookable travel entities.
 */
public abstract class BookingItem {
    protected final String bookingId;
    protected final String tripId;
    protected final double amount;

    public BookingItem(String bookingId, String tripId, double amount) {
        this.bookingId = bookingId;
        this.tripId = tripId;
        this.amount = amount;
    }

    public abstract String getCategoryName();
    public abstract boolean processBooking();

    public String getBookingId() { return bookingId; }
    public String getTripId() { return tripId; }
    public double getAmount() { return amount; }
}
