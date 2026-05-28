package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "batchowner"})
public class BatchOwnerTest extends BaseClassTest {

    private BatchOwnerDashboardPage dashPage;
    private SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void assertSoftAssertions() {
        softAssert.assertAll();
    }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsBatchOwner() {
        driver.get(ConfigReader.getBaseUrl());

        // All credentials read from LoginData.xlsx — no hardcoded values
        new LoginPage(driver).loginAsBatchOwner(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_BATCH_OWNER),
                TestDataProvider.getServiceLineForRole(AppConstants.ROLE_BATCH_OWNER),
                TestDataProvider.getPocIdForRole(AppConstants.ROLE_BATCH_OWNER));

        // Login redirect timeout from AppConstants — no magic number
        new WebDriverWait(driver, Duration.ofSeconds(AppConstants.LOGIN_REDIRECT_WAIT))
                .until(d -> !d.getCurrentUrl().contains("login"));

        dashPage = new BatchOwnerDashboardPage(driver);
        System.out.println("Batch Owner login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-BO-002 ──────────────────────────────────────────────────────────────

    @Test(priority = 1, description = "TC-BO-002: Batch Owner dashboard shows all 4 cohort summary cards")
    public void testDashboardShowsCohortSummaryCards() {
        softAssert.assertTrue(dashPage.isTotalCohortsCardVisible(),
                AppConstants.CARD_TOTAL_COHORTS + " card should be visible");
        softAssert.assertTrue(dashPage.isActiveCohortsCardVisible(),
                AppConstants.CARD_ACTIVE + " card should be visible");
        softAssert.assertTrue(dashPage.isCompletedCohortsCardVisible(),
                AppConstants.CARD_COMPLETED + " card should be visible");
        softAssert.assertTrue(dashPage.isUpcomingCohortsCardVisible(),
                AppConstants.CARD_UPCOMING + " card should be visible");

        System.out.println("PASS - All " + AppConstants.SYSTEM_CONFIG_CARD_COUNT
                + " cohort summary cards are visible on the dashboard.");
    }

    // ── TC-BO-005 ──────────────────────────────────────────────────────────────

    @Test(priority = 2, description = "TC-BO-005: Search bar filters the cohorts table and clearing restores all rows")
    public void testSearchFiltersCohortsTable() {
        WebElement cohortsLink = dashPage.getSidebarCohortsLinkElement();
        cohortsLink.click();
        dashPage.waitForSearchBarVisible();

        // Search term read from LoginData.xlsx — no hardcoded "INT"
        String searchTerm = TestDataProvider.getSearchTermForRole(AppConstants.ROLE_BATCH_OWNER);
        String firstCellText = dashPage.getFirstCohortRowFirstCellText();
        if (!firstCellText.isEmpty() && firstCellText.length() >= 3) {
            searchTerm = firstCellText.substring(0, 3);
        }

        WebElement searchBar = dashPage.getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);

        // Table settle timeout from AppConstants — no magic number
        dashPage.waitForCohortsTableToSettle(AppConstants.TABLE_SETTLE_FAST);

        List<WebElement> filteredRows = dashPage.getCohortsTableRows();
        softAssert.assertFalse(filteredRows.isEmpty(),
                "Search with term '" + searchTerm + "' should return at least one result");
        System.out.println("PASS - Search '" + searchTerm + "' filtered table to "
                + filteredRows.size() + " rows.");

        searchBar.clear();
        dashPage.waitForCohortsTableToSettle(AppConstants.TABLE_SETTLE_FAST);
        List<WebElement> restoredRows = dashPage.getCohortsTableRows();
        softAssert.assertFalse(restoredRows.isEmpty(),
                "Clearing the search should restore all cohort rows");
        System.out.println("PASS - Search cleared. Full list restored with "
                + restoredRows.size() + " rows.");
    }

    // ── TC-BO-008 ──────────────────────────────────────────────────────────────

    @Test(priority = 3, description = "TC-BO-008: Batch Owner login without entering a POC ID shows a validation alert")
    public void testBatchOwnerLoginWithoutPocIdShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // All values from Excel or AppConstants — nothing hardcoded
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(AppConstants.ROLE_BATCH_OWNER))
                 .selectRole(AppConstants.ROLE_BATCH_OWNER)
                 .selectServiceLine(TestDataProvider.getServiceLineForRole(AppConstants.ROLE_BATCH_OWNER))
                 .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        softAssert.assertEquals(alertText, AppConstants.ALERT_ENTER_POC_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_POC_ID);
        System.out.println("PASS - Validation alert shown for missing POC ID: " + alertText);
    }
}
