create database ftgo;
create database ftgo_consumer;
GRANT ALL PRIVILEGES ON ftgo.* TO 'mysqluser'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON ftgo_consumer.* TO 'mysqluser'@'%' WITH GRANT OPTION;
