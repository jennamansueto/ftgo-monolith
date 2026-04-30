-- Remove cross-service foreign keys
alter table courier_actions drop foreign key courier_actions_order_id;

alter table orders drop foreign key orders_assigned_courier_id;
