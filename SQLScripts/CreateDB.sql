CREATE SCHEMA IF NOT EXISTS AlphaManagement;
USE AlphaManagement;
CREATE TABLE IF NOT EXISTS users(
    user_id int auto_increment primary key,
    user_name varchar(50),
    password varchar(50) -- hvis den er hashed bliver den hashed med 12 chars
    );
CREATE TABLE IF NOT EXISTS projects(
    project_id          int auto_increment primary key,
    project_name        varchar(50) not null ,
    project_description varchar(500) not null ,
    total_hours double not null default 0,
    project_deadline    date,
    project_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED', 'NOT_STARTED') not null default 'NOT_STARTED'
    );
CREATE TABLE IF NOT EXISTS subprojects(
    subproject_id int auto_increment primary key,
    subproject_name varchar(50) not null,
    subproject_description varchar(500) not null,
    subproject_hours double not null default 0,
    subproject_deadline date,
    subproject_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED','NOT_STARTED') not null default 'NOT_STARTED',
    parent_project_id int not null
    );
CREATE TABLE IF NOT EXISTS tasks(
    task_id int auto_increment primary key,
    task_name varchar(50) not null,
    task_description varchar(500) not null,
    task_hours double not null default 0,
    task_deadline date,
    task_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED','NOT_STARTED') not null default 'NOT_STARTED',
    subproject_id int not null
    );
CREATE TABLE IF NOT EXISTS subtasks(
    subtask_id int auto_increment primary key,
    subtask_name varchar(50) not null,
    subtask_description varchar(500) not null,
    subtask_hours double not null default 0,
    subtask_deadline date,
    subtask_status enum('DONE', 'IN_PROGRESS','TODO','ARCHIVED', 'NOT_STARTED') not null default 'NOT_STARTED',
    parent_task_id int not null
    );
CREATE TABLE IF NOT EXISTS user_project_relation(
    id int auto_increment primary key,
    user_id int not null,
    project_id int not null
);
-- Create a trigger to update total_hours in projects table when subproject hours change
DELIMITER //
-- Trigger for updates
CREATE TRIGGER update_total_hours_after_update AFTER UPDATE ON subprojects
    FOR EACH ROW
BEGIN
    DECLARE total_subproject_hours DOUBLE;

    -- Calculate the total hours of all subprojects related to the project
    SELECT SUM(subproject_hours) INTO total_subproject_hours
    FROM subprojects
    WHERE parent_project_id = NEW.parent_project_id;

    -- Update the total_hours in the projects table
    UPDATE projects
    SET total_hours = total_subproject_hours
    WHERE project_id = NEW.parent_project_id;
END;
//

-- Trigger for inserts
CREATE TRIGGER update_total_hours_after_insert AFTER INSERT ON subprojects
    FOR EACH ROW
BEGIN
    DECLARE total_subproject_hours DOUBLE;

    -- Calculate the total hours of all subprojects related to the project
    SELECT SUM(subproject_hours) INTO total_subproject_hours
    FROM subprojects
    WHERE parent_project_id = NEW.parent_project_id;

    -- Update the total_hours in the projects table
    UPDATE projects
    SET total_hours = total_subproject_hours
    WHERE project_id = NEW.parent_project_id;
END;

-- Create a trigger to update subproject_hours in subprojects table when task hours change
CREATE TRIGGER update_subproject_hours_after_update AFTER UPDATE ON tasks
    FOR EACH ROW
BEGIN
    DECLARE total_task_hours DOUBLE;

    -- Calculate the total hours of all tasks related to the subproject
    SELECT SUM(task_hours) INTO total_task_hours
    FROM tasks
    WHERE subproject_id = NEW.subproject_id;

    -- Update the subproject_hours in the subprojects table
    UPDATE subprojects
    SET subproject_hours = total_task_hours
    WHERE subproject_id = NEW.subproject_id;
END;
//

-- Trigger for inserts
CREATE TRIGGER update_subproject_hours_after_insert AFTER INSERT ON tasks
    FOR EACH ROW
BEGIN
    DECLARE total_task_hours DOUBLE;

    -- Calculate the total hours of all tasks related to the subproject
    SELECT SUM(task_hours) INTO total_task_hours
    FROM tasks
    WHERE subproject_id = NEW.subproject_id;

    -- Update the subproject_hours in the subprojects table
    UPDATE subprojects
    SET subproject_hours = total_task_hours
    WHERE subproject_id = NEW.subproject_id;
END;
//
-- Create a trigger to update task_hours in tasks table when subtask hours change
-- Trigger for updates
CREATE TRIGGER update_task_hours_after_update AFTER UPDATE ON subtasks
    FOR EACH ROW
BEGIN
    DECLARE total_subtask_hours DOUBLE;

    -- Calculate the total hours of all subtasks related to the task
    SELECT SUM(subtask_hours) INTO total_subtask_hours
    FROM subtasks
    WHERE parent_task_id = NEW.parent_task_id;

    -- Update the task_hours in the tasks table
    UPDATE tasks
    SET task_hours = total_subtask_hours
    WHERE task_id = NEW.parent_task_id;
END;
//

-- Trigger for inserts
CREATE TRIGGER update_task_hours_after_insert AFTER INSERT ON subtasks
    FOR EACH ROW
BEGIN
    DECLARE total_subtask_hours DOUBLE;

    -- Calculate the total hours of all subtasks related to the task
    SELECT SUM(subtask_hours) INTO total_subtask_hours
    FROM subtasks
    WHERE parent_task_id = NEW.parent_task_id;

    -- Update the task_hours in the tasks table
    UPDATE tasks
    SET task_hours = total_subtask_hours
    WHERE task_id = NEW.parent_task_id;
END;
//

DELIMITER ;


