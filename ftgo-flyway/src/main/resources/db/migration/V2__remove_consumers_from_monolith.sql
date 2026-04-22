use ftgo;

-- The Consumer Service has been extracted into its own microservice with its
-- own database (ftgo_consumer). The monolith no longer owns the consumers
-- table.
drop table if exists consumers;
