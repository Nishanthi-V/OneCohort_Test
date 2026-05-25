package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

public class LoginSteps {

    private final TestContext context;
    private LoginPage loginPage;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        context.driver.get(ConfigReader.getBaseUrl());
        loginPage = new LoginPage(context.driver);
    }

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

    @Then("I should see alert {string}")
    public void iShouldSeeAlert(String expectedMessage) {
        String actual = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(actual, expectedMessage,
                "Alert message mismatch");
    }

    @Then("I should be redirected to the super admin dashboard")
    public void iShouldBeRedirectedToSuperAdminDashboard() {
        context.getWait().until(ExpectedConditions.urlContains("/super-admin"));
        Assert.assertTrue(context.driver.getCurrentUrl().contains("/super-admin"),
                "URL should contain /super-admin. Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the manager dashboard")
    public void iShouldBeRedirectedToManagerDashboard() {
        context.getWait().until(ExpectedConditions.urlContains("/manager/"));
        Assert.assertTrue(context.driver.getCurrentUrl().contains("/manager/"),
                "URL should contain /manager/. Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the leader dashboard")
    public void iShouldBeRedirectedToLeaderDashboard() {
        context.getWait().until(ExpectedConditions.urlContains("/leader/"));
        Assert.assertTrue(context.driver.getCurrentUrl().contains("/leader/"),
                "URL should contain /leader/. Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the batch owner dashboard")
    public void iShouldBeRedirectedToBatchOwnerDashboard() {
        context.getWait().until(d -> !d.getCurrentUrl().contains("login"));
        Assert.assertTrue(context.driver.getCurrentUrl().contains("/batch-owner/"),
                "URL should contain /batch-owner/. Got: " + context.driver.getCurrentUrl());
    }

    @Then("I should be redirected to the CR dashboard")
    public void iShouldBeRedirectedToCRDashboard() {
        context.getWait().until(ExpectedConditions.urlContains("/cr/"));
        Assert.assertTrue(context.driver.getCurrentUrl().contains("/cr/"),
                "URL should contain /cr/. Got: " + context.driver.getCurrentUrl());
    }

    @Then("the URL should contain {string}")
    public void theUrlShouldContain(String urlFragment) {
        context.getWait().until(ExpectedConditions.urlContains(urlFragment));
        Assert.assertTrue(context.driver.getCurrentUrl().contains(urlFragment),
                "URL should contain " + urlFragment + ". Got: " + context.driver.getCurrentUrl());
    }
}