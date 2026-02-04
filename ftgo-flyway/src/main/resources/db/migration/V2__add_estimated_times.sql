use ftgo;

-- Add estimated pickup and delivery time columns to orders table
ALTER TABLE orders
  ADD COLUMN estimated_pickup_time datetime,
  ADD COLUMN estimated_delivery_time datetime;
