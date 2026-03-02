create table restaurants
(
  id   bigint not null auto_increment,
  name varchar(255),
  street1 varchar(255),
  street2 varchar(255),
  city    varchar(255),
  state   varchar(255),
  zip     varchar(255),
  primary key (id)
) engine = InnoDB;

create table restaurant_menu_items
(
  restaurant_entity_id bigint not null,
  id                   varchar(255),
  name                 varchar(255),
  price                decimal(19, 2)
) engine = InnoDB;

alter table restaurant_menu_items
  add constraint fk_restaurant_menu_items_restaurant foreign key (restaurant_entity_id) references restaurants (id);
