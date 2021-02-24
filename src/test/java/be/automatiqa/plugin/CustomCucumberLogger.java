package be.automatiqa.plugin;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomCucumberLogger implements EventListener {
    private Timestamp testCaseStartTimestamp;
    private Timestamp testCaseEndTimestamp;
    private Map<UUID, Timestamp> stepStartTimestamps = new HashMap<>();
    private Map<UUID, Timestamp> stepEndTimestamps = new HashMap<>();

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::getTestCaseStartTimestamp);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::getTestCaseEndTimestamp);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::addStepStartTimestamp);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::addStepEndTimestamp);
    }

    private void getTestCaseStartTimestamp(TestCaseStarted event){
        this.testCaseStartTimestamp = new Timestamp(System.currentTimeMillis());
    }

    private void getTestCaseEndTimestamp(TestCaseFinished event){
        this.testCaseEndTimestamp = new Timestamp(System.currentTimeMillis());
    }

    private void addStepStartTimestamp(TestStepStarted event){
        this.stepStartTimestamps.put(event.getTestStep().getId(), new Timestamp(System.currentTimeMillis()));
    }

    private void addStepEndTimestamp(TestStepFinished event){
        this.stepEndTimestamps.put(event.getTestStep().getId(), new Timestamp(System.currentTimeMillis()));
    }


}
