# Event-Management-System (Project)
# 🎟️ EventHub — Centralized Event Management System
## Project Description :-
The Event Management System is a web-based platform designed to simplify event planning, organization, registration, and management. It provides a centralized platform for managing event information, registrations, and participants efficiently.
## 📌 Problem Statement 
Managing events using separate forms, messaging, and manual registration is stressful, time-consuming, error-prone, and inefficient. Having an online system where the creation of events, participant registration, management, and event information can be accessed in one place can help solve these issues, thus making the organization of events much easier and more efficient.
## 💡 The Solution
**EventHub** replaces manual forms with a unified web platform:
1. **Centralized Event Portal:** Attendees discover events, search by keyword, filter by category, and view real-time seat availability.
2. **Duplicate Prevention Engine:** Enforces case-insensitive email uniqueness per event at both the service layer (`existsByEventIdAndEmailIgnoreCase`) and database layer (`UNIQUE KEY uq_event_email`).
3. **Live Seat & Capacity Controls:** Real-time capacity validation prevents registrations once an event is sold out, complete with visual utilization progress bars.
4. **Unique Admission Pass Generation:** Generates tamper-resistant, human-readable ticket codes (`EVT-2026-XXXXX`) with printable e-passes and instant online reference lookup.
5. **Organizer Control Console:** Administrative dashboard to create, update, and delete events, view live attendee rosters, and cancel registrations to immediately free up seats.
## 🛠️ Tech 
| Layer | Technology | Purpose | 
| :--- | :--- | :--- |
| **Language** | Java 21 (LTS) | Modern Java core with records and pattern matching |
| **Framework** | Spring Boot 3.2.5 | Web MVC, dependency injection, and application lifecycle |
| **Database** | MySQL 8.0 / MariaDB | Production relational database (with zero-setup H2 in-memory option) |






