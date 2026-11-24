# YourTripy Backend: Core Java, OS & RDBMS Architecture Guide

This backend module demonstrates the implementation of **Core Java**, **Operating Systems (OS)**, and **Relational Database Management Systems (RDBMS)** concepts applied to the **YourTripy AI Trip Co-Pilot** platform.

> **Zero Modifications to Existing Frontend**: The existing React/Vite/Firebase frontend remains 100% untouched and functional. This backend module acts as an independent, enterprise-grade service.

---

## 1. Core Java Concepts Applied

| Concept | File Location | How It's Applied in YourTripy |
| :--- | :--- | :--- |
| **Generics (`<T>`)** | [`com.yourtripy.dto.ApiResponse<T>`](src/main/java/com/yourtripy/dto/ApiResponse.java) | Generic API wrapper for type-safe responses across all endpoints. |
| **Modern Java Records** | [`com.yourtripy.model.record.*`](src/main/java/com/yourtripy/model/record/) | Transparent, immutable data carriers for `ActivityRecord`, `DayPlanRecord`, and `LocationCoordinate`. |
| **Sealed Interfaces** | [`com.yourtripy.model.sealed.TravelStyle`](src/main/java/com/yourtripy/model/sealed/TravelStyle.java) | Restricts travel styles to known subclasses (`Backpacker`, `Moderate`, `Luxury`), enabling compiler-checked exhaustive pattern matching. |
| **Builder Pattern** | [`com.yourtripy.patterns.builder.TripPlan`](src/main/java/com/yourtripy/patterns/builder/TripPlan.java) | Assembles complex immutable trip plans step-by-step with validation. |
| **Strategy Pattern** | [`com.yourtripy.patterns.strategy.*`](src/main/java/com/yourtripy/patterns/strategy/) | Dynamic packing list generation (`BeachPackingStrategy`, `MountainPackingStrategy`, `DefaultPackingStrategy`). |
| **Factory & Polymorphism** | [`com.yourtripy.patterns.factory.*`](src/main/java/com/yourtripy/patterns/factory/) | Polymorphic travel bookings (`TrainBooking`, `FlightBooking`, `HotelBooking`, `BusBooking`). |
| **Custom Exceptions** | [`com.yourtripy.exception.*`](src/main/java/com/yourtripy/exception/) | Domain-specific exceptions (`TripNotFoundException`, `BudgetExceededException`, `ConcurrencyConflictException`). |
| **Streams API & Lambdas** | [`com.yourtripy.service.ItineraryProcessingService`](src/main/java/com/yourtripy/service/ItineraryProcessingService.java) | Filtering by preference, summing budget totals via `mapToDouble`, and grouping days using `Collectors.toMap`. |
| **CompletableFuture & Concurrency** | [`com.yourtripy.service.TripAggregatorService`](src/main/java/com/yourtripy/service/TripAggregatorService.java) | Concurrent aggregation of AI itinerary, Weather API, and Railways status in parallel. |

---

## 2. Operating Systems (OS) Concepts Applied

| OS Concept | File Location | How It's Applied in YourTripy |
| :--- | :--- | :--- |
| **Counting Semaphore** | [`com.yourtripy.os.concurrency.RateLimitingSemaphore`](src/main/java/com/yourtripy/os/concurrency/RateLimitingSemaphore.java) | Dijkstra's P and V operations with fairness policy to rate-limit external API calls (Gemini/IRCTC). |
| **Critical Section & Mutex** | [`com.yourtripy.os.concurrency.BookingCriticalSection`](src/main/java/com/yourtripy/os/concurrency/BookingCriticalSection.java) | Resolves race conditions during concurrent hotel/train seat reservations using fine-grained `ReentrantLock` with timeout deadlock prevention. |
| **Producer-Consumer Problem** | [`com.yourtripy.os.queue.TripProducerConsumerQueue`](src/main/java/com/yourtripy/os/queue/TripProducerConsumerQueue.java) | Bounded buffer (`ArrayBlockingQueue`) with condition variables and daemon consumer threads for background processing. |
| **Page Replacement (LRU)** | [`com.yourtripy.os.memory.LruItineraryCache`](src/main/java/com/yourtripy/os/memory/LruItineraryCache.java) | Custom O(1) Least Recently Used cache using Doubly Linked List + HashMap for caching popular city itineraries. |
| **CPU Priority Scheduling** | [`com.yourtripy.os.scheduling.PriorityTripScheduler`](src/main/java/com/yourtripy/os/scheduling/PriorityTripScheduler.java) | `PriorityBlockingQueue` prioritizing tasks based on user tier (`PREMIUM` > `STANDARD` > `FREE`). |

---

## 3. Relational Database (RDBMS) Concepts Applied

| RDBMS Concept | File Location | Key Highlights |
| :--- | :--- | :--- |
| **3NF Normalization** | [`database/schema.sql`](database/schema.sql) | Atomic relational tables: `users`, `trips`, `day_plans`, `activities`, `places`, `bookings`. Eliminates transitive dependencies. |
| **Referential Integrity** | [`database/schema.sql`](database/schema.sql) | Primary Keys and Foreign Keys with `ON DELETE CASCADE`. |
| **Check Constraints** | [`database/schema.sql`](database/schema.sql) | Enforces positive day counts (`CHECK (total_days > 0)`), valid styles, non-negative budgets. |
| **B-Tree & Composite Indexes** | [`database/schema.sql`](database/schema.sql) | `idx_trips_user_id` and composite `idx_trips_destination_style` for sub-millisecond lookups. |
| **Spatial Indexing (GIS)** | [`database/schema.sql`](database/schema.sql) | PostGIS GiST index on `places.geom` for fast radius search (`ST_DWithin`) of nearby restaurants. |
| **ACID Transactions** | [`database/acid_transactions.sql`](database/acid_transactions.sql) | Atomic multi-step bookings with commit and rollback; isolation levels (`REPEATABLE READ`). |
| **Optimistic & Pessimistic Locking**| [`database/acid_transactions.sql`](database/acid_transactions.sql) | `SELECT ... FOR UPDATE` (Pessimistic) and `version` column increments (Optimistic). |
| **Triggers & Views** | [`database/schema.sql`](database/schema.sql) | `log_trip_changes()` trigger function for audit logs; `v_user_trip_summary` view for dashboard loading. |

---

## Running the Java Backend

### Prerequisites
- JDK 17 or higher
- Maven 3.8+

### Commands
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The server will start on port `8080` with ready-to-use REST endpoints:
- `GET http://localhost:8080/api/trips/plan?destination=Manali&days=5`
- `GET http://localhost:8080/api/trips/packing-list?destination=Goa&days=3`
- `POST http://localhost:8080/api/trips/book`
