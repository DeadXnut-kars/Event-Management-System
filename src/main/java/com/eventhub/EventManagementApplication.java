package com.eventhub;
import com.eventhub.model.Event;
import com.eventhub.model.Registration;
import com.eventhub.repository.EventRepository;
import com.eventhub.repository.RegistrationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
@SpringBootApplication
public class EventManagementApplication {
    public static void main(String[] args) {
        // Bootstrap and run the Spring Boot web application
        SpringApplication.run(EventManagementApplication.class, args);
        // Print access URLs to the terminal for developer convenience
        System.out.println("---------------------------------------------------------");
        System.out.println(" EventHub is live and listening on http://localhost:8080");
        System.out.println(" Admin Console: http://localhost:8080/admin");
        System.out.println("---------------------------------------------------------");
    }
    @Bean
    public CommandLineRunner seedInitialData(EventRepository eventRepository, RegistrationRepository registrationRepository) {
        return args -> {
            // Seed sample events and attendees only if the database is currently empty
            if (eventRepository.count() == 0) {
                // Initialize TechPulse Hackathon event record
                Event e1 = new Event(
                        "TechPulse Hackathon 2026",
                        "Technical",
                        "A 24-hour sprint where builders, designers, and engineers team up to solve challenging problems across AI, Web3, and Cloud infrastructure. Mentors and prize pools included.",
                        LocalDateTime.now().plusDays(10).withHour(9).withMinute(0),
                        "Tech Innovation Center, Hall A",
                        120,
                        "Sarah Jenkins",
                        "organizer@techpulse.io",
                        "+1 (555) 234-5678",
                        "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=800&q=80"
                );
                // Save event into database repository
                eventRepository.save(e1);

                // Seed sample participants registered for the hackathon
                registrationRepository.save(new Registration("EVT-2026-A101", e1, "Alex Rivera", "alex.rivera@example.com", "+1 555-123-4567", "Apex Systems", "Software Engineer", "Looking for backend teammates"));
                registrationRepository.save(new Registration("EVT-2026-A102", e1, "Priya Sharma", "priya.sharma@example.com", "+1 555-234-5678", "Northwestern Tech", "Full-Stack Developer", "Building a healthcare app"));
                registrationRepository.save(new Registration("EVT-2026-A103", e1, "David Chen", "david.chen@example.com", "+1 555-345-6789", "Metro State", "UI/UX Designer", "Interested in frontend / design track"));

                // 2. Generative AI Workshop
                Event e2 = new Event(
                        "Deep Learning & Generative AI Workshop",
                        "Workshop",
                        "Hands-on workshop covering transformer architectures, fine-tuning open-source LLMs, and deploying inference pipelines with Python. Bring your own laptop.",
                        LocalDateTime.now().plusDays(14).withHour(10).withMinute(0),
                        "Conference Room 402, Building 3",
                        60,
                        "Dr. Marcus Vance",
                        "marcus.vance@aimedia.org",
                        "+1 (555) 345-6789",
                        "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80"
                );
                eventRepository.save(e2);

                registrationRepository.save(new Registration("EVT-2026-B201", e2, "Elena Rostova", "elena.r@example.com", "+1 555-456-7890", "DataWorks", "ML Engineer", "Interested in model quantization"));
                registrationRepository.save(new Registration("EVT-2026-B202", e2, "James Wilson", "jwilson@example.com", "+1 555-567-8901", "Independent", "Data Scientist", "Bringing personal GPU rig"));

                // 3. Resonance Music & Arts Festival
                Event e3 = new Event(
                        "Resonance Music & Arts Festival",
                        "Cultural",
                        "Annual celebration featuring live indie bands, visual art installations, stage theater, and acoustic performances across multiple stages.",
                        LocalDateTime.now().plusDays(25).withHour(17).withMinute(0),
                        "Civic Park Amphitheatre",
                        350,
                        "Community Arts Collective",
                        "contact@resonancefest.org",
                        "+1 (555) 456-7890",
                        "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80"
                );
                eventRepository.save(e3);

                // 4. Metropolitan Football Championship
                Event e4 = new Event(
                        "Metropolitan Football Championship",
                        "Sports",
                        "Knockout tournament featuring registered local clubs and teams competing for the annual city cup. 7-a-side format with certified referees.",
                        LocalDateTime.now().plusDays(8).withHour(16).withMinute(0),
                        "City Sports Arena Field 2",
                        50,
                        "Coach Ray Martinez",
                        "sports@cityleague.org",
                        "+1 (555) 567-8901",
                        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80"
                );
                eventRepository.save(e4);

                // 5. Cloud Architecture Keynote
                Event e5 = new Event(
                        "Modern Cloud Architecture & DevOps Keynote",
                        "Seminar",
                        "Insights and live case studies from enterprise software architects on Kubernetes, zero-trust security, distributed tracing, and resilient cloud systems.",
                        LocalDateTime.now().plusDays(18).withHour(14).withMinute(0),
                        "Civic Auditorium (Livestream available)",
                        150,
                        "CloudOps Network",
                        "keynotes@cloudops.net",
                        "+1 (555) 678-9012",
                        "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=80"
                );
                eventRepository.save(e5);

                // 6. Full-Stack Web Intensive
                Event e6 = new Event(
                        "Full-Stack Web Development Intensive",
                        "Technical",
                        "Comprehensive crash course on building end-to-end web apps: responsive UI, RESTful backends, database schema design, and production deployment.",
                        LocalDateTime.now().plusDays(5).withHour(11).withMinute(0),
                        "Workspace Co-op Lab 1",
                        45,
                        "DevGuild Leads",
                        "hello@devguild.io",
                        "+1 (555) 789-0123",
                        "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80"
                );
                eventRepository.save(e6);
            }
        };
    }
}
