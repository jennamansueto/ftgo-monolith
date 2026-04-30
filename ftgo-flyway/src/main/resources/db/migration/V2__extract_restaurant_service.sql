alter table orders add column restaurant_name varchar(255);

update orders o inner join restaurants r on o.restaurant_id = r.id set o.restaurant_name = r.name;

alter table orders drop foreign key orders_restaurant_id;
