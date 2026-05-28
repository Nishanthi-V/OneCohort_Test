package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebElement;
// SoftAssert accessed via context.softAssert (injected by Hooks.setUp)

import java.util.List;

public class BatchOwnerSteps {

    private final TestContext context;
    private BatchOwnerDashboardPage dashPage;
    private WebElement searchBar;

    public BatchOwnerSteps(TestContext context) {
        this.context = context;
    }

    private BatchOwnerDashboardPage getDashPage() {
        if (dashPage == null) {
            dashPage = new BatchOwnerDashboardPage(context.driver);
        }
        return dashPage;
    }

    // TC-BO-002 ── Summary card visibility ────────────────────────────────────

    @Then("the {string} card should be visible")
    public void theCardShouldBeVisible(String cardName) {
        boolean visible = switch (cardName) {
            case "Total Cohorts" -> getDashPage().isTotalCohortsCardVisible();
            case "Active"        -> getDashPage().isActiveCohortsCardVisible();
            case "Completed"     -> getDashPage().isCompletedCohortsCardVisible();
            case "Upcoming"      -> getDashPage().isUpcomingCohortsCardVisible();
            default -> throw new IllegalArgumentException("Unknown card: " + cardName);
        };
        context.softAssert.assertTrue(visible, "'" + cardName + "' card should be visible");
    }

    // TC-BO-005 ── Search functionality (all wait times from AppConstants) ─────

    @When("I wait for the search bar to be visible")
    public void iWaitForTheSearchBarToBeVisible() {
        getDashPage().waitForSearchBarVisible();
    }

    /**
     * Excel-driven search: reads the search term from LoginData.xlsx
     * (SearchTerm column for the Batch Owner row) — no hardcoded "INT".
     */
    @When("I search using the batch owner search term")
    public void iSearchUsingBatchOwnerSearchTerm() {
        String searchTerm = TestDataProvider.getSearchTermForRole(AppConstants.ROLE_BATCH_OWNER);
        searchBar = getDashPage().getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);
        try {
            getDashPage().waitForCohortsTableToSettle(AppConstants.TABLE_SETTLE_WAIT);
        } catch (Exception e) {
            System.out.println("[BatchOwnerSteps] Table did not settle after search — continuing.");
        }
    }

    @When("I type {string} in the search bar")
    public void iTypeInTheSearchBar(String searchTerm) {
        searchBar = getDashPage().getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);
        try {
            getDashPage().waitForCohortsTableToSettle(AppConstants.TABLE_SETTLE_WAIT);
        } catch (Exception e) {
            System.out.println("[BatchOwnerSteps] Table did not settle after search — continuing.");
        }
    }

    @When("I clear the search bar")
    public void iClearTheSearchBar() {
        if (searchBar == null) searchBar = getDashPage().getSearchBarElement();
        searchBar.clear();
        try {
            getDashPage().waitForCohortsTableToSettle(AppConstants.TABLE_SETTLE_WAIT);
        } catch (Exception e) {
            System.out.println("[BatchOwnerSteps] Table did not settle after clear — continuing.");
        }
    }

    @Then("the cohorts table should have at least {int} row")
    public void theCohortsTableShouldHaveAtLeastRow(int minRows) {
        List<WebElement> rows = getDashPage().getCohortsTableRows();
        context.softAssert.assertFalse(rows.isEmpty(),
                "Cohorts table should have at least " + minRows
                + " row(s). Found: " + rows.size());
    }
}
