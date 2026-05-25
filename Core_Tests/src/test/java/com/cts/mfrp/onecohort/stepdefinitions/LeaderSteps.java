package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.leader.LeaderDashboardPage;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.List;

public class LeaderSteps {

    private final TestContext context;
    private LeaderDashboardPage dashPage;

    public LeaderSteps(TestContext context) {
        this.context = context;
    }

    private LeaderDashboardPage getDashPage() {
        if (dashPage == null) {
            dashPage = new LeaderDashboardPage(context.driver);
            dashPage.waitForDashboardLoad();
        }
        return dashPage;
    }

    @Then("the leader role badge should be visible")
    public void theLeaderRoleBadgeShouldBeVisible() {
        String badge = getDashPage().getLeaderBadgeText();
        Assert.assertTrue(badge.toLowerCase().contains("leader"),
                "Role badge should display 'Leader'. Got: " + badge);
    }

    @Then("the dashboard should have KPI cards")
    public void theDashboardShouldHaveKpiCards() {
        List<String> kpiTitles = getDashPage().getKpiCardTitles();
        Assert.assertFalse(kpiTitles.isEmpty(),
                "Dashboard should have KPI cards. Found: " + kpiTitles);
    }

    @Then("the sidebar should have at least {int} navigation links")
    public void theSidebarShouldHaveAtLeastNavLinks(int count) {
        List<String> navLinks = getDashPage().getNavLinkTexts();
        Assert.assertTrue(navLinks.size() >= count,
                "Sidebar should have at least " + count + " links. Found: " + navLinks);
    }

    @Then("the sidebar should contain a {string} link")
    public void theSidebarShouldContainLink(String linkText) {
        List<String> navLinks = getDashPage().getNavLinkTexts();
        boolean found = navLinks.stream()
                .anyMatch(link -> link.toLowerCase().contains(linkText.toLowerCase()));
        Assert.assertTrue(found,
                "Sidebar should contain '" + linkText + "' link. Found: " + navLinks);
    }
}