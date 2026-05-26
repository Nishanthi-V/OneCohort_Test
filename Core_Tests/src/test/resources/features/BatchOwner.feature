Feature: Batch Owner Dashboard functionality
  As a Batch Owner
  I want to log in and access my dashboard
  So that I can manage cohorts and track training progress

  # Background: All credentials (UserId, ServiceLine, PocId) are read from LoginData.xlsx.
  # No hardcoded values appear in this feature file.
  Background:
    Given I am on the login page
    When I login with credentials for "Batch Owner"
    Then I should be redirected to the batch owner dashboard

  # TC-BO-002
  Scenario: Batch Owner dashboard shows all 4 cohort summary cards
    Then the "Total Cohorts" card should be visible
    And the "Active" card should be visible
    And the "Completed" card should be visible
    And the "Upcoming" card should be visible

  # TC-BO-005
  # Search term is read from the SearchTerm column in LoginData.xlsx — not hardcoded.
  Scenario: Search bar filters the cohorts table
    When I click the Cohorts nav link
    And I wait for the search bar to be visible
    And I search using the batch owner search term
    Then the cohorts table should have at least 1 row
    When I clear the search bar
    Then the cohorts table should have at least 1 row

  # TC-BO-008
  # Validation: Batch Owner requires a POC ID.
  # UserId and ServiceLine are from LoginData.xlsx; POC ID is intentionally omitted.
  Scenario: Batch Owner login without POC ID shows validation alert
    Given I am on the login page
    When I login without POC ID for "Batch Owner"
    And I click the Login button
    Then I should see alert "Please enter a POC ID"
