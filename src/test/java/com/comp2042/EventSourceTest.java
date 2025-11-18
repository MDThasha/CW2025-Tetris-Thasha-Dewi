package com.comp2042;

import com.comp2042.Event.EventSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventSourceTest {
    @Test
    void testEventSourceExists() {
        assertNotNull(EventSource.USER);
        assertNotNull(EventSource.THREAD);
    }

    @Test
    void testEventSourceValues() {
        EventSource[] sources = EventSource.values();
        assertEquals(2, sources.length, "Should have exactly 2 event sources");
    }
}