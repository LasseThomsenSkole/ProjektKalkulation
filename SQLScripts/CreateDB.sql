CREATE SCHEMA IF NOT EXISTS AlphaManagement;
USE AlphaManagement;
CREATE TABLE IF NOT EXISTS users(
    user_id int auto_increment primary key,
    user_name varchar(50),
    password varchar(12) -- hvis den er hashed bliver den hashed med 12 chars
);
CREATE TABLE IF NOT EXISTS projects(
    projects_id          int auto_increment primary key,
    project_name        varchar(50) not null ,
    project_description varchar(500) not null ,
    total_hours double not null default 0,
    project_deadline    date
);
CREATE TABLE IF NOT EXISTS subprojects(
    subproject_id int auto_increment primary key,
    subproject_name varchar(50) not null,
    subproject_description varchar(500) not null,
    subproject_hours double not null default 0,
    subproject_deadline date,
    parent_project_id int not null
);
CREATE TABLE IF NOT EXISTS tasks(
    task_id int auto_increment primary key,
    task_name varchar(50) not null,
    task_description varchar(500) not null,
    task_hours double not null default 0,
    task_deadline date,
    project_id int not null
);
CREATE TABLE IF NOT EXISTS subtasks(
    subtask_id int auto_increment primary key,
    subtask_name varchar(50) not null,
    subtask_description varchar(500) not null,
    subtask_hours double not null default 0,
    subtask_deadline date,
    parent_task_id int not null
)
