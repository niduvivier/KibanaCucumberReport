package be.automatiqa.plugin;

import lombok.*;

@Builder
@Getter
public class StepReport {
    private String name;
    private String status;
    private Long duration;
}