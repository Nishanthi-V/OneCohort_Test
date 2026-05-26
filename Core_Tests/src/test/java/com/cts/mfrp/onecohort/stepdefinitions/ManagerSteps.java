package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import io.cucumber.java.en.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

public class ManagerSteps {

    private final TestContext context;
    private ManagerDashboardPage dashPage;

    public ManagerSteps(TestContext context) {
        this.context = context;
    }

    @Then("the role badge should display {string}")
    public void theRoleBadgeShouldDisplay(String expectedRole) {
        dashPage = new ManagerDashboardPage(context.driver);
        dashPage.waitForDashboardLoad();
        Assert.assertEquals(dashPage.getRoleText(), expectedRole,
                "Role badge should display '" + expectedRole + "'");
    }

    @Then("the KPI card {string} should be visible")
    public void theKpiCardShouldBeVisible(String cardName) {
        if (dashPage == null) dashPage = new ManagerDashboardPage(context.driver);
        Assert.assertTrue(dashPage.isKpiCardPresent(cardName),
                "KPI card '" + cardName + "' should be visible");
    }

    /**
     * Cohorts nav: Manager → /manage-cohorts, Batch Owner → /cohorts.
     * URL segments come from AppConstants — no inline string literals.
     */
    @When("I click the Cohorts nav link")
    public void iClickTheCohortsNavLink() {
        String currentUrl = context.driver.getCurrentUrl();

        if (currentUrl.contains(AppConstants.URL_BATCH_OWNER)) {
            BatchOwnerDashboardPage boPage = new BatchOwnerDashboardPage(context.driver);
            boPage.getSidebarCohortsLinkElement().click();
            context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_COHORTS));
        } else {
            if (dashPage == null) dashPage = new ManagerDashboardPage(context.driver);
            dashPage.clickManageCohortsNav();
            context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_MANAGE_COHORTS));
        }
    }

    @When("I click the Dashboard nav link")
    public void iClickTheDashboardNavLink() {
        if (dashPage == null) dashPage = new ManagerDashboardPage(context.driver);
        dashPage.clickDashboardNav();
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_DASHBOARD));
    }
}
