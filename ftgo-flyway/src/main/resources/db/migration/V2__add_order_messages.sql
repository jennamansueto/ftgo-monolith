use ftgo;

create table order_messages
(
  id         bigint not null auto_increment,
  order_id   bigint not null,
  message    text not null,
  sent_at    datetime not null,
  primary key (id)
) engine = InnoDB;

alter table order_messages
  add constraint order_messages_order_id foreign key (order_id) references orders (id);

create index idx_order_messages_order_id on order_messages (order_id);
