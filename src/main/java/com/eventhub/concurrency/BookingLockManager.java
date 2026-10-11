package com.eventhub.concurrency;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BookingLockManager {

    // Thread-safe map maintaining a dedicated lock monitor object for each active event ID
    private final ConcurrentHashMap<Long, Object> eventLocks = new ConcurrentHashMap<>();

    public Object getLock(Long eventId) {
        // Return existing lock object or atomically create a new one if this is the first concurrent access
        return eventLocks.computeIfAbsent(eventId, id -> new Object());
    }

    public void removeLock(Long eventId) {
        // Clean up stored lock object when an event is deleted to prevent memory leaks
        eventLocks.remove(eventId);
    }
}
