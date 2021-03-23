package be.automatiqa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class MyStepdefs {
    @Given("a fake setup")
    public void aFakeSetup() {
    }

    @When("faking a step")
    public void fakingAStep() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Then("the result should be fake")
    public void theResultShouldBeFake() {
        Assert.assertNotNull("This should not be null", null);
    }

    @Given("another fake setup")
    public void anotherFakeSetup() throws Exception {
        throw new Exception();
    }

    @Then("the result should be correct")
    public void theResultShouldBeCorrect() {
        Assert.assertTrue(true);
    }
}
