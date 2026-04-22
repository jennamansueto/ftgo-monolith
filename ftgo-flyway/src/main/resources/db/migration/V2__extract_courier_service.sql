use ftgo;

-- Remove cross-database FK: the courier table now lives in ftgo_courier.
alter table orders drop foreign key orders_assigned_courier_id;

-- Courier data is now owned by the extracted courier service (ftgo_courier).
alter table courier_actions drop foreign key courier_actions_courier_id;
alter table courier_actions drop foreign key courier_actions_order_id;
drop table courier_actions;
drop table courier;
