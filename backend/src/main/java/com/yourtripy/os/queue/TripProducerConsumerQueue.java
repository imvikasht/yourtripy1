package com.yourtripy.os.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Operating Systems Concept: Producer-Consumer Problem (Bounded Buffer)
 * Manages background trip generation tasks.
 * Demonstrates:
 *  1. Bounded Shared Buffer (ArrayBlockingQueue of size N)
 *  2. Synchronization (Producer blocks when buffer is full, consumer blocks when empty)
 *  3. Daemon worker threads executing background I/O
 */
public class TripProducerConsumerQueue {
    public record TripTask(String taskId, String destination, int days, String userEmail) {}

    // Bounded buffer of fixed size 50 (OS concept: finite memory capacity)
    private final BlockingQueue<TripTask> buffer = new ArrayBlockingQueue<>(50);
    private volatile boolean isRunning = true;

    public TripProducerConsumerQueue() {
        startConsumerWorker();
    }

    /**
     * Producer: Enqueues a new background generation task.
     * Blocks if the buffer is full (preventing memory exhaustion).
     */
    public void produce(TripTask task) throws InterruptedException {
        buffer.put(task); // OS: Waits on buffer not full condition
    }

    /**
     * Consumer: Worker thread consuming tasks from the bounded buffer.
     */
    private void startConsumerWorker() {
        Thread worker = new Thread(() -> {
            while (isRunning) {
                try {
                    TripTask task = buffer.take(); // OS: Waits on buffer not empty condition
                    processTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Trip-Background-Worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void processTask(TripTask task) {
        System.out.println("[OS Worker] Consumed task: " + task.taskId() + " for destination: " + task.destination());
        // Simulating heavy background processing & email notification
    }

    public void shutdown() {
        this.isRunning = false;
    }
}
