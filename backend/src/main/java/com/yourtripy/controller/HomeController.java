package com.yourtripy.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Root controller to provide a friendly JSON response when visiting the root URL.
 */
@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "project", "YourTripy AI Trip Co-pilot - Java Core Engine",
            "status", "RUNNING",
            "environment", "Render Cloud Container",
            "endpoints", Map.of(
                "health", "/api/trips/health",
                "samplePlan", "/api/trips/plan?destination=Goa&days=3",
                "packingList", "/api/trips/packing-list?destination=Manali&days=5"
            )
        );
    }
}
