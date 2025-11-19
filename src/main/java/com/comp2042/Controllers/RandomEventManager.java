package com.comp2042.Controllers;

import com.comp2042.Controllers.GuiController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import java.util.Random;

public class RandomEventManager {

    private final GuiController guiController;
    private final Random random;

    // Event timing constants
    private static final int EVENT_START_TIME = 121;  // Start events after 121 seconds
    private static final int EVENT_DURATION = 7;     // Each event lasts 10 seconds
    private static final int MIN_EVENT_INTERVAL = 10; // Minimum 10s between events
    private static final int MAX_EVENT_INTERVAL = 50; // Maximum 50s between events

    // Event state flags
    private boolean eventsEnabled = false;
    private boolean eventActive = false;
    private EventType currentEvent = null;

    // Timelines
    private Timeline eventStartTimeline;
    private Timeline eventSchedulerTimeline;
    private Timeline eventDurationTimeline;

    // Original values for restoration
    private int originalSpeed;
    private boolean controlsReversed = false;

    public enum EventType {
        REVERSE_CONTROLS,
        BLACKOUT,
        SPEED_BOOST
    }

    public RandomEventManager(GuiController guiController) {
        this.guiController = guiController;
        this.random = new Random();
    }

    /**
     * Start the event system - waits 120s then begins scheduling random events
     */
    public void start() {
        // Wait 120 seconds before enabling events
        eventStartTimeline = new Timeline(new KeyFrame(Duration.seconds(EVENT_START_TIME), e -> {
            eventsEnabled = true;
            scheduleNextEvent();
        }));
        eventStartTimeline.setCycleCount(1);
        eventStartTimeline.play();
    }

    /**
     * Schedule the next random event after a random interval
     */
    private void scheduleNextEvent() {
        if (!eventsEnabled || eventActive) return;

        // Random interval between 15-30 seconds
        int interval = MIN_EVENT_INTERVAL + random.nextInt(MAX_EVENT_INTERVAL - MIN_EVENT_INTERVAL + 1);

        eventSchedulerTimeline = new Timeline(new KeyFrame(Duration.seconds(interval), e -> {
            triggerRandomEvent();
        }));
        eventSchedulerTimeline.setCycleCount(1);
        eventSchedulerTimeline.play();
    }

    /**
     * Trigger a random event
     */
    private void triggerRandomEvent() {
        if (eventActive) return;

        // Pick a random event
        EventType[] events = EventType.values();
        currentEvent = events[random.nextInt(events.length)];

        eventActive = true;

        // Activate the event
        switch (currentEvent) {
            case REVERSE_CONTROLS:
                activateReverseControls();
                break;
            case BLACKOUT:
                activateBlackout();
                break;
            case SPEED_BOOST:
                activateSpeedBoost();
                break;
        }

        // Schedule event end after 10 seconds
        eventDurationTimeline = new Timeline(new KeyFrame(Duration.seconds(EVENT_DURATION), e -> {
            endCurrentEvent();
        }));
        eventDurationTimeline.setCycleCount(1);
        eventDurationTimeline.play();
    }

    /**
     * REVERSE CONTROLS EVENT
     */
    private void activateReverseControls() {
        controlsReversed = true;
        Platform.runLater(() -> {
            guiController.showEventNotification("CONTROLS INVERTED!", "warning");
        });
    }

    private void deactivateReverseControls() {
        controlsReversed = false;
        Platform.runLater(() -> {
            guiController.showEventNotification("Controls Normal", "success");
        });
    }

    /**
     * BLACKOUT EVENT
     */
    private void activateBlackout() {
        Platform.runLater(() -> {
            guiController.activateBlackout();
            guiController.showEventNotification("BLACKOUT!", "warning");
        });
    }

    private void deactivateBlackout() {
        Platform.runLater(() -> {
            guiController.deactivateBlackout();
            guiController.showEventNotification("Vision Restored", "success");
        });
    }

    /**
     * SPEED BOOST EVENT
     */
    private void activateSpeedBoost() {
        originalSpeed = guiController.getCurrentSpeed();
        Platform.runLater(() -> {
            guiController.setTemporarySpeed(90);
            guiController.showEventNotification("SPEED BOOST!", "warning");
        });
    }

    private void deactivateSpeedBoost() {
        Platform.runLater(() -> {
            guiController.restoreNormalSpeed();
            guiController.showEventNotification("Speed Normal", "success");
        });
    }

    /**
     * End the current active event
     */
    private void endCurrentEvent() {
        if (!eventActive || currentEvent == null) return;

        switch (currentEvent) {
            case REVERSE_CONTROLS:
                deactivateReverseControls();
                break;
            case BLACKOUT:
                deactivateBlackout();
                break;
            case SPEED_BOOST:
                deactivateSpeedBoost();
                break;
        }

        eventActive = false;
        currentEvent = null;

        // Schedule next event
        scheduleNextEvent();
    }

    /**
     * Check if controls are currently reversed
     */
    public boolean areControlsReversed() {
        return controlsReversed;
    }

    /**
     * Stop all event timelines
     */
    public void stop() {
        eventsEnabled = false;

        if (eventStartTimeline != null) eventStartTimeline.stop();
        if (eventSchedulerTimeline != null) eventSchedulerTimeline.stop();
        if (eventDurationTimeline != null) eventDurationTimeline.stop();

        // Clean up any active events
        if (eventActive) {
            endCurrentEvent();
        }
    }

    /**
     * Pause event timelines
     */
    public void pause() {
        if (eventStartTimeline != null) eventStartTimeline.pause();
        if (eventSchedulerTimeline != null) eventSchedulerTimeline.pause();
        if (eventDurationTimeline != null) eventDurationTimeline.pause();
    }

    /**
     * Resume event timelines
     */
    public void resume() {
        if (eventStartTimeline != null && eventStartTimeline.getStatus() == Timeline.Status.PAUSED) {
            eventStartTimeline.play();
        }
        if (eventSchedulerTimeline != null && eventSchedulerTimeline.getStatus() == Timeline.Status.PAUSED) {
            eventSchedulerTimeline.play();
        }
        if (eventDurationTimeline != null && eventDurationTimeline.getStatus() == Timeline.Status.PAUSED) {
            eventDurationTimeline.play();
        }
    }
}