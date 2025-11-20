package com.comp2042.Event;

/** what correlates to movement in the game*/
public final class MoveEvent {

    /** event type*/
    private final EventType eventType;

    /** event source, user or system*/
    private final EventSource eventSource;

    /**Create a MoveEvent.
     * @param eventType the type of event
     * @param eventSource the origin of the event */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /** @return the event type (movement/action) */
    public EventType getEventType() {
        return eventType;
    }

    /** @return the source that produced this event */
    public EventSource getEventSource() {
        return eventSource;
    }
}
