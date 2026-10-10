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

import java.util.Optional;

@Controller
public class RegistrationController {

    // Services providing event and registration business logic
    private final EventService eventService;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(EventService eventService, RegistrationService registrationService) {
        // Inject service dependencies
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping("/events/{id}/register")
    public String showRegistrationForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Retrieve the event matching the requested ID
        Optional<Event> eventOptional = eventService.getEventById(id);
        // If event doesn't exist, redirect back to events list with an error notification
        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";
        }

        Event event = eventOptional.get();

        // Disallow loading registration form if the event is cancelled or inactive
        if (!"ACTIVE".equalsIgnoreCase(event.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration is closed for this event.");
            return "redirect:/events/" + id;
        }

        // Check if event is sold out before showing form to prevent unnecessary submissions
        if (event.isFull()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This event is at full capacity.");
            return "redirect:/events/" + id;
        }

        // Initialize empty registration model object for form binding
        Registration registration = new Registration();
        // Link target event to the new registration instance
        registration.setEvent(event);

        // Bind event and registration objects to the view model
        model.addAttribute("event", event);
        model.addAttribute("registration", registration);

        // Render the registration view template
        return "register";
    }

    @PostMapping("/events/{id}/register")
    public String processRegistration(@PathVariable("id") Long id,
                                      @Valid @ModelAttribute("registration") Registration registration,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        // Re-verify that target event exists in the database
        Optional<Event> eventOptional = eventService.getEventById(id);
        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events";
        }

        Event event = eventOptional.get();

        // If form fields fail bean validation constraints (blank name, invalid email, etc.), re-display form with field errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            return "register";
        }

        try {
            // Process registration: enforces capacity and prevents duplicate emails for this event
            Registration saved = registrationService.registerParticipant(id, registration);
            // Flash a success confirmation alert across redirect
            redirectAttributes.addFlashAttribute("successMessage", "Registration confirmed! Your pass is ready.");
            // Redirect to registration confirmation pass page
            return "redirect:/registrations/success/" + saved.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Catch domain business rule violations (e.g. duplicate email, capacity exceeded)
            model.addAttribute("event", event);
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        } catch (Exception ex) {
            // Fallback for unexpected runtime issues
            model.addAttribute("event", event);
            model.addAttribute("errorMessage", "Unable to complete registration. Please check your details and try again.");
            return "register";
        }
    }

    @GetMapping("/registrations/success/{id}")
    public String registrationSuccess(@PathVariable("id") Long id, Model model) {
        // Fetch saved registration to render electronic admission ticket
        Optional<Registration> regOptional = registrationService.getRegistrationById(id);
        if (regOptional.isEmpty()) {
            return "redirect:/events?error=RegistrationNotFound";
        }

        // Attach registration ticket details to the view model
        model.addAttribute("registration", regOptional.get());
        return "registration-success";
    }

    @GetMapping("/registrations/lookup")
    public String showLookupPage(@RequestParam(value = "code", required = false) String code, Model model) {
        // If a ticket reference code was submitted in the search box
        if (code != null && !code.trim().isEmpty()) {
            // Query ticket record using normalized uppercase confirmation code
            Optional<Registration> reg = registrationService.getRegistrationByCode(code.trim().toUpperCase());
            // If located, present registration badge details
            if (reg.isPresent()) {
                model.addAttribute("registration", reg.get());
            // If missing, display a clear lookup error message
            } else {
                model.addAttribute("lookupError", "No registration found with reference code: " + code.trim());
            }
        }
        return "lookup";
    }
}
