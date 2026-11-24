package com.yourtripy.patterns.strategy;

import java.util.ArrayList;
import java.util.List;

public class MountainPackingStrategy implements PackingStrategy {
    @Override
    public boolean appliesTo(String destination) {
        String dest = destination.toLowerCase();
        return dest.contains("mountain") || dest.contains("snow") || dest.contains("himalaya") 
            || dest.contains("manali") || dest.contains("alps") || dest.contains("ladakh");
    }

    @Override
    public List<String> generatePackingItems(int days) {
        List<String> items = new ArrayList<>(List.of(
            "Toothbrush", "Phone Charger", "Power Bank",
            "Underwear x" + days, "Socks x" + days,
            "Thermal Inners", "Waterproof Trekking Shoes", "Heavy Down Jacket", "Fleece Gloves", "Warm Beanie"
        ));
        return items;
    }
}
