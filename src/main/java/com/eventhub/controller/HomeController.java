package com.eventhub.controller;

import com.eventhub.service.EventService;
import com.eventhub.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Services supplying statistical data and event listings
    private final EventService eventService;
    private final RegistrationService registrationService;

    @Autowired
    public HomeController(EventService eventService, RegistrationService registrationService) {
        // Inject EventService and RegistrationService dependencies
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Supply total count of created events for the landing page hero counter
        model.addAttribute("totalEvents", eventService.getTotalEventsCount());
        // Supply count of upcoming future events
        model.addAttribute("upcomingEvents", eventService.getUpcomingEventsCount());
        // Supply total registered participants count across the platform
        model.addAttribute("totalRegistrations", registrationService.getTotalRegistrationsCount());
        // Fetch active events to feature on the homepage
        model.addAttribute("featuredEvents", eventService.getActiveEvents());
        // Populate category quick-filter pills on the homepage
        model.addAttribute("categories", eventService.getAllCategories());

        // Render homepage template
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        // Pass summary statistics to the about page
        model.addAttribute("totalEvents", eventService.getTotalEventsCount());
        model.addAttribute("totalRegistrations", registrationService.getTotalRegistrationsCount());
        // Render system overview and about information template
        return "about";
    }
}
