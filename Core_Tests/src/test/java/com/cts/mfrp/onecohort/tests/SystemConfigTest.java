package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.SystemConfigPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
@Test(groups = {"regression", "superadmin", "systemconfig"})
public class SystemConfigTest extends BaseClassTest {

    private SystemConfigPage systemConfigPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToSystemConfig() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("System Config").click();

        wait.until(ExpectedConditions.urlContains("system-config"));
        systemConfigPage = new SystemConfigPage(driver);
        systemConfigPage.waitForPageLoad();

        System.out.println("Setup complete. System Config URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-SC-004: System Config page shows exactly 4 configuration category cards")
    public void testConfigCardCountIsExactlyFour() {
        int cardCount = systemConfigPage.getConfigCardCount();
        Assert.assertEquals(cardCount, 4,
                "System Config page should show exactly 4 config category cards. Found: " + cardCount);
        System.out.println("PASS - System Config shows exactly 4 configuration cards.");
    }
}
