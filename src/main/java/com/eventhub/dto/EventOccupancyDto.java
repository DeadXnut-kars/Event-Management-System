package com.eventhub.dto;

public class EventOccupancyDto {

    // Event primary key identifier
    private Long eventId;
    // Event name
    private String title;
    // Category classification (Technical, Cultural, Workshop, etc.)
    private String category;
    // Maximum seat capacity
    private int capacity;
    // Confirmed registered attendees count
    private int registeredCount;
    // Remaining available seats
    private int seatsRemaining;
    // Percentage of capacity filled (0.0 to 100.0)
    private double occupancyRate;

    public EventOccupancyDto() {
    }

    public EventOccupancyDto(Long eventId, String title, String category, int capacity,
                             int registeredCount, int seatsRemaining, double occupancyRate) {
        this.eventId = eventId;
        this.title = title;
        this.category = category;
        this.capacity = capacity;
        this.registeredCount = registeredCount;
        this.seatsRemaining = seatsRemaining;
        this.occupancyRate = occupancyRate;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(int seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }
}
