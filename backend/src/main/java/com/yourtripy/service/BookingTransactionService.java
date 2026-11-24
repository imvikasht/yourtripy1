package com.yourtripy.service;

import com.yourtripy.exception.BudgetExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * RDBMS Concept: ACID Transactions & Concurrency Isolation
 * Demonstrates:
 *  1. Atomicity: Multi-vendor booking commits completely or rolls back on any exception.
 *  2. Isolation: Isolation.READ_COMMITTED prevents dirty reads.
 *  3. Propagation: REQUIRED ensures participation in existing transaction.
 */
@Service
public class BookingTransactionService {

    public record BookingResult(String bookingId, boolean confirmed, double totalCharged) {}

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = Exception.class
    )
    public BookingResult executeMultiVendorBooking(String tripId, String userId, double flightCost, double hotelCost, double userBudget) {
        double totalCost = flightCost + hotelCost;

        // Consistency check: Enforce domain rule
        if (totalCost > userBudget) {
            // Triggers automatic transaction ROLLBACK
            throw new BudgetExceededException(totalCost, userBudget);
        }

        // 1. Simulating DB Insert for Flight booking
        System.out.println("[DB Transaction] Inserted flight booking for user: " + userId + ", amount: " + flightCost);

        // 2. Simulating DB Insert for Hotel booking
        System.out.println("[DB Transaction] Inserted hotel booking for user: " + userId + ", amount: " + hotelCost);

        // 3. Simulating DB Update on Trips table with version increment (Optimistic Lock)
        System.out.println("[DB Transaction] Updated total budget in trips table for trip: " + tripId);

        // All steps succeeded -> Atomically COMMIT
        return new BookingResult("BKG-" + System.currentTimeMillis(), true, totalCost);
    }
}
