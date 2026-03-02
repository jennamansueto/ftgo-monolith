use ftgo;

-- Drop the foreign key constraint on orders.restaurant_id since restaurants
-- are now stored in the separate ftgo_restaurant database.
alter table orders drop foreign key orders_restaurant_id;

-- Drop the foreign key on restaurant_menu_items since this table is now
-- managed by the extracted restaurant service in its own database.
alter table restaurant_menu_items drop foreign key restaurant_menu_items_restaurant_id;
