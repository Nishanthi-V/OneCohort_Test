package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class SuperAdminSteps {

    private final TestContext context;

    public SuperAdminSteps(TestContext context) {
        this.context = context;
    }

    /**
     * Clicks the System Config menu item from the Super Admin dashboard.
     * All wait durations are driven by AppConstants.NAVIGATION_WAIT — no magic numbers.
     */
    @When("I navigate to the system config page")
    public void iNavigateToSystemConfigPage() {
        new WebDriverWait(context.driver, Duration.ofSeconds(AppConstants.NAVIGATION_WAIT))
                .until(ExpectedConditions.urlContains(AppConstants.URL_SUPER_ADMIN));

        By systemConfigLink = By.xpath(
                "//a[contains(normalize-space(),'System Config')] " +
                "| //a[contains(@href,'system-config')] " +
                "| //*[contains(normalize-space(),'System Config') " +
                "and (self::a or self::button or self::li)]");

        new WebDriverWait(context.driver, Duration.ofSeconds(AppConstants.NAVIGATION_WAIT))
                .until(ExpectedConditions.elementToBeClickable(systemConfigLink));

        context.driver.findElement(systemConfigLink).click();

        new WebDriverWait(context.driver, Duration.ofSeconds(AppConstants.NAVIGATION_WAIT))
                .until(ExpectedConditions.urlContains(AppConstants.URL_SYSTEM_CONFIG));

        System.out.println("[SuperAdminSteps] Navigated to: " + context.driver.getCurrentUrl());
    }

    /**
     * Asserts the system config page shows exactly AppConstants.SYSTEM_CONFIG_CARD_COUNT cards.
     * The expected count is a constant — not hardcoded inline.
     */
    @Then("the system config page should show exactly 4 configuration cards")
    public void systemConfigPageShowsFourCards() {
        SystemConfigPage systemConfigPage = new SystemConfigPage(context.driver);
        systemConfigPage.waitForPageLoad();

        int cardCount = systemConfigPage.getConfigCardCount();
        System.out.println("[SuperAdminSteps] Config cards found: " + cardCount);

        Assert.assertEquals(cardCount, AppConstants.SYSTEM_CONFIG_CARD_COUNT,
                "System Config should show exactly " + AppConstants.SYSTEM_CONFIG_CARD_COUNT
                + " cards. Found: " + cardCount);
        System.out.println("PASS - System Config shows exactly "
                + AppConstants.SYSTEM_CONFIG_CARD_COUNT + " configuration cards.");
    }
}
