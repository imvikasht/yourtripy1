-- =========================================================================
--  ACID TRANSACTIONS & CONCURRENCY EXPLAINED IN SIMPLE TERMS
--  (Use this exact story when explaining to your project evaluator!)
-- =========================================================================
--
--  💡 WHAT IS AN ACID TRANSACTION?
--  Imagine you are booking a "Vacation Package":
--    Step 1: Book the Flight ticket (₹5,000)
--    Step 2: Book the Hotel room (₹8,000)
--
--  ❌ THE NIGHTMARE SCENARIO:
--  Step 1 succeeds (your flight is booked and money is deducted).
--  Step 2 FAILS because the hotel is suddenly sold out!
--  Now you have a flight ticket to a city where you have nowhere to sleep,
--  and your money is already deducted!
--
--  ✅ THE ACID SOLUTION:
--  We wrap both steps in a single TRANSACTION:
--  Either BOTH succeed, or if even ONE fails, the database automatically
--  executes a ROLLBACK (cancels the flight, refunds your money, and restores everything).
--  It is strictly "ALL-OR-NOTHING"!
-- =========================================================================

-- -------------------------------------------------------------------------
-- SCENARIO 1: ATOMIC MULTI-VENDOR BOOKING (All-or-Nothing)
-- -------------------------------------------------------------------------
BEGIN; -- Start the atomic transaction

-- Step 1: PESSIMISTIC LOCK (SELECT ... FOR UPDATE)
-- Locks this trip's budget row so no other user or process can change it
-- while our booking calculation is running.
SELECT id, total_budget_amount 
FROM trips 
WHERE id = 'trip_manali_01' 
FOR UPDATE;

-- Step 2: Insert Flight booking
INSERT INTO bookings (id, trip_id, user_id, booking_type, provider_name, reference_number, amount, status)
VALUES ('bk_fl_001', 'trip_manali_01', 'user_vikas_01', 'FLIGHT', 'IndiGo Airlines', '6E-4521', 4500.00, 'CONFIRMED');

-- Step 3: Insert Hotel booking
INSERT INTO bookings (id, trip_id, user_id, booking_type, provider_name, reference_number, amount, status)
VALUES ('bk_ht_001', 'trip_manali_01', 'user_vikas_01', 'HOTEL', 'Himalayan Retreat', 'HR-9988', 8500.00, 'CONFIRMED');

-- Step 4: Update total spent budget
UPDATE trips 
SET total_budget_amount = total_budget_amount + 13000.00,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 'trip_manali_01';

-- IF ANY STEP FAILS (e.g. payment gateway timeout, card declined):
-- The database cancels everything:
-- ROLLBACK;

-- IF ALL STEPS SUCCEED:
-- The database saves everything permanently:
COMMIT;


-- -------------------------------------------------------------------------
-- SCENARIO 2: TRANSACTION ISOLATION (Preventing "Dirty Reads")
-- -------------------------------------------------------------------------
-- Question: "What is Isolation?"
-- Answer: "It prevents one user from seeing incomplete, half-saved data 
-- from another user's transaction before it is committed."
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
SELECT COUNT(*) FROM activities WHERE day_plan_id = 'dp_day_1';
-- Even if another user is adding 10 activities right now, this read
-- sees a stable, consistent snapshot of the data.
COMMIT;


-- -------------------------------------------------------------------------
-- SCENARIO 3: OPTIMISTIC LOCKING (Preventing Lost Updates)
-- -------------------------------------------------------------------------
-- Question: "What if two friends edit the same trip at the exact same second?"
-- Answer: We use a 'version' number column (0, 1, 2...).
-- Friend A checks version 0 -> updates destination -> version becomes 1.
-- Friend B tries to update where version = 0 -> Database says 0 rows affected!
-- Friend B's screen politely says: "Someone else just updated this trip. Please refresh!"
UPDATE trips 
SET destination = 'Goa - North & South',
    version = version + 1
WHERE id = 'trip_manali_01' AND version = 0;
