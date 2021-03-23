@myAwesomeFeature
Feature: My test Feature

  @myTag @mySecondTag
  Scenario: My first fake test
    Given a fake setup
    When faking a step
    Then the result should be fake

  Scenario: My second fake test
    Given another fake setup
    When faking a step
    Then the result should be fake

  Scenario: A passing test
    Given a fake setup
    When faking a step
    Then the result should be correct
