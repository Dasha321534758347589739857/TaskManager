CREATE TABLE IF NOT EXISTS tasks (
                                     id SERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    status VARCHAR(50) DEFAULT 'START',
    is_done BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Индексы для оптимизации
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);
CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks(created_at);

-- Вставка тестовых данных
INSERT INTO tasks (title, description, priority, status, is_done) VALUES
                                                                      ('Купить молоко', 'Блаблабла', 'HIGH', 'DONE', true),
                                                                      ('Сделать домашку', 'ААОААОАОА', 'MEDIUM', 'IN_PROGRESS', false),
                                                                      ('Доделать проект', 'Что же делать', 'LOW', 'START', false)
    ON CONFLICT DO NOTHING;