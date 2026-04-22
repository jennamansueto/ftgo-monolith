create database ftgo;
GRANT ALL PRIVILEGES ON ftgo.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database if not exists ftgo_restaurant;
GRANT ALL PRIVILEGES ON ftgo_restaurant.* TO 'mysqluser'@'%' WITH GRANT OPTION;
