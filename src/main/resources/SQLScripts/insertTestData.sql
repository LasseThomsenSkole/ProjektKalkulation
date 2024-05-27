-- Dette er lavet af chatGPT
USE
AlphaManagement;

-- Inserting test data into the 'users' table
INSERT INTO users ( is_admin, user_name, password)
VALUES (true, 'admin', 'admin'),
       (false, 'test', 'test'),
       (false, 'john_doe', 'password123'),
       (false, 'jane_smith', 'letmein'),
       (false, 'mike_jones', 'securepwd');

-- Inserting test data into the 'projects' table
INSERT INTO projects (project_name, project_description, project_startdate, project_deadline)
VALUES ('Project Alpha', 'Developing a new software application', '2024-04-26', '2024-07-31'),
       ('Project Beta', 'Building a website for client X', '2024-05-14', '2024-06-15'),
       ('Project Gamma', 'Research project on artificial intelligence', '2024-06-01', '2024-08-20');

-- Inserting test data into the 'subprojects' table
INSERT INTO subprojects (subproject_name, subproject_description, subproject_startdate, subproject_deadline, parent_project_id)
VALUES ('Alpha Subproject 1', 'Design phase of Project Alpha', '2024-05-01', '2024-05-15', 1),
       ('Alpha Subproject 2', 'Development phase of Project Alpha', '2024-05-01', '2024-06-30', 1),
       ('Beta Subproject 1', 'Frontend development for Project Beta', '2024-05-15', '2024-05-30', 2),
       ('Gamma Subproject 1', 'Data collection for Project Gamma', '2024-05-10', '2024-06-10', 3);

-- Inserting test data into the 'tasks' table
INSERT INTO tasks (task_name, task_description, task_startdate, task_deadline, parent_subproject_id)
VALUES ('Design UI', 'Create user interface mockups', '2024-05-02', '2024-05-10', 1),
       ('Backend API Development', 'Implement backend APIs', '2024-05-23', '2024-06-20', 2),
       ('Homepage Design', 'Design homepage layout', '2024-05-02', '2024-05-20', 3),
       ('Data Analysis', 'Analyze collected data', '2024-05-30', '2024-06-05', 4);

-- Inserting test data into the 'subtasks' table
INSERT INTO subtasks (subtask_name, subtask_description, subtask_hours, subtask_startdate, subtask_deadline, parent_task_id)
VALUES ('Design Logo', 'Create company logo', 6, '2024-05-01', '2024-05-05', 1),
       ('Database Schema Design', 'Design database schema', 7, '2024-06-04', '2024-06-10', 2),
       ('Implement API Endpoints', 'Implement REST API endpoints', 3, '2024-05-20', '2024-06-15', 2),
       ('Coding HTML/CSS', 'Write frontend code for homepage', 2, '2024-05-01', '2024-05-25', 3),
       ('Data Cleaning', 'Clean and preprocess collected data', 1, '2024-05-25', '2024-05-30', 4);

-- Inserting test data into the 'user_project_relation' table
INSERT INTO user_project_relation (user_id, project_id)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (1, 3),
       (4, 2);
