package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.cr.CRDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class CRSteps {

    private final TestContext context;
    private CRDashboardPage crPage;

    public CRSteps(TestContext context) {
        this.context = context;
    }

    /**
     * Cohort ID is read from LoginData.xlsx (CohortId column, CR row)
     * via TestDataProvider — not from config.properties.
     */
    private CRDashboardPage getCrPage() {
        if (crPage == null) {
            String cohortId = TestDataProvider.getCohortIdForRole(AppConstants.ROLE_CR);
            crPage = new CRDashboardPage(context.driver, cohortId);
        }
        return crPage;
    }

    // TC-CR-004 ── Read-only assertion ────────────────────────────────────────

    @Then("no CRUD buttons should be visible on the dashboard")
    public void noCrudButtonsShouldBeVisible() {
        context.driver.manage().timeouts()
                .implicitlyWait(java.time.Duration.ofSeconds(0));
        List<WebElement> crudButtons = getCrPage().getCrudButtonElements();
        context.driver.manage().timeouts()
                .implicitlyWait(java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        if (!crudButtons.isEmpty()) {
            String found = crudButtons.stream()
                    .map(btn -> "[" + btn.getText() + "]")
                    .collect(Collectors.joining(" "));
            Assert.fail("CR role should be read-only. Found CRUD buttons: " + found);
        }
    }

    // TC-CR-006 ── Evaluation section visibility ───────────────────────────────

    @Then("the {string} evaluation section should be visible")
    public void theEvaluationSectionShouldBeVisible(String sectionName) {
        boolean visible = switch (sectionName) {
            case "Qualifier" -> getCrPage().isQualifierExamVisible();
            case "Interim"   -> getCrPage().isInterimEvaluationVisible();
            case "Final"     -> getCrPage().isFinalEvaluationVisible();
            default -> throw new IllegalArgumentException("Unknown section: " + sectionName);
        };
        Assert.assertTrue(visible,
                sectionName + " evaluation section should be visible on the CR dashboard");
    }
}
