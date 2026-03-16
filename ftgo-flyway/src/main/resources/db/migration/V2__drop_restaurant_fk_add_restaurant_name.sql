use ftgo;

-- Drop FK constraint from orders to restaurants since restaurants now live in a separate database
alter table orders drop foreign key orders_restaurant_id;

-- Drop FK constraint from restaurant_menu_items since this table is no longer used in the ftgo database
alter table restaurant_menu_items drop foreign key restaurant_menu_items_restaurant_id;

-- Add restaurant_name column to orders so we no longer need a JPA relationship to Restaurant
alter table orders add column restaurant_name varchar(255);
