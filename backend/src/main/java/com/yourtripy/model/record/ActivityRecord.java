package com.yourtripy.model.record;

import java.util.List;

/**
 * Modern Java (Java 17/21): Record
 * Clean, immutable data carrier representing an itinerary activity.
 */
public record ActivityRecord(
    String id,
    String time,
    String title,
    String category,
    String duration,
    String location,
    String description,
    String recommendationReason,
    List<String> tips,
    String bestTimeToVisit,
    LocationCoordinate coordinates,
    double estimatedCost,
    String travelTimeFromPrevious
) {}
