package com.eventhub.dao;

import com.eventhub.dto.EventOccupancyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class EventAnalyticsDao {

    // Injected DataSource providing pooled database connections
    private final DataSource dataSource;

    @Autowired
    public EventAnalyticsDao(DataSource dataSource) {
        // Inject DataSource dependency
        this.dataSource = dataSource;
    }

    public List<EventOccupancyDto> getEventOccupancyAnalytics() {
        List<EventOccupancyDto> resultList = new ArrayList<>();

        // Raw SQL joining events with registrations to compute aggregate attendee counts
        String sql = "SELECT e.id, e.title, e.category, e.capacity, COUNT(r.id) AS registered_count " +
                     "FROM events e " +
                     "LEFT JOIN registrations r ON e.id = r.event_id " +
                     "GROUP BY e.id, e.title, e.category, e.capacity " +
                     "ORDER BY registered_count DESC";

        // Try-with-resources automatically closes Connection, PreparedStatement, and ResultSet
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Iterate through rows in the JDBC ResultSet cursor
            while (rs.next()) {
                // Extract primitive and object fields by column label from ResultSet
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String category = rs.getString("category");
                int capacity = rs.getInt("capacity");
                int registeredCount = rs.getInt("registered_count");

                // Compute seats remaining and occupancy rate metrics in memory
                int seatsRemaining = Math.max(0, capacity - registeredCount);
                double occupancyRate = capacity > 0 ? Math.round(((double) registeredCount / capacity) * 10000.0) / 100.0 : 0.0;

                // Instantiate and populate DTO record
                EventOccupancyDto dto = new EventOccupancyDto(id, title, category, capacity, registeredCount, seatsRemaining, occupancyRate);
                // Append mapped DTO to analytics collection
                resultList.add(dto);
            }
        } catch (SQLException e) {
            // Translate checked SQLException into an unchecked runtime exception with contextual error message
            throw new RuntimeException("Failed to execute JDBC event occupancy query: " + e.getMessage(), e);
        }

        return resultList;
    }

    public Map<String, Object> getCategorySummaryMetrics(String categoryName) {
        Map<String, Object> summary = new HashMap<>();

        // Parameterized SQL query aggregating total capacity and total registrations by category
        String sql = "SELECT COUNT(DISTINCT e.id) AS total_events, " +
                     "COALESCE(SUM(e.capacity), 0) AS total_capacity, " +
                     "COUNT(r.id) AS total_registrations " +
                     "FROM events e " +
                     "LEFT JOIN registrations r ON e.id = r.event_id " +
                     "WHERE LOWER(e.category) = LOWER(?)";

        // Open raw connection and prepare statement with positional parameter placeholder
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Bind category parameter safely to index 1 to prevent SQL injection vulnerabilities
            ps.setString(1, categoryName);

            // Execute query and extract single aggregate record
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Extract aggregate columns from ResultSet
                    long totalEvents = rs.getLong("total_events");
                    long totalCapacity = rs.getLong("total_capacity");
                    long totalRegistrations = rs.getLong("total_registrations");

                    // Populate summary map with extracted metric values
                    summary.put("category", categoryName);
                    summary.put("totalEvents", totalEvents);
                    summary.put("totalCapacity", totalCapacity);
                    summary.put("totalRegistrations", totalRegistrations);
                }
            }
        } catch (SQLException e) {
            // Throw runtime exception wrapping database access failure
            throw new RuntimeException("Failed to execute JDBC category summary query for category: " + categoryName, e);
        }

        return summary;
    }

    public List<Map<String, String>> getRecentRegistrationsByEvent(Long eventId, int limit) {
        List<Map<String, String>> attendees = new ArrayList<>();

        // Parameterized SQL selecting latest registrations for a specified event ID with limit clause
        String sql = "SELECT registration_code, participant_name, email, phone, registration_date " +
                     "FROM registrations " +
                     "WHERE event_id = ? " +
                     "ORDER BY registration_date DESC " +
                     "LIMIT ?";

        // Acquire raw JDBC connection and prepare statement
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Bind eventId to first positional placeholder
            ps.setLong(1, eventId);
            // Bind maximum row limit to second positional placeholder
            ps.setInt(2, limit);

            // Execute query and iterate through matched attendee rows
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    // Extract text columns from result set row
                    row.put("code", rs.getString("registration_code"));
                    row.put("name", rs.getString("participant_name"));
                    row.put("email", rs.getString("email"));
                    row.put("phone", rs.getString("phone"));
                    row.put("date", String.valueOf(rs.getTimestamp("registration_date")));
                    // Add mapped attendee row to output list
                    attendees.add(row);
                }
            }
        } catch (SQLException e) {
            // Wrap and rethrow SQL access failure
            throw new RuntimeException("Failed to fetch recent registrations via JDBC for event ID: " + eventId, e);
        }

        return attendees;
    }
}
