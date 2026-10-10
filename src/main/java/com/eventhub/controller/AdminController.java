package com.eventhub.controller;

import com.eventhub.model.Event;
import com.eventhub.model.Registration;
import com.eventhub.service.EventService;
import com.eventhub.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Services handling business operations for events and attendee registrations
    private final EventService eventService;
    private final RegistrationService registrationService;

    @Autowired
    public AdminController(EventService eventService, RegistrationService registrationService) {
        // Inject dependencies for administrative event and attendee management
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Retrieve the complete list of all registered events
        List<Event> allEvents = eventService.getAllEvents();
        // Calculate the aggregate number of attendee registrations across all events
        long totalRegistrations = registrationService.getTotalRegistrationsCount();
        // Count events scheduled to happen in the future
        long upcomingEvents = eventService.getUpcomingEventsCount();

        // Bind data attributes to the model for the admin dashboard view
        model.addAttribute("events", allEvents);
        model.addAttribute("totalEvents", allEvents.size());
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);

        // Render the administrative dashboard
        return "admin/dashboard";
    }

    @GetMapping("/events/new")
    public String showCreateEventForm(Model model) {
        // Instantiate a blank Event entity for data binding in the creation form
        Event event = new Event();
        // Pre-fill a convenient default start time 7 days ahead at 10:00 AM
        event.setEventDate(LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).withSecond(0));
        // Default the event status to active
        event.setStatus("ACTIVE");

        // Pass event object, creation flag, and header text to the view model
        model.addAttribute("event", event);
        model.addAttribute("isNew", true);
        model.addAttribute("pageTitle", "Create New Event");

        // Display the event form template
        return "admin/event-form";
    }

    @GetMapping("/events/edit/{id}")
    public String showEditEventForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Fetch the existing event by its identifier
        Optional<Event> eventOptional = eventService.getEventById(id);
        // If no matching event is found, notify and redirect back to admin home
        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/admin";
        }

        // Pass existing event details and edit flags to the view model
        model.addAttribute("event", eventOptional.get());
        model.addAttribute("isNew", false);
        model.addAttribute("pageTitle", "Edit Event");

        // Display the event form populated with existing values
        return "admin/event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute("event") Event event,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // If validation errors exist on title, capacity, or dates, redisplay form with error messages
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", event.getId() == null);
            model.addAttribute("pageTitle", event.getId() == null ? "Create New Event" : "Edit Event");
            return "admin/event-form";
        }

        // Save new event or persist modifications to an existing event
        eventService.saveEvent(event);
        // Determine whether this was a creation or update operation for the user message
        String action = (event.getId() != null) ? "updated" : "created";
        // Set flash message visible on the redirected admin dashboard
        redirectAttributes.addFlashAttribute("successMessage", "Event '" + event.getTitle() + "' " + action + " successfully.");

        // Redirect back to admin dashboard
        return "redirect:/admin";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Delete the event and any associated participant registrations
            eventService.deleteEvent(id);
            // Flash success notification
            redirectAttributes.addFlashAttribute("successMessage", "Event removed.");
        } catch (Exception ex) {
            // Flash error message if deletion fails due to database foreign keys or constraints
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to remove event: " + ex.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/events/{id}/participants")
    public String viewParticipants(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Retrieve the event to display its participant roster
        Optional<Event> eventOptional = eventService.getEventById(id);
        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/admin";
        }

        Event event = eventOptional.get();
        // Fetch all registered participants belonging to this specific event
        List<Registration> participants = registrationService.getParticipantsByEvent(id);

        // Bind event details, attendee list, count, and remaining capacity to the view
        model.addAttribute("event", event);
        model.addAttribute("participants", participants);
        model.addAttribute("participantCount", participants.size());
        model.addAttribute("seatsLeft", event.getSeatsLeft());

        // Render the participants management view
        return "admin/participants";
    }

    @PostMapping("/participants/{id}/delete")
    public String deleteParticipant(@PathVariable("id") Long id,
                                    @RequestParam("eventId") Long eventId,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Cancel and remove the participant registration by ID
            registrationService.cancelRegistration(id);
            // Flash confirmation message
            redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled.");
        } catch (Exception ex) {
            // Flash error message if cancellation encounters an error
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel registration: " + ex.getMessage());
        }
        // Redirect back to the participant list for this event
        return "redirect:/admin/events/" + eventId + "/participants";
    }
}
