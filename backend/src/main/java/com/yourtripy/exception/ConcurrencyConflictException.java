package com.yourtripy.exception;

/**
 * OS & Concurrency Concept: Race Condition / Optimistic Lock Failure
 */
public class ConcurrencyConflictException extends RuntimeException {
    public ConcurrencyConflictException(String resourceId) {
        super("Concurrent modification detected for resource: " + resourceId + ". Please retry.");
    }
}
