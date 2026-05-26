package com.cts.mfrp.onecohort.tests.leader;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.leader.LeaderDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "leader"})
public class LeaderTest extends BaseClassTest {

    private LeaderDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsLeader() {
        driver.get(ConfigReader.getBaseUrl());

        // Credentials read from LoginData.xlsx — no hardcoded user ID or service line
        new LoginPage(driver).loginAsLeader(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_LEADER),
                TestDataProvider.getServiceLineForRole(AppConstants.ROLE_LEADER));

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_LEADER));

        dashPage = new LeaderDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Leader dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-LEADER-001 ──────────────────────────────────────────────────────────

    @Test(priority = 1, description = "TC-LEADER-001: Leader dashboard loads with correct URL and KPI cards")
    public void testLeaderDashboardLoads() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains(AppConstants.URL_LEADER),
                "URL should contain " + AppConstants.URL_LEADER + ". Got: " + url);

        String leaderBadgeText = dashPage.getLeaderBadgeText();
        Assert.assertTrue(leaderBadgeText.toLowerCase().contains(
                AppConstants.ROLE_LEADER.toLowerCase()),
                "Role badge should display '" + AppConstants.ROLE_LEADER
                + "'. Got: " + leaderBadgeText);

        List<String> kpiTitles = dashPage.getKpiCardTitles();
        Assert.assertFalse(kpiTitles.isEmpty(),
                "Dashboard should have KPI cards. Found: " + kpiTitles);

        System.out.println("PASS - Leader dashboard loaded. URL: " + url
                + " | KPI cards: " + kpiTitles);
    }

    // ── TC-LEADER-002 ──────────────────────────────────────────────────────────

    @Test(priority = 2, description = "TC-LEADER-002: Leader sidebar has at least 2 nav links including a Cohorts link")
    public void testLeaderSidebarNavigation() {
        List<String> navLinks = dashPage.getNavLinkTexts();

        // Minimum link count from AppConstants — no magic number
        Assert.assertTrue(navLinks.size() >= AppConstants.MIN_LEADER_NAV_LINKS,
                "Leader sidebar should have at least " + AppConstants.MIN_LEADER_NAV_LINKS
                + " navigation links. Found: " + navLinks);

        // Nav link label from AppConstants — no inline "cohort" string
        boolean hasCohortLink = navLinks.stream()
                .anyMatch(link -> link.toLowerCase()
                        .contains(AppConstants.NAV_COHORTS.toLowerCase()));
        Assert.assertTrue(hasCohortLink,
                "Sidebar should have a '" + AppConstants.NAV_COHORTS
                + "' link. Found: " + navLinks);

        System.out.println("PASS - Leader sidebar navigation verified. Links: " + navLinks);
    }

    // ── TC-LEADER-003 ──────────────────────────────────────────────────────────

    @Test(priority = 3, description = "TC-LEADER-003: Leader login without selecting a Service Line shows a validation alert")
    public void testLeaderLoginWithoutServiceLineShowsAlert() {
        driver.get(ConfigReader.getBaseUrl());
        LoginPage loginPage = new LoginPage(driver);

        // User ID from Excel, role name from AppConstants — nothing hardcoded
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(AppConstants.ROLE_LEADER))
                 .selectRole(AppConstants.ROLE_LEADER)
                 .clickLoginButton();

        String alertText = loginPage.acceptAlertAndGetMessage();
        Assert.assertEquals(alertText, AppConstants.ALERT_SELECT_SERVICE_LINE,
                "Alert should say: " + AppConstants.ALERT_SELECT_SERVICE_LINE);
        System.out.println("PASS - Validation alert shown for missing Service Line: " + alertText);
    }
}
