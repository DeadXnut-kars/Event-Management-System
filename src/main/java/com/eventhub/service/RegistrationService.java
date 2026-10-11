package com.eventhub.service;

import com.eventhub.model.Event;
import com.eventhub.model.Registration;
import com.eventhub.repository.EventRepository;
import com.eventhub.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RegistrationService {

    // Repositories for persisting and querying events and registration records
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    // Lock manager supplying per-event monitor locks for thread synchronization
    private final com.eventhub.concurrency.BookingLockManager bookingLockManager;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               com.eventhub.concurrency.BookingLockManager bookingLockManager) {
        // Inject required repository and concurrency dependencies
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.bookingLockManager = bookingLockManager;
    }

    public Registration registerParticipant(Long eventId, Registration registration) {
        // Obtain dedicated lock object for this event ID to serialize concurrent bookings
        Object eventLock = bookingLockManager.getLock(eventId);

        // Synchronize on event lock to eliminate race conditions during concurrent seat check and booking
        synchronized (eventLock) {
            // Fetch the event by its unique ID or throw an exception if it does not exist
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

            // Prevent registration if the organizer has closed or cancelled the event
            if (!"ACTIVE".equalsIgnoreCase(event.getStatus())) {
                throw new IllegalStateException("Registration is closed. This event is not active.");
            }

            // Fetch current registration count to enforce event seat limits
            long currentCount = registrationRepository.countByEventId(eventId);
            // Reject the registration if total attendees have reached the maximum allowed capacity
            if (currentCount >= event.getCapacity()) {
                throw new IllegalStateException("Event has reached maximum capacity (" + event.getCapacity() + ").");
            }

            // Sanitize email address by trimming spaces and converting to lowercase for consistent checking
            String trimmedEmail = registration.getEmail().trim().toLowerCase();
            // Prevent duplicate registrations by checking if this email is already registered for this event
            if (registrationRepository.existsByEventIdAndEmailIgnoreCase(eventId, trimmedEmail)) {
                throw new IllegalArgumentException("This email address is already registered for this event: " + trimmedEmail);
            }

            // Associate the validated event with this participant record
            registration.setEvent(event);
            // Store the normalized email address to guarantee data uniformity
            registration.setEmail(trimmedEmail);
            // Set the registration timestamp to the current system time
            registration.setRegistrationDate(LocalDateTime.now());

            // Generate a random 5-character alphanumeric token for the ticket reference
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            // Extract the current calendar year to structure human-readable confirmation codes
            int currentYear = LocalDateTime.now().getYear();
            // Formulate a structured ticket code like 'EVT-2026-A1B2C' for check-in and lookup
            registration.setRegistrationCode("EVT-" + currentYear + "-" + uniqueSuffix);

            // Commit the participant registration to the database and return the saved instance
            return registrationRepository.save(registration);
        }
    }

    @Transactional(readOnly = true)
    public List<Registration> getParticipantsByEvent(Long eventId) {
        // Retrieve all registered participants for a given event ordered by most recent registration
        return registrationRepository.findByEventIdOrderByRegistrationDateDesc(eventId);
    }

    @Transactional(readOnly = true)
    public Optional<Registration> getRegistrationById(Long id) {
        // Lookup a single registration record by primary key ID
        return registrationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Registration> getRegistrationByCode(String code) {
        // Locate a registration using the public confirmation ticket code
        return registrationRepository.findByRegistrationCode(code);
    }

    public void cancelRegistration(Long id) {
        // Remove a participant registration by primary key ID to free up a seat
        registrationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long getTotalRegistrationsCount() {
        // Count total registrations across all events for administrative dashboard statistics
        return registrationRepository.count();
    }
}
