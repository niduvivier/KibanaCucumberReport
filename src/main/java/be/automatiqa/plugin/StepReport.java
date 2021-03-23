package be.automatiqa.plugin;

import lombok.*;

@Builder
@Getter
public class StepReport {
    private final String name;
    private final String status;
    private final Long duration;
}
