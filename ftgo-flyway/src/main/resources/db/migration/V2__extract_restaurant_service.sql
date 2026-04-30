alter table orders drop foreign key orders_restaurant_id;

alter table orders add column restaurant_name varchar(255);
