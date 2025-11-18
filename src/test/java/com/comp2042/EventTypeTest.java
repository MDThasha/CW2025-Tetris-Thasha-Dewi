package com.comp2042;

import com.comp2042.Event.EventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {
    @Test
    void testAllEventTypesExist() {
        assertNotNull(EventType.DOWN);
        assertNotNull(EventType.LEFT);
        assertNotNull(EventType.RIGHT);
        assertNotNull(EventType.ROTATE);
    }

    @Test
    void testEventTypeValues() {
        EventType[] types = EventType.values();
        assertEquals(4, types.length, "Should have exactly 4 event types");
    }

    @Test
    void testInvalidEventTypeValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            EventType.valueOf("INVALID_TYPE");
        }, "valueOf with invalid name should throw IllegalArgumentException");
    }

    @Test
    void testEventTypeNotNull() {
        for (EventType type : EventType.values()) {
            assertNotNull(type, "Event type should not be null");
            assertNotNull(type.name(), "Event type name should not be null");
        }
    }
}