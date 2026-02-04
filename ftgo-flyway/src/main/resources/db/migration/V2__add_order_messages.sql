use ftgo;

create table order_messages
(
  id            bigint not null auto_increment,
  order_id      bigint not null,
  restaurant_id bigint not null,
  consumer_id   bigint not null,
  message_text  text,
  created_at    datetime,
  primary key (id)
) engine = InnoDB;

alter table order_messages
  add constraint order_messages_order_id foreign key (order_id) references orders (id);

alter table order_messages
  add constraint order_messages_restaurant_id foreign key (restaurant_id) references restaurants (id);

alter table order_messages
  add constraint order_messages_consumer_id foreign key (consumer_id) references consumers (id);
