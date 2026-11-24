package com.yourtripy.patterns.strategy;

import java.util.List;

/**
 * =========================================================================
 *  DESIGN PATTERN: Strategy Pattern (Smart Packing Engine)
 * =========================================================================
 *  🎯 PURPOSE IN SIMPLE WORDS:
 *  Dynamically picks what clothes and gear to pack based on the trip's destination!
 *
 *  🎒 THE PACKING ANALOGY:
 *  Instead of having one giant, messy if-else condition:
 *  if (dest == "beach") ... else if (dest == "mountain") ...
 *
 *  We create separate "Strategy" classes:
 *   - BeachPackingStrategy (suggests swimwear, sunscreen, flip-flops)
 *   - MountainPackingStrategy (suggests heavy jackets, trekking boots, thermals)
 *   - DefaultPackingStrategy (suggests walking shoes, umbrella, casual wear)
 *
 *  🌟 WHY PROFESSORS LOVE THIS:
 *  It follows the SOLID "Open/Closed Principle":
 *  If you want to add a "Desert" strategy tomorrow, you just create a new DesertPackingStrategy 
 *  class WITHOUT changing a single line of existing code!
 *
 *  🎓 COLLEGE SUBJECT: Software Engineering & Object-Oriented Design Patterns.
 * =========================================================================
 */
public class PackingStrategyContext {
    private final List<PackingStrategy> strategies;
    private final PackingStrategy defaultStrategy;

    public PackingStrategyContext() {
        this.strategies = List.of(
            new BeachPackingStrategy(),
            new MountainPackingStrategy()
        );
        this.defaultStrategy = new DefaultPackingStrategy();
    }

    /**
     * Finds the matching strategy at runtime and generates the packing list.
     */
    public List<String> getPackingList(String destination, int days) {
        return strategies.stream()
            .filter(strategy -> strategy.appliesTo(destination))
            .findFirst()
            .orElse(defaultStrategy)
            .generatePackingItems(days);
    }
}
