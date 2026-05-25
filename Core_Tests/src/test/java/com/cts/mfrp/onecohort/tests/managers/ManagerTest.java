package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "manager"})
public class ManagerTest extends BaseClassTest {

    private ManagerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsManager() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsManager(
                ConfigReader.getManagerUserId(),
                ConfigReader.getValidServiceLineId());

        wait.until(ExpectedConditions.urlContains("/manager/"));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Manager dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-MGR-001 ────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-MGR-001: Manager dashboard loads with correct URL, role badge and KPI cards")
    public void testManagerDashboardLoads() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "URL should contain /manager/. Got: " + url);
        Assert.assertTrue(url.contains("/dashboard"),
                "URL should contain /dashboard. Got: " + url);

        String roleText = dashPage.getRoleText();
        Assert.assertEquals(roleText, "Manager",
                "Role badge should display 'Manager'");

        Assert.assertTrue(dashPage.isKpiCardPresent("Service Lines"),    "Service Lines KPI card missing");
        Assert.assertTrue(dashPage.isKpiCardPresent("Learning Paths"),   "Learning Paths KPI card missing");
        Assert.assertTrue(dashPage.isKpiCardPresent("Avg. Completion Rate"), "Avg. Completion Rate KPI card missing");

        System.out.println("PASS - Manager dashboard loaded correctly. URL: " + url);
    }

    // ── TC-MGR-002 ────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-MGR-002: Manager can navigate to Cohorts and back to Dashboard via sidebar")
    public void testManagerSidebarNavigation() {
        dashPage.clickManageCohortsNav();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/dashboard")));

        String urlAfterCohorts = driver.getCurrentUrl();
        Assert.assertTrue(urlAfterCohorts.contains("/manager/"),
                "After clicking Cohorts, URL should still be under /manager/. Got: " + urlAfterCohorts);
        System.out.println("PASS - Navigated to cohorts section. URL: " + urlAfterCohorts);

        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Should return to dashboard after clicking Dashboard nav");

        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("PASS - Returned to Manager dashboard.");
    }

    // ── TC-MGR-006 ────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-MGR-006: Manager login without selecting a Service Line shows a validation alert")
    public void testManagerLoginWithoutServiceLineShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterUserId(ConfigReader.getManagerUserId())
                 .selectRole("Manager")
                 .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE);
        System.out.println("PASS - Validation alert shown for missing Service Line: " + alertText);
    }
}
