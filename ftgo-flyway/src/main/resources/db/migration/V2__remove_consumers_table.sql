-- Consumer data is now managed by the standalone consumer service
-- with its own database (ftgo_consumer_service)
drop table if exists consumers;
