package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "manager"})
public class ManagerTest extends BaseClassTest {

    private ManagerDashboardPage dashPage;
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
    public void loginAsManager() {
        driver.get(ConfigReader.getBaseUrl());

        // Credentials read from LoginData.xlsx — no hardcoded user ID or service line
        new LoginPage(driver).loginAsManager(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_MANAGER),
                TestDataProvider.getServiceLineForRole(AppConstants.ROLE_MANAGER));

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_MANAGER));
        wait.until(ExpectedConditions.urlContains(AppConstants.URL_DASHBOARD));

        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Manager dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-MGR-001 ─────────────────────────────────────────────────────────────

    @Test(priority = 1, description = "TC-MGR-001: Manager dashboard loads with correct URL, role badge and KPI cards")
    public void testManagerDashboardLoads() {
        String url = driver.getCurrentUrl();
        softAssert.assertTrue(url.contains(AppConstants.URL_MANAGER),
                "URL should contain " + AppConstants.URL_MANAGER + ". Got: " + url);
        softAssert.assertTrue(url.contains(AppConstants.URL_DASHBOARD),
                "URL should contain " + AppConstants.URL_DASHBOARD + ". Got: " + url);

        String roleText = dashPage.getRoleText();
        softAssert.assertEquals(roleText, AppConstants.ROLE_MANAGER,
                "Role badge should display '" + AppConstants.ROLE_MANAGER + "'");

        // KPI card names sourced from AppConstants — no inline strings
        softAssert.assertTrue(dashPage.isKpiCardPresent(AppConstants.KPI_SERVICE_LINES),
                AppConstants.KPI_SERVICE_LINES + " KPI card missing");
        softAssert.assertTrue(dashPage.isKpiCardPresent(AppConstants.KPI_LEARNING_PATHS),
                AppConstants.KPI_LEARNING_PATHS + " KPI card missing");
        softAssert.assertTrue(dashPage.isKpiCardPresent(AppConstants.KPI_AVG_COMPLETION_RATE),
                AppConstants.KPI_AVG_COMPLETION_RATE + " KPI card missing");

        System.out.println("PASS - Manager dashboard loaded correctly. URL: " + url);
    }

    // ── TC-MGR-002 ─────────────────────────────────────────────────────────────

    @Test(priority = 2, description = "TC-MGR-002: Manager can navigate to Cohorts and back to Dashboard via sidebar")
    public void testManagerSidebarNavigation() {
        dashPage.clickManageCohortsNav();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains(AppConstants.URL_DASHBOARD)));

        String urlAfterCohorts = driver.getCurrentUrl();
        softAssert.assertTrue(urlAfterCohorts.contains(AppConstants.URL_MANAGER),
                "After clicking Cohorts, URL should still be under "
                + AppConstants.URL_MANAGER + ". Got: " + urlAfterCohorts);
        System.out.println("PASS - Navigated to cohorts section. URL: " + urlAfterCohorts);

        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains(AppConstants.URL_DASHBOARD));
        softAssert.assertTrue(driver.getCurrentUrl().contains(AppConstants.URL_DASHBOARD),
                "Should return to dashboard after clicking Dashboard nav");

        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("PASS - Returned to Manager dashboard.");
    }

    // ── TC-MGR-006 ─────────────────────────────────────────────────────────────

    @Test(priority = 3, description = "TC-MGR-006: Manager login without selecting a Service Line shows a validation alert")
    public void testManagerLoginWithoutServiceLineShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // User ID from Excel, role name from AppConstants — nothing hardcoded
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(AppConstants.ROLE_MANAGER))
                 .selectRole(AppConstants.ROLE_MANAGER)
                 .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        softAssert.assertEquals(alertText, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE);
        System.out.println("PASS - Validation alert shown for missing Service Line: " + alertText);
    }
}
