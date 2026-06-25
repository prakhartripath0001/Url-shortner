-- PostgreSQL initialization script
-- Creates separate databases for each microservice
-- This enforces the Database-per-Service pattern
--
-- WHY SEPARATE DATABASES?
-- - Each service owns its data — no direct SQL joins across services
-- - Independent schema evolution — URL service can migrate without touching auth-service
-- - Independent scaling — can move each DB to separate server as load grows
-- - Follows Domain-Driven Design (DDD) bounded context principle

CREATE DATABASE auth_db;
CREATE DATABASE url_db;
CREATE DATABASE analytics_db;
CREATE DATABASE notification_db;
CREATE DATABASE payment_db;

-- Grant all privileges to the postgres user for local dev
-- In production: create a separate user per DB with minimal privileges
GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE url_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE analytics_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO postgres;
