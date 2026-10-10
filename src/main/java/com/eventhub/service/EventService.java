package com.eventhub.service;

import com.eventhub.model.Event;
import com.eventhub.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    // Data repository managing Event persistence operations
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        // Inject EventRepository dependency
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> getActiveEvents() {
        // Fetch all active events sorted chronologically by start date
        return eventRepository.findByStatusOrderByEventDateAsc("ACTIVE");
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        // Retrieve every event from the database regardless of status
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Event> getEventById(Long id) {
        // Find a specific event by its primary key identifier
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        // Default the event status to ACTIVE if none was supplied during creation
        if (event.getStatus() == null || event.getStatus().trim().isEmpty()) {
            event.setStatus("ACTIVE");
        }
        // Persist new or updated event record in the database
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        // Delete an event record and cascade-delete its linked registrations
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Event> searchAndFilterEvents(String query, String category) {
        // Verify if a non-empty search keyword was supplied
        boolean hasQuery = query != null && !query.trim().isEmpty();
        // Verify if a specific event category was selected
        boolean hasCategory = category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL");

        // Filter by both search keyword and category when both criteria are provided
        if (hasQuery && hasCategory) {
            return eventRepository.searchActiveEventsWithCategory(query.trim(), category.trim());
        // Filter purely by keyword across event titles and descriptions
        } else if (hasQuery) {
            return eventRepository.searchActiveEvents(query.trim());
        // Filter purely by category type
        } else if (hasCategory) {
            return eventRepository.findByStatusAndCategoryIgnoreCase("ACTIVE", category.trim());
        // Return default list of all upcoming active events if no filter is applied
        } else {
            return getActiveEvents();
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        // Fetch distinct event categories to populate UI filter dropdowns
        return eventRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public long getTotalEventsCount() {
        // Count total events stored in the system for administrative metrics
        return eventRepository.count();
    }

    @Transactional(readOnly = true)
    public long getUpcomingEventsCount() {
        // Query database count of active events scheduled in the future
        Long count = eventRepository.countUpcomingEvents();
        // Return 0 if query result is null to avoid null pointer exceptions
        return count != null ? count : 0;
    }
}
