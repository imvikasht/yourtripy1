package com.yourtripy.os.concurrency;

import com.yourtripy.exception.ConcurrencyConflictException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * =========================================================================
 *  OPERATING SYSTEMS CONCEPT: Mutual Exclusion (Mutex) & Critical Section
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Prevents double-booking! If two travelers try to book the last train seat or
 *  hotel room at the exact same millisecond, only ONE person gets it.
 *
 *  🚻 THE BATHROOM DOOR LOCK ANALOGY:
 *   - When person A enters the bathroom, they lock the door (Acquire Mutex).
 *   - Person B arrives, finds it locked, and must wait outside.
 *   - Person A finishes and unlocks the door (Release Mutex).
 *   - To prevent Deadlocks (someone falling asleep inside forever), we use 
 *     tryLock(3000 ms). If the lock isn't free in 3 seconds, we release gracefully!
 *
 *  🎓 COLLEGE SUBJECT: Operating Systems (Race Conditions, Critical Section Problem).
 * =========================================================================
 */
public class BookingCriticalSection {

    // Separate locks for each room or seat ID so people booking different rooms don't block each other!
    private final ConcurrentHashMap<String, ReentrantLock> resourceLocks = new ConcurrentHashMap<>();

    public <T> T executeInCriticalSection(String resourceId, CriticalSectionTask<T> task) {
        // Get or create the specific lock for this room/seat
        ReentrantLock lock = resourceLocks.computeIfAbsent(resourceId, k -> new ReentrantLock(true));

        boolean acquired = false;
        try {
            // 🛡️ Deadlock Prevention: Wait at most 3 seconds. Do not wait forever!
            acquired = lock.tryLock(3000, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new ConcurrencyConflictException("Seat/Room is currently locked by another customer: " + resourceId);
            }

            // 🔒 CRITICAL SECTION: Safe from race conditions! Only 1 thread executes this at a time.
            return task.execute();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Booking interrupted", e);
        } finally {
            if (acquired) {
                lock.unlock(); // 🔓 Always unlock in finally block so door is never left locked!
            }
        }
    }

    @FunctionalInterface
    public interface CriticalSectionTask<T> {
        T execute();
    }
}
