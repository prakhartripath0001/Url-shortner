-- MySQL initialization script
-- Creates separate databases for each microservice to enforce Database-per-Service pattern

CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS url_db;
CREATE DATABASE IF NOT EXISTS analytics_db;
CREATE DATABASE IF NOT EXISTS notification_db;
CREATE DATABASE IF NOT EXISTS payment_db;
