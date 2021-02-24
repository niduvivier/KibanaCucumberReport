package be.automatiqa.plugin;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;

import java.sql.Timestamp;
import java.util.*;

public class CustomCucumberLogger implements ConcurrentEventListener {
    private CucumberReport cucumberReport;

    private Timestamp testCaseStartTimestamp;
    private Map<UUID, Timestamp> stepStartTimestamps = new HashMap<>();
    private Map<UUID, Timestamp> stepEndTimestamps = new HashMap<>();

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::setUpTestCaseInfos);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::addStepStartTimestamp);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::addStepEndTimestamp);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::finalizeTestCaseInfo);
    }

    private void setUpTestCaseInfos(TestCaseStarted event){
        cucumberReport = new CucumberReport();
        cucumberReport.setTestCaseName(event.getTestCase().getName());
        cucumberReport.setTestCaseTags(event.getTestCase().getTags());
        testCaseStartTimestamp = Timestamp.from(event.getInstant());
    }

    private void finalizeTestCaseInfo(TestCaseFinished event){
        System.out.println(this);
        Timestamp testCaseEndTimestamp = Timestamp.from(event.getInstant());
        cucumberReport.setTestCaseDuration(testCaseEndTimestamp.getTime() - testCaseStartTimestamp.getTime());
    }

    private void addStepStartTimestamp(TestStepStarted event){
        stepStartTimestamps.put(event.getTestStep().getId(), Timestamp.from(event.getInstant()));
    }

    private void addStepEndTimestamp(TestStepFinished event){
        stepEndTimestamps.put(event.getTestStep().getId(), Timestamp.from(event.getInstant()));
    }

    public String toString(){
        StringBuilder result = new StringBuilder(String.format("Scenario [%s] start: %s", cucumberReport.getTestCaseName(), testCaseStartTimestamp));
        if(!cucumberReport.getTestCaseTags().isEmpty()){
            result.append("\nScenario tags: ");
            for (String tag : cucumberReport.getTestCaseTags()){
                result.append(String.format("%s ", tag));
            }
        }
        for (UUID uuid:stepEndTimestamps.keySet()){
            result.append(String.format("\nStep %s start: %s", uuid, stepStartTimestamps.get(uuid)));
        }
        result.append("\nScenario end: ");
        return result.toString();
    }

}
