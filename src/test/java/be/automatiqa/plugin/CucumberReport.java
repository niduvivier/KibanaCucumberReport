package be.automatiqa.plugin;

import lombok.*;

import java.util.List;

@Builder
@Getter
public class CucumberReport {
    private String testCaseName;
    private Long testCaseDuration;
    private List<String> testCaseTags;
    private String errorMessage;
    private @Singular List<StepReport> stepReports;
}
