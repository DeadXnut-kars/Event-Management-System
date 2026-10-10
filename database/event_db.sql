-- ==========================================================
-- EventHub - Database Schema and Seed Data
-- Database Engine: MySQL / MariaDB (XAMPP / MySQL Workbench)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS event_db;
USE event_db;

DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;

CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    venue VARCHAR(150) NOT NULL,
    capacity INT NOT NULL,
    organizer_name VARCHAR(100) NOT NULL,
    organizer_email VARCHAR(100) NOT NULL,
    organizer_phone VARCHAR(20),
    banner_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_code VARCHAR(30) NOT NULL UNIQUE,
    event_id BIGINT NOT NULL,
    participant_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    organization VARCHAR(100),
    role_title VARCHAR(50),
    special_notes TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_registration 
        FOREIGN KEY (event_id) REFERENCES events(id) 
        ON DELETE CASCADE,
    CONSTRAINT uq_event_email 
        UNIQUE (event_id, email)
);

-- ==========================================================
-- Seed Events
-- ==========================================================

INSERT INTO events (title, category, description, event_date, venue, capacity, organizer_name, organizer_email, organizer_phone, banner_url, status)
VALUES 
(
    'TechPulse Hackathon 2026',
    'Technical',
    'A 24-hour sprint where builders, designers, and engineers team up to solve challenging problems across AI, Web3, and Cloud infrastructure. Mentors and prize pools included.',
    DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY) + INTERVAL 9 HOUR,
    'Tech Innovation Center, Hall A',
    120,
    'Sarah Jenkins',
    'organizer@techpulse.io',
    '+1 (555) 234-5678',
    'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
),
(
    'Deep Learning & Generative AI Workshop',
    'Workshop',
    'Hands-on workshop covering transformer architectures, fine-tuning open-source LLMs, and deploying inference pipelines with Python. Bring your own laptop.',
    DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY) + INTERVAL 10 HOUR,
    'Conference Room 402, Building 3',
    60,
    'Dr. Marcus Vance',
    'marcus.vance@aimedia.org',
    '+1 (555) 345-6789',
    'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
),
(
    'Resonance Music & Arts Festival',
    'Cultural',
    'Annual celebration featuring live indie bands, visual art installations, stage theater, and acoustic performances across multiple stages.',
    DATE_ADD(CURRENT_DATE, INTERVAL 25 DAY) + INTERVAL 17 HOUR,
    'Civic Park Amphitheatre',
    350,
    'Community Arts Collective',
    'contact@resonancefest.org',
    '+1 (555) 456-7890',
    'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
),
(
    'Metropolitan Football Championship',
    'Sports',
    'Knockout tournament featuring registered local clubs and teams competing for the annual city cup. 7-a-side format with certified referees.',
    DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY) + INTERVAL 16 HOUR,
    'City Sports Arena Field 2',
    50,
    'Coach Ray Martinez',
    'sports@cityleague.org',
    '+1 (555) 567-8901',
    'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
),
(
    'Modern Cloud Architecture & DevOps Keynote',
    'Seminar',
    'Insights and live case studies from enterprise software architects on Kubernetes, zero-trust security, distributed tracing, and resilient cloud systems.',
    DATE_ADD(CURRENT_DATE, INTERVAL 18 DAY) + INTERVAL 14 HOUR,
    'Civic Auditorium (Livestream available)',
    150,
    'CloudOps Network',
    'keynotes@cloudops.net',
    '+1 (555) 678-9012',
    'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
),
(
    'Full-Stack Web Development Intensive',
    'Technical',
    'Comprehensive crash course on building end-to-end web apps: responsive UI, RESTful backends, database schema design, and production deployment.',
    DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY) + INTERVAL 11 HOUR,
    'Workspace Co-op Lab 1',
    45,
    'DevGuild Leads',
    'hello@devguild.io',
    '+1 (555) 789-0123',
    'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80',
    'ACTIVE'
);

-- Seed Registrations
INSERT INTO registrations (registration_code, event_id, participant_name, email, phone, organization, role_title, special_notes)
VALUES
('EVT-2026-A101', 1, 'Alex Rivera', 'alex.rivera@example.com', '+1 555-123-4567', 'Apex Systems', 'Software Engineer', 'Looking for backend teammates'),
('EVT-2026-A102', 1, 'Priya Sharma', 'priya.sharma@example.com', '+1 555-234-5678', 'Northwestern Tech', 'Full-Stack Developer', 'Building a healthcare app'),
('EVT-2026-A103', 1, 'David Chen', 'david.chen@example.com', '+1 555-345-6789', 'Metro State', 'UI/UX Designer', 'Interested in frontend / design track');

INSERT INTO registrations (registration_code, event_id, participant_name, email, phone, organization, role_title, special_notes)
VALUES
('EVT-2026-B201', 2, 'Elena Rostova', 'elena.r@example.com', '+1 555-456-7890', 'DataWorks', 'ML Engineer', 'Interested in model quantization'),
('EVT-2026-B202', 2, 'James Wilson', 'jwilson@example.com', '+1 555-567-8901', 'Independent', 'Data Scientist', 'Bringing personal GPU rig');
