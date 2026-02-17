CREATE TABLE hibernate_sequence (
  next_val BIGINT
);

INSERT INTO hibernate_sequence VALUES (1);

CREATE TABLE consumers (
  id BIGINT NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  PRIMARY KEY (id)
);
