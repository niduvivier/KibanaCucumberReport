package be.automatiqa.plugin;

import be.automatiqa.infra.RestClient;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import java.sql.Timestamp;
import java.util.*;

public class CustomCucumberLogger implements ConcurrentEventListener {
    private RestClient restClient;

    private CucumberReport cucumberReport;

    private CucumberReport.CucumberReportBuilder cucumberReportBuilder;
    private StepReport.StepReportBuilder stepReportBuilder;

    private Timestamp testCaseStartTimestamp;
    private Map<UUID, Timestamp> stepStartTimestamps = new HashMap<>();
    private Map<UUID, Timestamp> stepEndTimestamps = new HashMap<>();

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        restClient = new RestClient();
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::setUpTestCaseInfos);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::addStepStartTimestamp);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::addStepEndTimestamp);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::finalizeTestCaseInfo);
    }

    private void setUpTestCaseInfos(TestCaseStarted event){
        cucumberReportBuilder = CucumberReport.builder()
                .testCaseName(event.getTestCase().getName())
                .testCaseTags(event.getTestCase().getTags());
        testCaseStartTimestamp = Timestamp.from(event.getInstant());
    }

    private void finalizeTestCaseInfo(TestCaseFinished event){
        Timestamp testCaseEndTimestamp = Timestamp.from(event.getInstant());
        cucumberReport = cucumberReportBuilder
                .errorMessage(event.getResult().getError() != null ?
                        event.getResult().getError().toString() : null)
                .testCaseDuration(testCaseEndTimestamp.getTime() - testCaseStartTimestamp.getTime())
                .build();
        restClient.postReportToELK(cucumberReport);
    }

    private void addStepStartTimestamp(TestStepStarted event){
        if(event.getTestStep() instanceof PickleStepTestStep){
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            stepReportBuilder = StepReport.builder()
                    .name(pickleStep.getStep().getText());
        }
        stepStartTimestamps.put(event.getTestStep().getId(), Timestamp.from(event.getInstant()));
    }

    private void addStepEndTimestamp(TestStepFinished event){
        cucumberReportBuilder.stepReport(stepReportBuilder.duration(10L).build());
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
