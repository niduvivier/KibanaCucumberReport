package be.automatiqa.plugin;

import be.automatiqa.infra.RestClient;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import java.sql.Timestamp;

public class CustomCucumberLogger implements ConcurrentEventListener {
    private Double executionId;

    private RestClient restClient;

    private CucumberReport cucumberReport;

    private CucumberReport.CucumberReportBuilder cucumberReportBuilder;
    private StepReport.StepReportBuilder stepReportBuilder;

    private Timestamp testCaseStartTimestamp;
    private Timestamp stepStartTimestamp;

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        restClient = new RestClient();
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::setUpTestCaseInfos);
        eventPublisher.registerHandlerFor(TestStepStarted.class, this::addStepStartTimestamp);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::addStepEndTimestamp);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::finalizeTestCaseInfo);
    }

    private void setUpTestCaseInfos(TestCaseStarted event){
        if(executionId == null){
            executionId = restClient.getLastExecutionId() + 1;
        }
        cucumberReportBuilder = CucumberReport.builder()
                .testCaseName(event.getTestCase().getName())
                .testCaseTags(event.getTestCase().getTags())
                .executionId(executionId);
        testCaseStartTimestamp = Timestamp.from(event.getInstant());
    }

    private void finalizeTestCaseInfo(TestCaseFinished event){
        Timestamp testCaseEndTimestamp = Timestamp.from(event.getInstant());
        cucumberReport = cucumberReportBuilder
                .errorType(event.getResult().getError() != null ?
                        event.getResult().getError().getClass().toString() : null)
                .errorMessage(event.getResult().getError() != null ?
                        event.getResult().getError().getMessage() : null)
                .testCaseDuration(testCaseEndTimestamp.getTime() - testCaseStartTimestamp.getTime())
                .status(event.getResult().getStatus().name())
                .build();
        restClient.postReportToELK(cucumberReport);
    }

    private void addStepStartTimestamp(TestStepStarted event){
        if(event.getTestStep() instanceof PickleStepTestStep){
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            stepReportBuilder = StepReport.builder()
                    .name(pickleStep.getStep().getText());
        }
        stepStartTimestamp = Timestamp.from(event.getInstant());
    }

    private void addStepEndTimestamp(TestStepFinished event){
        Timestamp stepEndTimestamp = Timestamp.from(event.getInstant());
        cucumberReportBuilder.stepReport(stepReportBuilder
                .duration(stepEndTimestamp.getTime() - stepStartTimestamp.getTime())
                .status(event.getResult().getStatus().name())
                .build());
    }

    public String toString(){
        StringBuilder result = new StringBuilder(String.format("Scenario [%s] start: %s", cucumberReport.getTestCaseName(), testCaseStartTimestamp));
        if(!cucumberReport.getTestCaseTags().isEmpty()){
            result.append("\nScenario tags: ");
            for (String tag : cucumberReport.getTestCaseTags()){
                result.append(String.format("%s ", tag));
            }
        }
        result.append("\nScenario end: ");
        return result.toString();
    }

}
