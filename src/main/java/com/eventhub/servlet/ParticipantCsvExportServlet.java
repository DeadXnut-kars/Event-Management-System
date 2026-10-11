package com.eventhub.servlet;

import com.eventhub.model.Registration;
import com.eventhub.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class ParticipantCsvExportServlet extends HttpServlet {

    // Service dependency used to query registered participant records
    private final RegistrationService registrationService;

    @Autowired
    public ParticipantCsvExportServlet(RegistrationService registrationService) {
        // Inject RegistrationService instance
        this.registrationService = registrationService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract the eventId query parameter from the HTTP GET request
        String eventIdParam = request.getParameter("eventId");

        // Validate that the required eventId parameter is present and non-empty
        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            // Respond with HTTP 400 Bad Request error status if parameter is missing
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required 'eventId' parameter.");
            return;
        }

        Long eventId;
        try {
            // Parse eventId string into Long type
            eventId = Long.parseLong(eventIdParam.trim());
        } catch (NumberFormatException e) {
            // Respond with HTTP 400 if parameter is not a valid numeric identifier
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric format for 'eventId'.");
            return;
        }

        // Query database for all participants registered for this event
        List<Registration> participants = registrationService.getParticipantsByEvent(eventId);

        // Configure HTTP response MIME type to text/csv with UTF-8 character encoding
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Set Content-Disposition header with attachment directive to force browser file download
        String fileName = "participants_event_" + eventId + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Prevent browser and proxy caching of dynamically generated export file
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Obtain character output stream from the HttpServletResponse
        try (PrintWriter writer = response.getWriter()) {
            // Output CSV column headers row
            writer.println("Ticket Code,Participant Name,Email,Phone,Organization,Role,Registration Date");

            // Iterate through participants list and stream each row formatted as comma-separated values
            for (Registration reg : participants) {
                // Escape special characters to prevent CSV injection or broken delimiter formatting
                String code = escapeCsv(reg.getRegistrationCode());
                String name = escapeCsv(reg.getParticipantName());
                String email = escapeCsv(reg.getEmail());
                String phone = escapeCsv(reg.getPhone());
                String org = escapeCsv(reg.getOrganization());
                String role = escapeCsv(reg.getRoleTitle());
                String date = escapeCsv(reg.getFormattedDate());

                // Print single comma-separated attendee line
                writer.printf("%s,%s,%s,%s,%s,%s,%s%n", code, name, email, phone, org, role, date);
            }

            // Flush the writer buffer to push remaining bytes to the client
            writer.flush();
        }
    }

    private String escapeCsv(String value) {
        // Return empty quotes if field is null
        if (value == null) {
            return "\"\"";
        }
        // Escape existing double quotes by doubling them per RFC 4180 CSV standard
        String escaped = value.replace("\"", "\"\"");
        // Wrap field value in enclosing double quotes
        return "\"" + escaped + "\"";
    }
}
