package be.automatiqa;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"be.automatiqa.plugin.CustomCucumberLogger"})
public class RunCucumberTest {
}
