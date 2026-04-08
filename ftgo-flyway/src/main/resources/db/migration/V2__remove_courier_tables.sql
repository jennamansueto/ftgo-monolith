use ftgo;

ALTER TABLE orders DROP FOREIGN KEY orders_assigned_courier_id;
ALTER TABLE courier_actions DROP FOREIGN KEY courier_actions_order_id;
ALTER TABLE courier_actions DROP FOREIGN KEY courier_actions_courier_id;
DROP TABLE IF EXISTS courier_actions;
DROP TABLE IF EXISTS courier;
