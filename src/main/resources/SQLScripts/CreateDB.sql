CREATE SCHEMA IF NOT EXISTS AlphaManagement;
USE AlphaManagement;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    is_admin BOOL NOT NULL DEFAULT FALSE,
    password VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(50) NOT NULL,
    project_description VARCHAR(255) NOT NULL,
    total_hours DOUBLE NOT NULL DEFAULT 0,
    project_startdate DATE,
    project_deadline DATE,
    project_status ENUM('DONE', 'IN_PROGRESS', 'TODO', 'ARCHIVED', 'NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED'
    );

CREATE TABLE IF NOT EXISTS subprojects (
    subproject_id INT AUTO_INCREMENT PRIMARY KEY,
    subproject_name VARCHAR(50) NOT NULL,
    subproject_description VARCHAR(255) NOT NULL,
    subproject_hours DOUBLE NOT NULL DEFAULT 0,
    subproject_startdate DATE,
    subproject_deadline DATE,
    subproject_status ENUM('DONE', 'IN_PROGRESS', 'TODO', 'ARCHIVED', 'NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    parent_project_id INT NOT NULL,
    FOREIGN KEY (parent_project_id) REFERENCES projects(project_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(50) NOT NULL,
    task_description VARCHAR(255) NOT NULL,
    task_hours DOUBLE NOT NULL DEFAULT 0,
    task_startdate DATE,
    task_deadline DATE,
    task_status ENUM('DONE', 'IN_PROGRESS', 'TODO', 'ARCHIVED', 'NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    parent_subproject_id INT NOT NULL,
    FOREIGN KEY (parent_subproject_id) REFERENCES subprojects(subproject_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS subtasks (
    subtask_id INT AUTO_INCREMENT PRIMARY KEY,
    subtask_name VARCHAR(50) NOT NULL,
    subtask_description VARCHAR(255) NOT NULL,
    subtask_hours DOUBLE NOT NULL DEFAULT 0,
    subtask_startdate DATE,
    subtask_deadline DATE,
    subtask_status ENUM('DONE', 'IN_PROGRESS', 'TODO', 'ARCHIVED', 'NOT_STARTED') NOT NULL DEFAULT 'NOT_STARTED',
    parent_task_id INT NOT NULL,
    FOREIGN KEY (parent_task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS user_project_relation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
    );

DELIMITER //

-- Trigger for updates on subprojects
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

-- Trigger for inserts on subprojects
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
//

-- Trigger for updates on tasks
CREATE TRIGGER update_subproject_hours_after_update AFTER UPDATE ON tasks
    FOR EACH ROW
BEGIN
    DECLARE total_task_hours DOUBLE;

    -- Calculate the total hours of all tasks related to the subproject
    SELECT SUM(task_hours) INTO total_task_hours
    FROM tasks
    WHERE parent_subproject_id = NEW.parent_subproject_id;

    -- Update the subproject_hours in the subprojects table
    UPDATE subprojects
    SET subproject_hours = total_task_hours
    WHERE subproject_id = NEW.parent_subproject_id;
END;
//

-- Trigger for inserts on tasks
CREATE TRIGGER update_subproject_hours_after_insert AFTER INSERT ON tasks
    FOR EACH ROW
BEGIN
    DECLARE total_task_hours DOUBLE;

    -- Calculate the total hours of all tasks related to the subproject
    SELECT SUM(task_hours) INTO total_task_hours
    FROM tasks
    WHERE parent_subproject_id = NEW.parent_subproject_id;

    -- Update the subproject_hours in the subprojects table
    UPDATE subprojects
    SET subproject_hours = total_task_hours
    WHERE subproject_id = NEW.parent_subproject_id;
END;
//

-- Trigger for updates on subtasks
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

-- Trigger for inserts on subtasks
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
