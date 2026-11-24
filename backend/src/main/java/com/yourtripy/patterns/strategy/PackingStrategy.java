package com.yourtripy.patterns.strategy;

import java.util.List;

/**
 * Core Java Design Pattern: Strategy Pattern
 * Encapsulates dynamic packing list algorithms instead of hardcoded if-else statements.
 */
public interface PackingStrategy {
    boolean appliesTo(String destination);
    List<String> generatePackingItems(int days);
}
