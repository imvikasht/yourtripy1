package com.yourtripy.patterns.builder;

import com.yourtripy.model.record.DayPlanRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 *  DESIGN PATTERN: Builder Pattern (Complex Object Assembly)
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Constructs a complicated TripPlan step-by-step cleanly without needing an 
 *  ugly constructor with 15 confusing parameters!
 *
 *  🥪 THE SUBWAY SANDWICH ANALOGY:
 *  When you order at Subway:
 *   - You don't shout: "Give me bread-honey-oat-cheese-toasted-chicken-lettuce-mayo" all in 1 second!
 *   - You pick your bread -> add cheese -> choose veggies -> pick sauce -> and finally say "Wrap it!" (.build()).
 *
 *  TripPlan plan = new TripPlan.Builder()
 *      .destination("Manali")
 *      .totalDays(5)
 *      .travelStyle("Adventure")
 *      .build();
 *
 *  🎓 COLLEGE SUBJECT: Software Engineering (Creational Design Patterns).
 * =========================================================================
 */
public class TripPlan {
    private final String id;
    private final String destination;
    private final int totalDays;
    private final String travelStyle;
    private final double totalBudget;
    private final List<DayPlanRecord> days;

    private TripPlan(Builder builder) {
        this.id = builder.id;
        this.destination = builder.destination;
        this.totalDays = builder.totalDays;
        this.travelStyle = builder.travelStyle;
        this.totalBudget = builder.totalBudget;
        this.days = Collections.unmodifiableList(new ArrayList<>(builder.days));
    }

    public String getId() { return id; }
    public String getDestination() { return destination; }
    public int getTotalDays() { return totalDays; }
    public String getTravelStyle() { return travelStyle; }
    public double getTotalBudget() { return totalBudget; }
    public List<DayPlanRecord> getDays() { return days; }

    public static class Builder {
        private String id;
        private String destination;
        private int totalDays;
        private String travelStyle = "Moderate";
        private double totalBudget = 0.0;
        private final List<DayPlanRecord> days = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder totalDays(int totalDays) {
            if (totalDays <= 0) throw new IllegalArgumentException("Total days must be positive");
            this.totalDays = totalDays;
            return this;
        }

        public Builder travelStyle(String travelStyle) {
            this.travelStyle = travelStyle;
            return this;
        }

        public Builder totalBudget(double totalBudget) {
            if (totalBudget < 0) throw new IllegalArgumentException("Budget cannot be negative");
            this.totalBudget = totalBudget;
            return this;
        }

        public Builder addDayPlan(DayPlanRecord dayPlan) {
            this.days.add(dayPlan);
            return this;
        }

        public Builder addAllDayPlans(List<DayPlanRecord> dayPlans) {
            this.days.addAll(dayPlans);
            return this;
        }

        public TripPlan build() {
            if (destination == null || destination.isBlank()) {
                throw new IllegalStateException("Destination is mandatory to build a TripPlan");
            }
            return new TripPlan(this);
        }
    }
}
