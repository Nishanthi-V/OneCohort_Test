package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
// SoftAssert accessed via context.softAssert (injected by Hooks.setUp)

public class LoginSteps {

    private final TestContext context;
    private LoginPage loginPage;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        context.driver.get(ConfigReader.getBaseUrl());
        loginPage = new LoginPage(context.driver);
    }

    // ── Excel-driven full login (reads ALL credentials from LoginData.xlsx) ───

    /**
     * Single step that logs in completely using data from LoginData.xlsx.
     * The role name is the key; UserId, ServiceLine, PocId, CohortId are
     * looked up automatically — no hardcoding needed in feature files.
     */
    @When("I login with credentials for {string}")
    public void iLoginWithCredentialsFor(String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(role));
        loginPage.selectRole(role);
        String serviceLine = TestDataProvider.getServiceLineForRole(role);
        if (!serviceLine.isEmpty()) loginPage.selectServiceLine(serviceLine);
        String pocId = TestDataProvider.getPocIdForRole(role);
        if (!pocId.isEmpty()) loginPage.enterPocId(pocId);
        String cohortId = TestDataProvider.getCohortIdForRole(role);
        if (!cohortId.isEmpty()) loginPage.enterCohortId(cohortId);
        loginPage.clickLoginButton();
    }

    // ── Partial-login steps for validation / negative scenarios ──────────────

    /** Enters UserId + role only — no service line (triggers service-line alert). */
    @When("I login without service line for {string}")
    public void iLoginWithoutServiceLineFor(String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(role));
        loginPage.selectRole(role);
        // Service line intentionally omitted
    }

    /** Enters UserId + role + service line — no POC ID (triggers POC alert). */
    @When("I login without POC ID for {string}")
    public void iLoginWithoutPocIdFor(String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(role));
        loginPage.selectRole(role);
        String serviceLine = TestDataProvider.getServiceLineForRole(role);
        if (!serviceLine.isEmpty()) loginPage.selectServiceLine(serviceLine);
        // POC ID intentionally omitted
    }

    /** Enters UserId + role only — no cohort ID (triggers cohort-ID alert). */
    @When("I login without cohort ID for {string}")
    public void iLoginWithoutCohortIdFor(String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(role));
        loginPage.selectRole(role);
        // Cohort ID intentionally omitted
    }

    // ── Individual field steps (still available for fine-grained scenarios) ───

    @When("I enter user ID {string} and select role {string}")
    public void iEnterUserIdAndSelectRole(String userId, String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.enterUserId(userId);
        loginPage.selectRole(role);
    }

    @When("I select role {string}")
    public void iSelectRole(String role) {
        loginPage = new LoginPage(context.driver);
        loginPage.selectRole(role);
    }

    @And("I select service line {string}")
    public void iSelectServiceLine(String serviceLineId) {
        loginPage.selectServiceLine(serviceLineId);
    }

    @And("I enter POC ID {string}")
    public void iEnterPocId(String pocId) {
        loginPage.enterPocId(pocId);
    }

    @And("I enter cohort ID {string}")
    public void iEnterCohortId(String cohortId) {
        loginPage.enterCohortId(cohortId);
    }

    @And("I click the Login button")
    public void iClickTheLoginButton() {
        loginPage.clickLoginButton();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Then("I should see alert {string}")
    public void iShouldSeeAlert(String expectedMessage) {
        String actual = loginPage.acceptAlertAndGetMessage();
        context.softAssert.assertEquals(actual, expectedMessage, "Alert message mismatch");
    }

    @Then("I should be redirected to the super admin dashboard")
    public void iShouldBeRedirectedToSuperAdminDashboard() {
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_SUPER_ADMIN));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(AppConstants.URL_SUPER_ADMIN),
                "URL should contain " + AppConstants.URL_SUPER_ADMIN
                + ". Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the manager dashboard")
    public void iShouldBeRedirectedToManagerDashboard() {
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_MANAGER));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(AppConstants.URL_MANAGER),
                "URL should contain " + AppConstants.URL_MANAGER
                + ". Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the leader dashboard")
    public void iShouldBeRedirectedToLeaderDashboard() {
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_LEADER));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(AppConstants.URL_LEADER),
                "URL should contain " + AppConstants.URL_LEADER
                + ". Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the batch owner dashboard")
    public void iShouldBeRedirectedToBatchOwnerDashboard() {
        context.getWait().until(d -> !d.getCurrentUrl().contains("login"));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(AppConstants.URL_BATCH_OWNER),
                "URL should contain " + AppConstants.URL_BATCH_OWNER
                + ". Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the CR dashboard")
    public void iShouldBeRedirectedToCRDashboard() {
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_CR));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(AppConstants.URL_CR),
                "URL should contain " + AppConstants.URL_CR
                + ". Got: " + context.driver.getCurrentUrl());
    }

    @Then("the URL should contain {string}")
    public void theUrlShouldContain(String urlFragment) {
        context.getWait().until(ExpectedConditions.urlContains(urlFragment));
        context.softAssert.assertTrue(context.driver.getCurrentUrl().contains(urlFragment),
                "URL should contain " + urlFragment + ". Got: " + context.driver.getCurrentUrl());
    }
}
