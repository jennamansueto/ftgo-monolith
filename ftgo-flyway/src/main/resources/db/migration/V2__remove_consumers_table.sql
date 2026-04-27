-- Consumer table is now owned by the standalone consumer microservice (ftgo-consumer-service-standalone)
-- Remove it from the monolith's schema
drop table if exists consumers;
