package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cr.CRDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "cr"})
public class CRTest extends BaseClassTest {

    private static final String CR_COHORT_ID = ConfigReader.getValidCohortId();
    private CRDashboardPage crPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsCR() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsCR(ConfigReader.getSuperAdminUserId(), CR_COHORT_ID);

        wait.until(ExpectedConditions.urlContains("/cr/"));

        crPage = new CRDashboardPage(driver, CR_COHORT_ID);
        System.out.println("CR login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-CR-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-CR-004: CR dashboard has no Create, Edit or Delete buttons (read-only access)")
    public void testCRHasNoCreateEditDeleteButtons() {
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        List<WebElement> crudButtons = crPage.getCrudButtonElements();
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        if (!crudButtons.isEmpty()) {
            String foundButtons = crudButtons.stream()
                    .map(btn -> "[" + btn.getText() + "]")
                    .collect(Collectors.joining(" "));
            Assert.fail("CR role should be read-only. Found CRUD buttons: " + foundButtons);
        }

        System.out.println("PASS - No Create/Edit/Delete buttons found. CR access is correctly read-only.");
    }

    // ── TC-CR-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-CR-006: Qualifier, Interim and Final evaluation sections are visible on CR dashboard")
    public void testEvaluationsVisible() {
        Assert.assertTrue(crPage.isQualifierExamVisible(),
                "Qualifier Exam section should be visible on the CR dashboard");
        Assert.assertTrue(crPage.isInterimEvaluationVisible(),
                "Interim Evaluation section should be visible on the CR dashboard");
        Assert.assertTrue(crPage.isFinalEvaluationVisible(),
                "Final Evaluation section should be visible on the CR dashboard");
        System.out.println("PASS - All 3 evaluation sections visible on CR dashboard (Qualifier, Interim, Final).");
    }
}
