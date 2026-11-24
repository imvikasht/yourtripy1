package com.yourtripy.os.scheduling;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Operating Systems Concept: CPU Scheduling - Priority Scheduling
 * Demonstrates scheduling tasks based on priority tiers:
 *  1. VIP / Premium users (Priority 1) -> Scheduled first
 *  2. Standard users (Priority 2)
 *  3. Free users (Priority 3)
 */
public class PriorityTripScheduler {

    public enum PriorityLevel {
        PREMIUM(1),
        STANDARD(2),
        FREE(3);

        private final int rank;
        PriorityLevel(int rank) { this.rank = rank; }
        public int getRank() { return rank; }
    }

    public record ScheduledTripTask(
        String taskId,
        String destination,
        PriorityLevel priority,
        long arrivalTimeMs
    ) {}

    // Priority Queue sorting lowest rank integer first (Rank 1 = Highest Priority)
    // Resolving ties using Arrival Time (First-Come, First-Served / FCFS)
    private final PriorityBlockingQueue<ScheduledTripTask> queue = new PriorityBlockingQueue<>(
        20,
        Comparator.comparingInt((ScheduledTripTask t) -> t.priority().getRank())
                  .thenComparingLong(ScheduledTripTask::arrivalTimeMs)
    );

    public void submitTask(String taskId, String destination, PriorityLevel priority) {
        queue.put(new ScheduledTripTask(taskId, destination, priority, System.currentTimeMillis()));
    }

    public ScheduledTripTask dispatchNextTask() throws InterruptedException {
        // Blocks until highest priority task is ready
        return queue.take();
    }

    public int getPendingQueueSize() {
        return queue.size();
    }
}
