package com.yourtripy.model.record;

import java.util.List;

/**
 * Modern Java: Record for Day Plan with nested activities.
 */
public record DayPlanRecord(
    int dayNumber,
    String summary,
    List<ActivityRecord> activities
) {}
