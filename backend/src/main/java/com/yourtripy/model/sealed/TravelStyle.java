package com.yourtripy.model.sealed;

/**
 * Modern Java (Java 17/21): Sealed Interfaces
 * Restricts inheritance to known, strictly modeled travel styles,
 * enabling exhaustive pattern matching in switch statements.
 */
public sealed interface TravelStyle permits BackpackerStyle, ModerateStyle, LuxuryStyle, AdventureStyle {
    String getDisplayName();
    double getBudgetMultiplier();
}

final class BackpackerStyle implements TravelStyle {
    @Override public String getDisplayName() { return "Backpacker"; }
    @Override public double getBudgetMultiplier() { return 0.5; }
}

final class ModerateStyle implements TravelStyle {
    @Override public String getDisplayName() { return "Moderate"; }
    @Override public double getBudgetMultiplier() { return 1.0; }
}

final class LuxuryStyle implements TravelStyle {
    @Override public String getDisplayName() { return "Luxury"; }
    @Override public double getBudgetMultiplier() { return 2.5; }
}

final class AdventureStyle implements TravelStyle {
    @Override public String getDisplayName() { return "Adventure"; }
    @Override public double getBudgetMultiplier() { return 1.2; }
}
