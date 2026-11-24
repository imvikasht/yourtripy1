package com.yourtripy.patterns.factory;

/**
 * Factory pattern creating the proper polymorphic booking item at runtime.
 */
public class TransportBookingFactory {

    public enum BookingType {
        HOTEL, FLIGHT, TRAIN, BUS
    }

    public static BookingItem createBooking(BookingType type, String bookingId, String tripId, double amount, String referenceNo) {
        return switch (type) {
            case TRAIN -> new TrainBooking(bookingId, tripId, amount, referenceNo);
            case FLIGHT -> new FlightBooking(bookingId, tripId, amount, referenceNo);
            case HOTEL -> new HotelBooking(bookingId, tripId, amount, referenceNo);
            case BUS -> new BusBooking(bookingId, tripId, amount, referenceNo);
        };
    }
}

class TrainBooking extends BookingItem {
    private final String pnr;
    public TrainBooking(String id, String tripId, double amount, String pnr) {
        super(id, tripId, amount);
        this.pnr = pnr;
    }
    @Override public String getCategoryName() { return "Indian Railways Ticket"; }
    @Override public boolean processBooking() {
        System.out.println("Processing IRCTC train booking for PNR: " + pnr);
        return true;
    }
}

class FlightBooking extends BookingItem {
    private final String ticketNumber;
    public FlightBooking(String id, String tripId, double amount, String ticketNumber) {
        super(id, tripId, amount);
        this.ticketNumber = ticketNumber;
    }
    @Override public String getCategoryName() { return "Airline Flight Ticket"; }
    @Override public boolean processBooking() {
        System.out.println("Processing Airline booking for Ticket: " + ticketNumber);
        return true;
    }
}

class HotelBooking extends BookingItem {
    private final String reservationId;
    public HotelBooking(String id, String tripId, double amount, String reservationId) {
        super(id, tripId, amount);
        this.reservationId = reservationId;
    }
    @Override public String getCategoryName() { return "Hotel Room Reservation"; }
    @Override public boolean processBooking() {
        System.out.println("Processing Hotel reservation: " + reservationId);
        return true;
    }
}

class BusBooking extends BookingItem {
    private final String seatNumber;
    public BusBooking(String id, String tripId, double amount, String seatNumber) {
        super(id, tripId, amount);
        this.seatNumber = seatNumber;
    }
    @Override public String getCategoryName() { return "Intercity Bus Seat"; }
    @Override public boolean processBooking() {
        System.out.println("Processing Bus booking for seat: " + seatNumber);
        return true;
    }
}
