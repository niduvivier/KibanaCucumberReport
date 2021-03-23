package be.automatiqa.plugin;

import lombok.*;

import java.util.List;

@Builder
@Getter
public class CucumberReport {
    @Setter
    private Double executionId;
    private final String testCaseName;
    private final String status;
    private final Long testCaseDuration;
    private final List<String> testCaseTags;
    private final String errorType;
    private final String errorMessage;
    private @Singular final List<StepReport> stepReports;
}
