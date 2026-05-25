package com.cts.mfrp.onecohort.pages.batchowners;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the Batch Owner (POC) Dashboard.
 * Covers all elements tested in BatchOwnerTest (FRD Section 13).
 */
public class BatchOwnerDashboardPage extends BasePage {

    // ── Dashboard locators ────────────────────────────────────────────────────

    private final By dashboardHeading = By.xpath(
            "//*[contains(normalize-space(),'Batch Owner Dashboard') " +
                    "or contains(normalize-space(),'POC Dashboard')]");

    // FRD 13.3.1: Cohorts Summary cards — uses kpi-card (actual class from DOM inspection)
    private final By totalCohortsCard = By.xpath(
            "//*[contains(@class,'kpi-card')]" +
                    "[.//*[contains(normalize-space(),'Total Cohort')]]");

    private final By activeCohortsCard = By.xpath(
            "//*[contains(@class,'kpi-card')]" +
                    "[.//*[contains(normalize-space(),'Active')]]");

    private final By completedCohortsCard = By.xpath(
            "//*[contains(@class,'kpi-card')]" +
                    "[.//*[contains(normalize-space(),'Completed')]]");

    private final By upcomingCohortsCard = By.xpath(
            "//*[contains(@class,'kpi-card')]" +
                    "[.//*[contains(normalize-space(),'Upcoming')]]");

    // FRD 13.7: Sidebar
    private final By sidebarCohortsLink = By.xpath(
            "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
                    "//a[contains(normalize-space(),'Cohort')] " +
                    "| //nav//a[contains(normalize-space(),'Cohort')]");

    // FRD 13.4: Cohorts list page
    private final By cohortsSearchBar = By.cssSelector(
            "input[type='search'], input[placeholder*='Search'], input[placeholder*='search'], " +
                    "input[placeholder*='Cohort'], input[placeholder*='cohort'], " +
                    "[class*='search'] input");

    private final By cohortsTableRows = By.cssSelector(
            "table.table tbody tr, .table-responsive table tbody tr");

    // FRD 13.7: CRUD buttons (must NOT be present for Batch Owner — read-only role)
    private final By crudButtons = By.xpath(
            "//button[contains(normalize-space(),'Create') " +
                    "or contains(normalize-space(),'Edit') " +
                    "or contains(normalize-space(),'Delete') " +
                    "or contains(normalize-space(),'Add Trainee') " +
                    "or contains(normalize-space(),'Add Batch Owner')]");

    public BatchOwnerDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── URL / load ────────────────────────────────────────────────────────────

    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.urlContains("/batch-owner/"));
    }

    // ── Cohorts Summary cards (FRD 13.3.1) ───────────────────────────────────
    // FIX: Uses explicit wait (15s from BasePage) instead of elementExists() (0s wait)
    // so cards are found even if Angular renders them slightly after page load.

    public boolean isTotalCohortsCardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(totalCohortsCard));
            return true;
        } catch (Exception e) {
            System.out.println("[BatchOwnerDashboardPage] 'Total Cohorts' kpi-card not found. " +
                    "Page body: " + driver.findElement(By.tagName("body"))
                    .getText().substring(0, Math.min(300,
                            driver.findElement(By.tagName("body")).getText().length())));
            return false;
        }
    }

    public boolean isActiveCohortsCardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(activeCohortsCard));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean isCompletedCohortsCardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(completedCohortsCard));
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean isUpcomingCohortsCardVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(upcomingCohortsCard));
            return true;
        } catch (Exception e) { return false; }
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    public WebElement getSidebarCohortsLinkElement() {
        return wait.until(ExpectedConditions.elementToBeClickable(sidebarCohortsLink));
    }

    // ── Cohorts list page (FRD 13.4) ─────────────────────────────────────────

    public void waitForSearchBarVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cohortsSearchBar));
    }

    public WebElement getSearchBarElement() {
        return driver.findElement(cohortsSearchBar);
    }

    public List<WebElement> getCohortsTableRows() {
        try { return driver.findElements(cohortsTableRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public String getFirstCohortRowFirstCellText() {
        List<WebElement> rows = getCohortsTableRows();
        if (rows.isEmpty()) return "";
        try { return rows.get(0).findElement(By.cssSelector("td:first-child")).getText().trim(); }
        catch (Exception e) { return ""; }
    }

    public void waitForCohortsTableToSettle(int seconds) {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(seconds))
                .until(d -> !d.findElements(cohortsTableRows).isEmpty());
    }

    // ── CRUD buttons check (FRD 13.7) ────────────────────────────────────────

    public List<WebElement> getCrudButtonElements() {
        try { return driver.findElements(crudButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Validation login test (TC-BO-008) ─────────────────────────────────────

    public boolean isDashboardHeadingVisible() {
        return elementExists(dashboardHeading);
    }
}