package com.yourtripy.patterns.strategy;

import java.util.ArrayList;
import java.util.List;

public class BeachPackingStrategy implements PackingStrategy {
    @Override
    public boolean appliesTo(String destination) {
        String dest = destination.toLowerCase();
        return dest.contains("beach") || dest.contains("goa") || dest.contains("miami") 
            || dest.contains("maldives") || dest.contains("island") || dest.contains("bali");
    }

    @Override
    public List<String> generatePackingItems(int days) {
        List<String> items = new ArrayList<>(List.of(
            "Toothbrush", "Phone Charger", "Power Bank",
            "Underwear x" + days, "Socks x" + days,
            "Sunscreen (SPF 50+)", "Swimwear", "Polarized Sunglasses", "Flip-flops", "Quick-dry Beach Towel"
        ));
        return items;
    }
}
