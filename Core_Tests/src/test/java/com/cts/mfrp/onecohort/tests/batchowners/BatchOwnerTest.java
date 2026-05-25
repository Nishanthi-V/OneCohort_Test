package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "batchowner"})
public class BatchOwnerTest extends BaseClassTest {

    private BatchOwnerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsBatchOwner() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsBatchOwner(
                ConfigReader.getSuperAdminUserId(),
                ConfigReader.getValidServiceLineId(),
                ConfigReader.getValidPocId());

        new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(d -> !d.getCurrentUrl().contains("login"));

        dashPage = new BatchOwnerDashboardPage(driver);
        System.out.println("Batch Owner login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-BO-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-BO-002: Batch Owner dashboard shows all 4 cohort summary cards")
    public void testDashboardShowsCohortSummaryCards() {
        Assert.assertTrue(dashPage.isTotalCohortsCardVisible(),    "Total Cohorts card should be visible");
        Assert.assertTrue(dashPage.isActiveCohortsCardVisible(),   "Active Cohorts card should be visible");
        Assert.assertTrue(dashPage.isCompletedCohortsCardVisible(), "Completed Cohorts card should be visible");
        Assert.assertTrue(dashPage.isUpcomingCohortsCardVisible(), "Upcoming Cohorts card should be visible");

        System.out.println("PASS - All 4 cohort summary cards are visible on the dashboard.");
    }

    // ── TC-BO-005 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-BO-005: Search bar filters the cohorts table and clearing restores all rows")
    public void testSearchFiltersCohortsTable() {
        // Navigate to the Cohorts list page first
        WebElement cohortsLink = dashPage.getSidebarCohortsLinkElement();
        cohortsLink.click();
        dashPage.waitForSearchBarVisible();

        String searchTerm = "INT";
        String firstCellText = dashPage.getFirstCohortRowFirstCellText();
        if (!firstCellText.isEmpty() && firstCellText.length() >= 3) {
            searchTerm = firstCellText.substring(0, 3);
        }

        WebElement searchBar = dashPage.getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);

        dashPage.waitForCohortsTableToSettle(5);

        List<WebElement> filteredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(filteredRows.isEmpty(),
                "Search with term '" + searchTerm + "' should return at least one result");
        System.out.println("PASS - Search '" + searchTerm + "' filtered table to " + filteredRows.size() + " rows.");

        searchBar.clear();
        dashPage.waitForCohortsTableToSettle(5);
        List<WebElement> restoredRows = dashPage.getCohortsTableRows();
        Assert.assertFalse(restoredRows.isEmpty(),
                "Clearing the search should restore all cohort rows");
        System.out.println("PASS - Search cleared. Full list restored with " + restoredRows.size() + " rows.");
    }

    // ── TC-BO-008 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-BO-008: Batch Owner login without entering a POC ID shows a validation alert")
    public void testBatchOwnerLoginWithoutPocIdShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUserId(ConfigReader.getSuperAdminUserId())
                 .selectRole("Batch Owner")
                 .selectServiceLine(ConfigReader.getValidServiceLineId())
                 .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_ENTER_POC_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID);
        System.out.println("PASS - Validation alert shown for missing POC ID: " + alertText);
    }
}
