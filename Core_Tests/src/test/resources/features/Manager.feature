Feature: Manager Dashboard functionality
  As a Manager
  I want to log in and access my dashboard
  So that I can view service lines, learning paths and cohort data

  Background:
    Given I am on the login page
    When I enter user ID "MG001" and select role "Manager"
    And I select service line "Cloud & Data Enterprise (SRV-10002)"
    And I click the Login button
    Then I should be redirected to the manager dashboard

  # TC-MGR-001
  Scenario: Manager dashboard loads with correct URL, role badge and KPI cards
    Then the URL should contain "/manager/"
    And the role badge should display "Manager"
    And the KPI card "Service Lines" should be visible
    And the KPI card "Learning Paths" should be visible
    And the KPI card "Avg. Completion Rate" should be visible

  # TC-MGR-002
  Scenario: Manager can navigate to Cohorts and back to Dashboard via sidebar
    When I click the Cohorts nav link
    Then the URL should contain "/manager/"
    When I click the Dashboard nav link
    Then the URL should contain "/dashboard"

  # TC-MGR-006
  Scenario: Manager login without selecting a Service Line shows validation alert
    Given I am on the login page
    When I enter user ID "MG001" and select role "Manager"
    And I click the Login button
    Then I should see alert "Please select a Service Line"
