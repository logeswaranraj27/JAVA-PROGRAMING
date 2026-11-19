CREATE DATABASE IF NOT EXISTS bus_pass_db;
USE bus_pass_db;

CREATE TABLE IF NOT EXISTS bus_pass (
    pass_id VARCHAR(20) PRIMARY KEY,
    register_number VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    route_number VARCHAR(20) NOT NULL,
    boarding_stop VARCHAR(100) NOT NULL,
    validity_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL
);
