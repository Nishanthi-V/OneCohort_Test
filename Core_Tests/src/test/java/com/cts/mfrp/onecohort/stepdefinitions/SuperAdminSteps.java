package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class SuperAdminSteps {

    private final TestContext context;

    public SuperAdminSteps(TestContext context) {
        this.context = context;
    }

    /**
     * FIX: Instead of navigating directly to the URL (which requires an
     * active session and redirects to login otherwise), we click the
     * System Config menu item from the Super Admin dashboard.
     * This matches exactly what SystemConfigTest.java does in TestNG.
     */
    @When("I navigate to the system config page")
    public void iNavigateToSystemConfigPage() {
        // Wait for super admin dashboard to be loaded
        new WebDriverWait(context.driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.urlContains("/super-admin"));

        // Click System Config menu item from the sidebar/nav
        By systemConfigLink = By.xpath(
                "//a[contains(normalize-space(),'System Config')] " +
                        "| //a[contains(@href,'system-config')] " +
                        "| //*[contains(normalize-space(),'System Config') " +
                        "and (self::a or self::button or self::li)]");

        new WebDriverWait(context.driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(systemConfigLink));

        context.driver.findElement(systemConfigLink).click();

        // Wait for system config page to load
        new WebDriverWait(context.driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.urlContains("system-config"));

        System.out.println("[SuperAdminSteps] Navigated to: " + context.driver.getCurrentUrl());
    }

    /**
     * FIX: Uses SystemConfigPage.java's exact locator — By.cssSelector(".config-card")
     * which is the same one that passes in TestNG SystemConfigTest.java.
     * Also waits for the page heading to confirm the page has fully loaded
     * before counting cards.
     */
    @Then("the system config page should show exactly 4 configuration cards")
    public void systemConfigPageShowsFourCards() {
        // Use SystemConfigPage which has the correct .config-card locator
        SystemConfigPage systemConfigPage = new SystemConfigPage(context.driver);
        systemConfigPage.waitForPageLoad();

        int cardCount = systemConfigPage.getConfigCardCount();

        System.out.println("[SuperAdminSteps] Config cards found: " + cardCount);

        Assert.assertEquals(cardCount, 4,
                "System Config should show exactly 4 cards. Found: " + cardCount);
        System.out.println("PASS - System Config shows exactly 4 configuration cards.");
    }
}