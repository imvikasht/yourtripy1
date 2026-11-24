package com.yourtripy.patterns.strategy;

import java.util.ArrayList;
import java.util.List;

public class DefaultPackingStrategy implements PackingStrategy {
    @Override
    public boolean appliesTo(String destination) {
        return true; // Fallback strategy
    }

    @Override
    public List<String> generatePackingItems(int days) {
        List<String> items = new ArrayList<>(List.of(
            "Toothbrush", "Phone Charger", "Power Bank",
            "Underwear x" + days, "Socks x" + days,
            "Comfortable Walking Shoes", "Compact Umbrella", "Casual Outfits", "Light Jacket"
        ));
        return items;
    }
}
