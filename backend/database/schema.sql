-- =========================================================================
--  YOURTRIPY: SIMPLIFIED RELATIONAL DATABASE SCHEMA (RDBMS)
--  Written to be 100% understandable for College Presentations & Viva!
-- =========================================================================
-- 
--  💡 WHY DO WE NEED THIS FILE?
--  Instead of saving everything into one giant, messy JSON block, we split
--  our travel data into clean, organized tables. This is called NORMALIZATION (3NF).
--
--  🏢 THE REAL-LIFE ANALOGY:
--  Think of an organization:
--   - 1 User can create Many Trips. (1 to N)
--   - 1 Trip has Many Days. (1 to N)
--   - 1 Day has Many Activities. (1 to N)
--  If you delete a Trip, you want all its days and activities to be deleted automatically
--  so you don't leave "ghost" activities behind! We do that with: ON DELETE CASCADE.
-- =========================================================================

-- Optional extension: Only needed if you want GPS spatial radius searches
CREATE EXTENSION IF NOT EXISTS postgis;

-- -------------------------------------------------------------------------
-- TABLE 1: USERS
-- What it does: Stores basic user profile info.
-- Viva Question: "What is PRIMARY KEY?" -> A unique ID so no two users have the same account.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) PRIMARY KEY,                         -- Unique User ID (e.g., 'usr_101')
    name VARCHAR(120) NOT NULL,                         -- Traveler Name
    email VARCHAR(255) UNIQUE NOT NULL,                 -- UNIQUE constraint prevents duplicate accounts
    avatar_url TEXT,
    tier VARCHAR(20) DEFAULT 'STANDARD' 
         CHECK (tier IN ('FREE', 'STANDARD', 'PREMIUM')), -- CHECK constraint ensures only valid tiers
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------------------
-- TABLE 2: TRIPS (Parent Table)
-- What it does: Stores overall trip summary (e.g., "Manali 5 Days Trip").
-- Viva Question: "What is FOREIGN KEY?" -> user_id links each trip to the user who created it!
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trips (
    id VARCHAR(64) PRIMARY KEY,                         -- Trip ID (e.g., 'trip_manali_01')
    user_id VARCHAR(64) NOT NULL,                       -- Belongs to which user?
    destination VARCHAR(150) NOT NULL,                  -- City/Place (e.g., 'Manali')
    total_days INT NOT NULL 
         CHECK (total_days > 0 AND total_days <= 60),   -- Business rule: Trips must be 1 to 60 days
    travel_style VARCHAR(30) NOT NULL 
         CHECK (travel_style IN ('Budget', 'Moderate', 'Luxury', 'Adventure', 'Romantic')),
    total_budget_amount NUMERIC(12, 2) DEFAULT 0.00 
         CHECK (total_budget_amount >= 0),              -- Budget can never be negative
    image_url TEXT,
    version INT DEFAULT 0,                              -- For OPTIMISTIC LOCKING: stops 2 users from overwriting each other
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- If user deletes their account, delete all their trips automatically!
    CONSTRAINT fk_trips_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ⚡ INDEXES: Makes searching super fast (like an index at the back of a textbook)
-- Instead of scanning 1,000,000 rows one by one (O(N)), B-Tree finds it in O(log N) time!
CREATE INDEX IF NOT EXISTS idx_trips_user_id ON trips(user_id);
CREATE INDEX IF NOT EXISTS idx_trips_destination_style ON trips(destination, travel_style);

-- -------------------------------------------------------------------------
-- TABLE 3: DAY PLANS
-- What it does: Splits a trip into Day 1, Day 2, Day 3, etc.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS day_plans (
    id VARCHAR(64) PRIMARY KEY,                         -- Day ID (e.g., 'dp_manali_day1')
    trip_id VARCHAR(64) NOT NULL,                       -- Belongs to which trip?
    day_number INT NOT NULL CHECK (day_number > 0),     -- 1, 2, 3...
    summary TEXT,                                       -- e.g., "Sightseeing around Solang Valley"
    
    -- If a trip is deleted, delete its days automatically
    CONSTRAINT fk_dayplans_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    -- A trip cannot have two "Day 1"s!
    CONSTRAINT uq_trip_day UNIQUE (trip_id, day_number)
);

CREATE INDEX IF NOT EXISTS idx_dayplans_trip_id ON day_plans(trip_id);

-- -------------------------------------------------------------------------
-- TABLE 4: ACTIVITIES
-- What it does: Stores what to do on each day (e.g., "Visit Solang Valley at 10 AM").
-- Viva Question: "Why separate Activities from Day Plans?"
-- Answer: 3rd Normal Form (3NF)! An activity depends only on its specific day.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activities (
    id VARCHAR(64) PRIMARY KEY,
    day_plan_id VARCHAR(64) NOT NULL,                   -- Belongs to Day 1, Day 2, etc.
    activity_time VARCHAR(20),                          -- e.g., '10:00 AM'
    title VARCHAR(200) NOT NULL,                        -- e.g., 'Paragliding at Solang'
    category VARCHAR(50) NOT NULL,                      -- 'Adventure', 'Culture', 'Food'
    duration VARCHAR(50),                               -- '2 hours'
    location VARCHAR(255) NOT NULL,                     -- Place name
    description TEXT,
    recommendation_reason TEXT,
    best_time_to_visit VARCHAR(100),
    latitude NUMERIC(10, 6) NOT NULL,                   -- GPS Latitude for Map
    longitude NUMERIC(10, 6) NOT NULL,                  -- GPS Longitude for Map
    estimated_cost NUMERIC(10, 2) DEFAULT 0.00 CHECK (estimated_cost >= 0),
    travel_time_from_prev VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- If day plan is deleted, delete its activities automatically
    CONSTRAINT fk_activities_dayplan FOREIGN KEY (day_plan_id) REFERENCES day_plans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_activities_dayplan_id ON activities(day_plan_id);

-- -------------------------------------------------------------------------
-- TABLE 5: NEARBY PLACES / RESTAURANTS (PostGIS Spatial Concept)
-- What it does: Stores restaurants and attractions with GPS coordinates.
-- Viva Question: "How does the app know which restaurant is closest to my activity?"
-- Answer: PostGIS spatial index calculates distance between points in 2 milliseconds!
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS places (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    place_type VARCHAR(50) NOT NULL,                    -- 'RESTAURANT', 'CAFE', 'HOTEL'
    rating NUMERIC(3, 2) CHECK (rating >= 0 AND rating <= 5.0),
    latitude NUMERIC(10, 6) NOT NULL,
    longitude NUMERIC(10, 6) NOT NULL,
    geom GEOGRAPHY(Point, 4326)                         -- GPS Geography Point
);

-- Spatial index for radius searches (e.g. "Find food within 2km")
CREATE INDEX IF NOT EXISTS idx_places_spatial ON places USING GIST(geom);

-- -------------------------------------------------------------------------
-- TABLE 6: BOOKINGS (Demonstrates ACID Transactions)
-- What it does: Stores confirmed tickets for Flights, Hotels, and Trains.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(64) PRIMARY KEY,
    trip_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    booking_type VARCHAR(20) NOT NULL 
         CHECK (booking_type IN ('HOTEL', 'FLIGHT', 'TRAIN', 'BUS', 'ACTIVITY')),
    provider_name VARCHAR(100),                         -- e.g., 'IndiGo', 'Indian Railways'
    reference_number VARCHAR(100),                      -- PNR or Confirmation Number
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    status VARCHAR(20) DEFAULT 'PENDING' 
         CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED')),
    booked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bookings_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bookings_user_status ON bookings(user_id, status);

-- -------------------------------------------------------------------------
-- TABLE 7: AUDIT LOGS (Demonstrates Database Triggers)
-- What it does: An automatic black-box recorder. If someone updates or deletes a trip,
-- this table records WHO changed it and WHAT was changed.
-- -------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trip_audit_logs (
    id SERIAL PRIMARY KEY,
    trip_id VARCHAR(64) NOT NULL,
    action VARCHAR(20) NOT NULL,                        -- 'UPDATE' or 'DELETE'
    changed_by VARCHAR(64),
    old_data JSONB,                                     -- What it looked like before
    new_data JSONB,                                     -- What it looks like now
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Trigger function: Automatically runs AFTER any change on the trips table
CREATE OR REPLACE FUNCTION log_trip_changes() 
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        INSERT INTO trip_audit_logs (trip_id, action, old_data, new_data)
        VALUES (OLD.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO trip_audit_logs (trip_id, action, old_data, new_data)
        VALUES (OLD.id, 'DELETE', to_jsonb(OLD), NULL);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_trip_audit ON trips;
CREATE TRIGGER trg_trip_audit
AFTER UPDATE OR DELETE ON trips
FOR EACH ROW EXECUTE FUNCTION log_trip_changes();

-- -------------------------------------------------------------------------
-- TABLE 8: SQL VIEW (Demonstrates Virtual Tables for Fast Dashboards)
-- Viva Question: "What is a VIEW?"
-- Answer: A pre-computed saved query! Instead of writing 4 complex JOINs
-- every time a user opens My Trips page, the app just selects from this View!
-- -------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_user_trip_summary AS
SELECT 
    t.id AS trip_id,
    t.user_id,
    t.destination,
    t.total_days,
    t.travel_style,
    t.total_budget_amount,
    COUNT(DISTINCT d.id) AS days_count,
    COUNT(DISTINCT a.id) AS activities_count,
    COALESCE(SUM(b.amount), 0.00) AS total_booked_expenses
FROM trips t
LEFT JOIN day_plans d ON t.id = d.trip_id
LEFT JOIN activities a ON d.id = a.day_plan_id
LEFT JOIN bookings b ON t.id = b.trip_id AND b.status = 'CONFIRMED'
GROUP BY t.id, t.user_id, t.destination, t.total_days, t.travel_style, t.total_budget_amount;

-- -------------------------------------------------------------------------
-- 9. SUPABASE SECURITY: ROW LEVEL SECURITY (RLS)
-- Enables security so Supabase shows zero warnings and secures table access
-- -------------------------------------------------------------------------
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE trips ENABLE ROW LEVEL SECURITY;
ALTER TABLE day_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE activities ENABLE ROW LEVEL SECURITY;
ALTER TABLE places ENABLE ROW LEVEL SECURITY;
ALTER TABLE bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE trip_audit_logs ENABLE ROW LEVEL SECURITY;

-- Allow read and write access for application operations
DO $$ 
BEGIN
    -- Public read policies (anyone can read places and public trips)
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Public places read policy') THEN
        CREATE POLICY "Public places read policy" ON places FOR SELECT USING (true);
    END IF;
    
    -- Application access policies
    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to users') THEN
        CREATE POLICY "Allow access to users" ON users FOR ALL USING (true) WITH CHECK (true);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to trips') THEN
        CREATE POLICY "Allow access to trips" ON trips FOR ALL USING (true) WITH CHECK (true);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to day_plans') THEN
        CREATE POLICY "Allow access to day_plans" ON day_plans FOR ALL USING (true) WITH CHECK (true);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to activities') THEN
        CREATE POLICY "Allow access to activities" ON activities FOR ALL USING (true) WITH CHECK (true);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to bookings') THEN
        CREATE POLICY "Allow access to bookings" ON bookings FOR ALL USING (true) WITH CHECK (true);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_policies WHERE policyname = 'Allow access to audit logs') THEN
        CREATE POLICY "Allow access to audit logs" ON trip_audit_logs FOR ALL USING (true) WITH CHECK (true);
    END IF;
END $$;
