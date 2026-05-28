package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.HomePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class LoginTest extends BaseTest {

    private SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void assertSoftAssertions() {
        softAssert.assertAll();
    }

    @Test(description = "TC-AUTH-001: Super Admin login with valid User ID should redirect to dashboard")
    public void superAdminLogin_ValidUserId_RedirectsToDashboard() {
        LoginPage loginPage = new LoginPage(getDriver());

        HomePage homePage = loginPage.loginAsSuperAdmin(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_SUPER_ADMIN));

        softAssert.assertTrue(
                homePage.getCurrentUrl().contains(AppConstants.URL_SUPER_ADMIN),
                "URL should contain " + AppConstants.URL_SUPER_ADMIN + " after Super Admin login");
        softAssert.assertTrue(
                homePage.isDashboardLoaded(),
                "Dashboard should be visible after Super Admin login");
    }

    @Test(description = "TC-AUTH-002: Login with empty User ID should show a validation alert")
    public void login_EmptyUserId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        // Role name from AppConstants — no inline string literal
        loginPage.selectRole(AppConstants.ROLE_SUPER_ADMIN);
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();
        softAssert.assertEquals(
                alertMessage,
                AppConstants.ALERT_EMPTY_USER_ID,
                "Alert should say: " + AppConstants.ALERT_EMPTY_USER_ID);
    }

    @Test(description = "TC-AUTH-006: CR login without Cohort ID should show a validation alert")
    public void crLogin_NoCohortId_ShowsAlert() {
        LoginPage loginPage = new LoginPage(getDriver());

        // User ID and role both driven by AppConstants / TestDataProvider
        loginPage.enterUserId(TestDataProvider.getUserIdForRole(AppConstants.ROLE_CR));
        loginPage.selectRole(AppConstants.ROLE_CR);
        loginPage.clickLoginButton();

        String alertMessage = loginPage.acceptAlertAndGetMessage();
        softAssert.assertEquals(
                alertMessage,
                AppConstants.ALERT_ENTER_COHORT_ID,
                "Alert should say: " + AppConstants.ALERT_ENTER_COHORT_ID);
    }
}
