ALTER TABLE orders ADD COLUMN restaurant_name varchar(255);
UPDATE orders o JOIN restaurants r ON o.restaurant_id = r.id SET o.restaurant_name = r.name;
UPDATE orders SET order_minimum = 0.00 WHERE order_minimum IS NULL OR order_minimum = 2147483647.00;
ALTER TABLE orders DROP FOREIGN KEY orders_restaurant_id;
DROP TABLE IF EXISTS restaurant_menu_items;
DROP TABLE IF EXISTS restaurants;
