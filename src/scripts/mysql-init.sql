DROP DATABASE IF EXISTS gym_bro_rest_db;
 DROP USER IF EXISTS `gym_bro_rest_admin`@`%`;
 CREATE DATABASE IF NOT EXISTS gym_bro_rest_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
 CREATE USER IF NOT EXISTS `gym_bro_rest_admin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
 GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON gym_bro_rest_db.* TO `gym_bro_rest_admin`@`%`;