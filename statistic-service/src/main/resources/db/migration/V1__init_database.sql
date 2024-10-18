CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      author_id INTEGER,
                      assignee_id INTEGER,
                      deadline TIMESTAMP,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      status VARCHAR(50) NOT NULL,
                      priority VARCHAR(50) NOT NULL
);
CREATE TABLE time_entries (
                              id SERIAL PRIMARY KEY,
                              task_id INTEGER NOT NULL,
                              user_id INTEGER,
                              date DATE,
                              hours FLOAT,
                              FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
);
