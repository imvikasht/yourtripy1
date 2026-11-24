package com.yourtripy.os.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * =========================================================================
 *  OPERATING SYSTEMS CONCEPT: Counting Semaphore (Rate Limiting)
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Stops the app from crashing by limiting how many users can call Google Gemini AI 
 *  at the exact same second!
 *
 *  🚪 THE CLUB BOUNCER ANALOGY:
 *  Imagine a club with a capacity of 5 people:
 *   - The Bouncer has 5 wristbands (Permits).
 *   - When a user arrives, they take a wristband (P-Operation / acquire).
 *   - If all 5 wristbands are taken, new visitors must wait in an orderly queue.
 *   - When someone leaves, they return the wristband (V-Operation / release), 
 *     and the next person in line enters.
 *
 *  🎓 COLLEGE SUBJECT: Operating Systems (Process Synchronization, Dijkstra's Semaphores).
 * =========================================================================
 */
public class RateLimitingSemaphore {
    
    // The semaphore maintaining the number of available permits
    private final Semaphore semaphore;
    private final int maxPermits;

    public RateLimitingSemaphore(int maxConcurrentRequests) {
        this.maxPermits = maxConcurrentRequests;
        // true flag guarantees FIFO fairness (First-In-First-Out prevents starvation!)
        this.semaphore = new Semaphore(maxConcurrentRequests, true);
    }

    /**
     * P-operation / Down(S) / acquire:
     * Tries to grab a permit. If full, waits up to timeoutMs before giving up.
     */
    public boolean acquirePermit(long timeoutMs) throws InterruptedException {
        return semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * V-operation / Up(S) / release:
     * Gives back the permit so the next waiting thread can run!
     */
    public void releasePermit() {
        semaphore.release();
    }

    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }
}
