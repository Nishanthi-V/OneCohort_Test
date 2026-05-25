Feature: Leader Dashboard functionality
  As a Leader
  I want to log in and access my dashboard
  So that I can view cohort and training data

  Background:
    Given I am on the login page
    When I enter user ID "LD001" and select role "Leader"
    And I select service line "Cloud & Data Enterprise (SRV-10002)"
    And I click the Login button
    Then I should be redirected to the leader dashboard

  # TC-LEADER-001
  Scenario: Leader dashboard loads with correct URL and KPI cards
    Then the URL should contain "/leader/"
    And the leader role badge should be visible
    And the dashboard should have KPI cards

  # TC-LEADER-002
  Scenario: Leader sidebar has at least 2 nav links including Cohorts
    Then the sidebar should have at least 2 navigation links
    And the sidebar should contain a "Cohorts" link

  # TC-LEADER-003
  Scenario: Leader login without selecting a Service Line shows validation alert
    Given I am on the login page
    When I enter user ID "LD001" and select role "Leader"
    And I click the Login button
    Then I should see alert "Please select a Service Line"
