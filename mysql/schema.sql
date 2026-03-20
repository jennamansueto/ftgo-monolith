create database ftgo;
GRANT ALL PRIVILEGES ON ftgo.* TO 'mysqluser'@'%' WITH GRANT OPTION;

create database consumer_service;
GRANT ALL PRIVILEGES ON consumer_service.* TO 'mysqluser'@'%' WITH GRANT OPTION;
