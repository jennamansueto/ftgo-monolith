use ftgo;

alter table orders drop foreign key orders_assigned_courier_id;

alter table courier_actions drop foreign key courier_actions_order_id;
alter table courier_actions drop foreign key courier_actions_courier_id;

drop table courier_actions;
drop table courier;
