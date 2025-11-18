package com.comp2042;

import com.comp2042.Event.EventSource;
import com.comp2042.Event.EventType;
import com.comp2042.Event.MoveEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveEventTest {
    @Test
    void testMoveEventGetEventSource() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testMoveEventWithSystemSource() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        assertEquals(EventSource.THREAD, event.getEventSource());
    }

    @Test
    void testMultipleMoveEvents() {
        MoveEvent event1 = new MoveEvent(EventType.LEFT, EventSource.USER);
        MoveEvent event2 = new MoveEvent(EventType.RIGHT, EventSource.USER);

        assertNotEquals(event1.getEventType(), event2.getEventType());
    }
}