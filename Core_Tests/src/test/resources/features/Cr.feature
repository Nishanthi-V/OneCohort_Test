Feature: CR Dashboard functionality
  As a Class Representative
  I want to log in and view my cohort dashboard
  So that I can track training progress in read-only mode

  # Background: All credentials (UserId, CohortId) are read from LoginData.xlsx.
  # No hardcoded values appear in this feature file.
  Background:
    Given I am on the login page
    When I login with credentials for "CR"
    Then I should be redirected to the CR dashboard

  # TC-CR-004
  Scenario: CR dashboard has no Create, Edit or Delete buttons
    Then no CRUD buttons should be visible on the dashboard

  # TC-CR-006
  Scenario: Qualifier, Interim and Final evaluation sections are visible
    Then the "Qualifier" evaluation section should be visible
    And the "Interim" evaluation section should be visible
    And the "Final" evaluation section should be visible
