package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
@Test(groups = {"regression", "superadmin", "systemconfig"})
public class SystemConfigTest extends BaseClassTest {

    /** UI label for the System Config menu item — a UI text constant, not test data. */
    private static final String MENU_LABEL_SYSTEM_CONFIG = "System Config";

    private SystemConfigPage systemConfigPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToSystemConfig() {
        driver.get(ConfigReader.getBaseUrl());

        // Super Admin user ID read from LoginData.xlsx — no hardcoded value
        new LoginPage(driver).loginAsSuperAdmin(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_SUPER_ADMIN));

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_SUPER_ADMIN));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement(MENU_LABEL_SYSTEM_CONFIG).click();

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_SYSTEM_CONFIG));
        systemConfigPage = new SystemConfigPage(driver);
        systemConfigPage.waitForPageLoad();

        System.out.println("Setup complete. System Config URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-SC-004: System Config page shows exactly "
            + AppConstants.SYSTEM_CONFIG_CARD_COUNT + " configuration category cards")
    public void testConfigCardCountIsExactlyFour() {
        int cardCount = systemConfigPage.getConfigCardCount();
        Assert.assertEquals(cardCount, AppConstants.SYSTEM_CONFIG_CARD_COUNT,
                "System Config page should show exactly "
                + AppConstants.SYSTEM_CONFIG_CARD_COUNT
                + " config category cards. Found: " + cardCount);
        System.out.println("PASS - System Config shows exactly "
                + AppConstants.SYSTEM_CONFIG_CARD_COUNT + " configuration cards.");
    }
}
