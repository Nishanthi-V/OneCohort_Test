Feature: CR Dashboard functionality
  As a Class Representative
  I want to log in and view my cohort dashboard
  So that I can track training progress in read-only mode

  Background:
    Given I am on the login page
    When I enter user ID "SA001" and select role "CR"
    And I enter cohort ID "INTCLD024"
    And I click the Login button
    Then I should be redirected to the CR dashboard

  # TC-CR-004
  Scenario: CR dashboard has no Create, Edit or Delete buttons
    Then no CRUD buttons should be visible on the dashboard

  # TC-CR-006
  Scenario: Qualifier, Interim and Final evaluation sections are visible
    Then the "Qualifier" evaluation section should be visible
    And the "Interim" evaluation section should be visible
    And the "Final" evaluation section should be visible
