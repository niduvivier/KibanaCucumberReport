package be.automatiqa.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class CucumberReport {
    private String testCaseName;
    private Long testCaseDuration;
    private List<String> testCaseTags;

    public void sendToELK(){
        //TODO send (this) to elasticsearch
    }
}
