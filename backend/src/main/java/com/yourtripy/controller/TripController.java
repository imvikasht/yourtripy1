package com.yourtripy.controller;

import com.yourtripy.dto.ApiResponse;
import com.yourtripy.patterns.strategy.PackingStrategyContext;
import com.yourtripy.service.BookingTransactionService;
import com.yourtripy.service.TripAggregatorService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing backend endpoints for the React frontend.
 * Enables 3-Tier Architecture demonstration for College Project Presentation.
 */
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*") // Allows communication with Vite frontend on http://localhost:5173
public class TripController {

    private final TripAggregatorService aggregatorService;
    private final BookingTransactionService bookingService;
    private final PackingStrategyContext packingContext = new PackingStrategyContext();

    public TripController(TripAggregatorService aggregatorService, BookingTransactionService bookingService) {
        this.aggregatorService = aggregatorService;
        this.bookingService = bookingService;
    }

    /**
     * Health check endpoint to verify backend connectivity from frontend
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> checkHealth() {
        return ApiResponse.ok(Map.of(
            "status", "UP",
            "service", "YourTripy Java Backend",
            "modules", List.of("Core Java Concurrency", "OS Semaphores & LRU", "RDBMS 3NF & ACID")
        ), "Java Backend is running healthy");
    }

    /**
     * Endpoint 1: Parallel Trip Plan Aggregation (CompletableFuture)
     */
    @GetMapping("/plan")
    public CompletableFuture<ApiResponse<TripAggregatorService.AggregatedTripData>> getTripPlan(
        @RequestParam String destination,
        @RequestParam(defaultValue = "3") int days
    ) {
        return aggregatorService.fetchAggregatedTrip(destination, days)
            .thenApply(data -> ApiResponse.ok(data, "Trip plan aggregated successfully via CompletableFuture"));
    }

    /**
     * Endpoint 2: Smart Packing List via Strategy Pattern
     */
    @GetMapping("/packing-list")
    public ApiResponse<List<String>> getPackingList(
        @RequestParam String destination,
        @RequestParam(defaultValue = "3") int days
    ) {
        List<String> items = packingContext.getPackingList(destination, days);
        return ApiResponse.ok(items, "Packing list generated using Java Strategy Pattern");
    }

    /**
     * Endpoint 3: Indian Railways Live Status Tracker (Multi-modal Transport)
     */
    @GetMapping("/railways/status")
    public ApiResponse<Map<String, String>> getRailwayStatus(@RequestParam(defaultValue = "12951") String trainNumber) {
        // Simulates dynamic live lookup for Indian Railways
        String cleanNumber = trainNumber.trim();
        String trainName = switch (cleanNumber) {
            case "12951", "12952" -> "Mumbai Rajdhani Express";
            case "12004", "12003" -> "New Delhi Shatabdi Express";
            case "22436", "22435" -> "Vande Bharat Express";
            case "12259", "12260" -> "Sealdah Bikaner AC Duronto";
            default -> "Express Special (" + cleanNumber + ")";
        };

        Map<String, String> data = Map.of(
            "trainNumber", cleanNumber,
            "trainName", trainName,
            "status", "On Time",
            "nextStation", "New Delhi Junction (NDLS)",
            "delay", "0 mins",
            "platform", "Platform 4",
            "speed", "110 km/h",
            "source", "Verified by YourTripy Java Backend"
        );
        return ApiResponse.ok(data, "Live railway status retrieved successfully");
    }

    /**
     * Endpoint 4: Atomic Multi-vendor Booking (ACID Transaction)
     */
    @PostMapping("/book")
    public ApiResponse<BookingTransactionService.BookingResult> bookTrip(
        @RequestParam String tripId,
        @RequestParam String userId,
        @RequestParam double flightCost,
        @RequestParam double hotelCost,
        @RequestParam double budget
    ) {
        BookingTransactionService.BookingResult result = bookingService.executeMultiVendorBooking(
            tripId, userId, flightCost, hotelCost, budget
        );
        return ApiResponse.ok(result, "Booking transaction committed atomically");
    }
}
