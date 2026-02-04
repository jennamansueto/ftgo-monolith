-- Add estimated delivery time tracking fields to orders table
ALTER TABLE orders
ADD COLUMN estimated_pickup_time datetime,
ADD COLUMN estimated_delivery_time datetime;
