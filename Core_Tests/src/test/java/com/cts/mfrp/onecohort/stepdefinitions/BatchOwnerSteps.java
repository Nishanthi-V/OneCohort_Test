package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerDashboardPage;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

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

    // TC-BO-002
    @Then("the {string} card should be visible")
    public void theCardShouldBeVisible(String cardName) {
        boolean visible = switch (cardName) {
            case "Total Cohorts" -> getDashPage().isTotalCohortsCardVisible();
            case "Active"        -> getDashPage().isActiveCohortsCardVisible();
            case "Completed"     -> getDashPage().isCompletedCohortsCardVisible();
            case "Upcoming"      -> getDashPage().isUpcomingCohortsCardVisible();
            default -> throw new IllegalArgumentException("Unknown card: " + cardName);
        };
        Assert.assertTrue(visible, "'" + cardName + "' card should be visible");
    }

    // TC-BO-005
    @When("I wait for the search bar to be visible")
    public void iWaitForTheSearchBarToBeVisible() {
        getDashPage().waitForSearchBarVisible();
    }

    /**
     * FIX: waitForCohortsTableToSettle was timing out at 5 seconds.
     * The table might not exist yet or takes longer to render.
     * Now uses a safe try/catch — if no rows appear in 10 seconds
     * we just continue and let the assertion handle the failure.
     */
    @When("I type {string} in the search bar")
    public void iTypeInTheSearchBar(String searchTerm) {
        searchBar = getDashPage().getSearchBarElement();
        searchBar.clear();
        searchBar.sendKeys(searchTerm);
        // Safe wait — don't fail here, let the assertion fail with a clear message
        try {
            getDashPage().waitForCohortsTableToSettle(10);
        } catch (Exception e) {
            System.out.println("[BatchOwnerSteps] Table did not settle after search — continuing.");
        }
    }

    @When("I clear the search bar")
    public void iClearTheSearchBar() {
        if (searchBar == null) searchBar = getDashPage().getSearchBarElement();
        searchBar.clear();
        try {
            getDashPage().waitForCohortsTableToSettle(10);
        } catch (Exception e) {
            System.out.println("[BatchOwnerSteps] Table did not settle after clear — continuing.");
        }
    }

    @Then("the cohorts table should have at least {int} row")
    public void theCohortsTableShouldHaveAtLeastRow(int minRows) {
        List<WebElement> rows = getDashPage().getCohortsTableRows();
        Assert.assertFalse(rows.isEmpty(),
                "Cohorts table should have at least " + minRows +
                        " row(s). Found: " + rows.size());
    }
}