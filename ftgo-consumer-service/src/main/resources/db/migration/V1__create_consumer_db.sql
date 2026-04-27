CREATE TABLE consumers (
  id         bigint not null,
  first_name varchar(255),
  last_name  varchar(255),
  primary key (id)
) engine = InnoDB;

CREATE TABLE hibernate_sequence (
  next_val bigint
) engine = InnoDB;

INSERT INTO hibernate_sequence VALUES (1);
