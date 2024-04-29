CREATE SCHEMA IF NOT EXISTS AlphaManagement;
USE AlphaManagement;
CREATE TABLE IF NOT EXISTS users(
    id int auto_increment primary key,
    name varchar(50),
    password varchar(12) -- hvis den er hashed bliver den hashed med 12 chars
);
CREATE TABLE IF NOT EXISTS projects(
    id int auto_increment primary key,
    name varchar(50),
    total_hours double not null default 0,
    deadline date,


)