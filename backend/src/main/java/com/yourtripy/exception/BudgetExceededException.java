package com.yourtripy.exception;

public class BudgetExceededException extends RuntimeException {
    public BudgetExceededException(double requested, double maximum) {
        super("Requested budget (" + requested + ") exceeds maximum limit (" + maximum + ").");
    }
}
