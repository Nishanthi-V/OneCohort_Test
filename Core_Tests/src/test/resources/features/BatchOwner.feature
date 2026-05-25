Feature: Batch Owner Dashboard functionality
  As a Batch Owner
  I want to log in and access my dashboard
  So that I can manage cohorts and track training progress

  Background:
    Given I am on the login page
    When I enter user ID "SA001" and select role "Batch Owner"
    And I select service line "Cloud & Data Enterprise (SRV-10002)"
    And I enter POC ID "USR-40002"
    And I click the Login button
    Then I should be redirected to the batch owner dashboard

  # TC-BO-002
  Scenario: Batch Owner dashboard shows all 4 cohort summary cards
    Then the "Total Cohorts" card should be visible
    And the "Active" card should be visible
    And the "Completed" card should be visible
    And the "Upcoming" card should be visible

  # TC-BO-005
  Scenario: Search bar filters the cohorts table
    When I click the Cohorts nav link
    And I wait for the search bar to be visible
    And I type "INT" in the search bar
    Then the cohorts table should have at least 1 row
    When I clear the search bar
    Then the cohorts table should have at least 1 row

  # TC-BO-008
  Scenario: Batch Owner login without POC ID shows validation alert
    Given I am on the login page
    When I enter user ID "SA001" and select role "Batch Owner"
    And I select service line "Cloud & Data Enterprise (SRV-10002)"
    And I click the Login button
    Then I should see alert "Please enter a POC ID"
