ALTER TABLE orders ADD COLUMN restaurant_name varchar(255);
ALTER TABLE orders DROP FOREIGN KEY orders_restaurant_id;
DROP TABLE IF EXISTS restaurant_menu_items;
DROP TABLE IF EXISTS restaurants;
