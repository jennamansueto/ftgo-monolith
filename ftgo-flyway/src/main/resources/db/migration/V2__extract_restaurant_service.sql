-- Drop the foreign key constraint from orders to restaurants
-- since restaurants table will move to the restaurant service database
alter table orders drop foreign key orders_restaurant_id;

-- Add restaurant_name column to orders for denormalized storage
alter table orders add column restaurant_name varchar(255);

-- Populate restaurant_name from the existing restaurant data before removal
update orders o
  inner join restaurants r on o.restaurant_id = r.id
  set o.restaurant_name = r.name;

-- Drop the restaurant_menu_items foreign key and table (moving to restaurant service DB)
alter table restaurant_menu_items drop foreign key restaurant_menu_items_restaurant_id;
drop table restaurant_menu_items;

-- Drop the restaurants table (moving to restaurant service DB)
drop table restaurants;
