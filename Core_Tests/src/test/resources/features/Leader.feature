Feature: Leader Dashboard functionality
  As a Leader
  I want to log in and access my dashboard
  So that I can view cohort and training data

  # Background: All credentials (UserId, ServiceLine) are read from LoginData.xlsx.
  # No hardcoded values appear in this feature file.
  Background:
    Given I am on the login page
    When I login with credentials for "Leader"
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
  # Validation: Leader role requires a Service Line.
  # UserId is read from LoginData.xlsx; Service Line is intentionally omitted.
  Scenario: Leader login without selecting a Service Line shows validation alert
    Given I am on the login page
    When I login without service line for "Leader"
    And I click the Login button
    Then I should see alert "Please select a Service Line"
