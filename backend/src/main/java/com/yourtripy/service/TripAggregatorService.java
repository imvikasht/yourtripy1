package com.yourtripy.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;

/**
 * =========================================================================
 *  CORE JAVA CONCURRENCY: CompletableFuture & Thread Pools
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Makes the app 3x faster by fetching 3 things at the exact same time!
 *
 *  🍳 THE RESTAURANT KITCHEN ANALOGY:
 *  Sequential (Slow):
 *   Chef cooks the Burger (takes 3 min) -> THEN fries the French Fries (takes 2 min) 
 *   -> THEN pours the Soda (takes 1 min). Total wait: 6 MINUTES!
 *
 *  Parallel with CompletableFuture (Fast):
 *   3 separate kitchen workers start at the exact same time:
 *   Worker 1 makes Burger, Worker 2 makes Fries, Worker 3 pours Soda.
 *   Total wait: only 3 MINUTES (the time of the longest task)!
 *
 *  🎓 COLLEGE SUBJECT: Core Java Concurrency (Java 8+) & Operating Systems Multithreading.
 * =========================================================================
 */
@Service
public class TripAggregatorService {

    // Thread pool with 10 background worker threads (like 10 kitchen staff members)
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // Simple record to hold our combined results
    public record AggregatedTripData(
        String destination,
        String aiItineraryJson,
        String weatherForecast,
        String railwayRouteStatus,
        long totalProcessingTimeMs
    ) {}

    /**
     * Fetches AI Itinerary, Weather, and Railway Status concurrently.
     */
    public CompletableFuture<AggregatedTripData> fetchAggregatedTrip(String destination, int days) {
        long startTime = System.currentTimeMillis();

        // 🧵 Thread 1: Calls Google Gemini AI to plan the itinerary
        CompletableFuture<String> aiTask = CompletableFuture.supplyAsync(() -> {
            simulateNetworkDelay(1200); // Simulates 1.2s AI thinking time
            return "{\"destination\": \"" + destination + "\", \"days\": " + days + ", \"status\": \"AI Generated Successfully\"}";
        }, threadPool);

        // 🧵 Thread 2: Calls Weather API for temperature & climate
        CompletableFuture<String> weatherTask = CompletableFuture.supplyAsync(() -> {
            simulateNetworkDelay(600);  // Simulates 0.6s Weather API response
            return "{\"temp\": \"22°C\", \"condition\": \"Sunny with pleasant breeze\"}";
        }, threadPool);

        // 🧵 Thread 3: Calls Indian Railways API for train availability
        CompletableFuture<String> railwayTask = CompletableFuture.supplyAsync(() -> {
            simulateNetworkDelay(800);  // Simulates 0.8s Railway route lookup
            return "{\"trainName\": \"Rajdhani Express\", \"status\": \"On Time\", \"delay\": \"0 mins\"}";
        }, threadPool);

        // 🤝 The Magic: CompletableFuture.allOf waits for all 3 tasks to finish in parallel!
        return CompletableFuture.allOf(aiTask, weatherTask, railwayTask)
            .thenApply(v -> {
                long duration = System.currentTimeMillis() - startTime;
                return new AggregatedTripData(
                    destination,
                    aiTask.join(),       // Get result from Thread 1
                    weatherTask.join(),  // Get result from Thread 2
                    railwayTask.join(),  // Get result from Thread 3
                    duration
                );
            });
    }

    private void simulateNetworkDelay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
