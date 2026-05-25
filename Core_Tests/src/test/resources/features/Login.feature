Feature: Login functionality for all roles
  As a user of OneCohort
  I want to log in with my credentials
  So that I can access my role-based dashboard

  # TC-AUTH-001
  Scenario: Super Admin login with valid User ID redirects to dashboard
    Given I am on the login page
    When I enter user ID "SA001" and select role "Super Admin"
    And I click the Login button
    Then I should be redirected to the super admin dashboard

  # TC-AUTH-002
  Scenario: Login with empty User ID shows validation alert
    Given I am on the login page
    When I select role "Super Admin"
    And I click the Login button
    Then I should see alert "Please enter a User ID"

  # TC-AUTH-006
  Scenario: CR login without Cohort ID shows validation alert
    Given I am on the login page
    When I enter user ID "CR001" and select role "CR"
    And I click the Login button
    Then I should see alert "Please enter a Cohort ID"

# TC-SC-004
Scenario: Super Admin System Config page shows exactly 4 configuration cards
  Given I am on the login page
  When I enter user ID "SA001" and select role "Super Admin"
  And I click the Login button
  Then I should be redirected to the super admin dashboard
  When I navigate to the system config page
  Then the system config page should show exactly 4 configuration cards