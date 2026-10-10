package com.eventhub.controller;

import com.eventhub.model.Event;
import com.eventhub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    // Service providing business logic for event discovery and detail retrieval
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        // Inject EventService dependency
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(@RequestParam(value = "search", required = false) String search,
                             @RequestParam(value = "category", required = false) String category,
                             Model model) {
        // Query active events matching optional search keywords and category filters
        List<Event> events = eventService.searchAndFilterEvents(search, category);
        // Fetch all distinct event categories for populating the filter dropdown
        List<String> categories = eventService.getAllCategories();

        // Pass filtered events list to the template
        model.addAttribute("events", events);
        // Pass available categories list to populate UI filter options
        model.addAttribute("categories", categories);
        // Preserve user search input in filter bar after form submission
        model.addAttribute("currentSearch", search);
        // Preserve selected category in dropdown after form submission
        model.addAttribute("currentCategory", category);
        // Display total number of matching events found
        model.addAttribute("totalFound", events.size());

        // Render the public events catalog page
        return "events";
    }

    @GetMapping("/{id}")
    public String eventDetails(@PathVariable("id") Long id, Model model) {
        // Look up the requested event by its primary key
        Optional<Event> eventOptional = eventService.getEventById(id);
        // If event doesn't exist, redirect back to the catalog with an error flag
        if (eventOptional.isEmpty()) {
            return "redirect:/events?error=EventNotFound";
        }

        Event event = eventOptional.get();
        // Bind the event entity to the view model
        model.addAttribute("event", event);
        // Calculate remaining available seats
        model.addAttribute("seatsLeft", event.getSeatsLeft());
        // Calculate current registered attendee count
        model.addAttribute("registeredCount", event.getRegisteredCount());
        // Check if event has reached maximum capacity to disable registration CTA
        model.addAttribute("isFull", event.isFull());

        // Render event details template
        return "event-details";
    }
}
