package com.eventhub.repository;

import com.eventhub.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Retrieve all participants for an event ordered by newest registration first
    List<Registration> findByEventIdOrderByRegistrationDateDesc(Long eventId);

    // Efficient boolean query checking if an email is already registered for this event to prevent duplicates
    boolean existsByEventIdAndEmailIgnoreCase(Long eventId, String email);

    // Count current number of confirmed attendees for capacity enforcement
    long countByEventId(Long eventId);

    // Find registration pass by its generated unique ticket confirmation code
    Optional<Registration> findByRegistrationCode(String registrationCode);

    // Fetch all registrations made by a specific user across different events
    List<Registration> findByEmailIgnoreCaseOrderByRegistrationDateDesc(String email);
}
