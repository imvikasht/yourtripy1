package com.yourtripy.service;

import com.yourtripy.model.record.ActivityRecord;
import com.yourtripy.model.record.DayPlanRecord;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Core Java Concept: Java Streams API & Functional Programming
 * Demonstrates:
 *  1. stream(), filter(), map(), sorted(), toList()
 *  2. Collectors.groupingBy() for day-to-day grouping
 *  3. mapToDouble() and sum() for budget aggregation
 */
@Service
public class ItineraryProcessingService {

    /**
     * Filters activities matching the user's category preferences (e.g. "Foodie", "Adventure")
     * and sorts them by rating / estimated cost.
     */
    public List<ActivityRecord> filterAndRankActivities(List<ActivityRecord> allActivities, List<String> preferredCategories) {
        return allActivities.stream()
            .filter(activity -> preferredCategories.stream()
                .anyMatch(pref -> activity.category().equalsIgnoreCase(pref)))
            .sorted(Comparator.comparingDouble(ActivityRecord::estimatedCost))
            .collect(Collectors.toList());
    }

    /**
     * Calculates the total estimated cost of all activities in an itinerary.
     */
    public double calculateTotalEstimatedCost(List<ActivityRecord> activities) {
        return activities.stream()
            .mapToDouble(ActivityRecord::estimatedCost)
            .sum();
    }

    /**
     * Groups raw activity records by their designated day number.
     */
    public Map<Integer, List<ActivityRecord>> groupActivitiesByDay(List<DayPlanRecord> dayPlans) {
        return dayPlans.stream()
            .collect(Collectors.toMap(
                DayPlanRecord::dayNumber,
                DayPlanRecord::activities
            ));
    }
}
