-- Add restaurant_name column to orders table for denormalization
-- This is needed because the Order entity no longer has a JPA @ManyToOne
-- relationship to Restaurant after extracting the Restaurant Service.
ALTER TABLE orders ADD COLUMN restaurant_name VARCHAR(255);

-- Populate restaurant_name from the restaurants table for existing orders
UPDATE orders o
  INNER JOIN restaurants r ON o.restaurant_id = r.id
  SET o.restaurant_name = r.name;

-- Drop the foreign key constraint between orders and restaurants
-- since Restaurant is now managed by the standalone Restaurant microservice
ALTER TABLE orders DROP FOREIGN KEY orders_restaurant_id;
