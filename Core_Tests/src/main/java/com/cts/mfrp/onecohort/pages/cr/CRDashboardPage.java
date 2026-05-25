package com.cts.mfrp.onecohort.pages.cr;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the CR (Class Representative) Dashboard.
 * Covers all elements tested in CRDashboardTest (FRD Section 12).
 */
public class CRDashboardPage extends BasePage {

    private final String cohortId;

    // ── Dashboard locators (FRD 12.2) ─────────────────────────────────────────

    private final By welcomeGreeting = By.xpath(
            "//*[contains(text(),'Welcome') and contains(text(),'CR')]");

    private final By summaryHeaderCards = By.cssSelector(
            "[class*='card'], [class*='summary'], [class*='stat'], [class*='metric'], [class*='header-card']");

    private final By totalMembersCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
                    "[.//*[contains(normalize-space(),'Total Member') or contains(normalize-space(),'Members')]]");

    private final By learningPathCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
                    "[.//*[contains(normalize-space(),'Learning Path')]]");

    private final By statusCard = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'stat') or contains(@class,'metric')]" +
                    "[.//*[contains(normalize-space(),'Status')]]");

    private final By batchOwnerField = By.xpath("//*[contains(normalize-space(),'Batch Owner')]");

    private final By startDateField = By.xpath("//*[contains(normalize-space(),'Start Date')]");

    private final By totalInternsField = By.xpath(
            "//*[contains(normalize-space(),'Total Intern') or contains(normalize-space(),'Interns')]");

    private final By currentProgressField = By.xpath(
            "//*[contains(normalize-space(),'Current Progress') or contains(normalize-space(),'% Complete')]");

    private final By trainingTimelineSection = By.xpath(
            "//*[contains(normalize-space(),'Training Timeline') or contains(normalize-space(),'Timeline')]");

    private final By weekButtons = By.xpath(
            "//button[contains(normalize-space(),'Week')] | //*[contains(@class,'week')]");

    /**
     * FIX: Broadened the qualifier exam locator to match more possible text variants:
     * "Qualifier Exam", "Qualifier Test", "Qualifying Exam", "Qualifier", etc.
     * Also added a fallback locator for class-based matching.
     *
     * The old locator '//*[contains(normalize-space(),'Qualifier')]' was correct
     * but elementExists() uses a 0-second implicit wait which fails if the section
     * renders slightly after page load. isQualifierExamVisible() now uses an
     * explicit wait with a short timeout before falling back to a DOM check.
     */
    private final By qualifierExam = By.xpath(
            "//*[contains(normalize-space(),'Qualifier') or contains(normalize-space(),'qualifier')]");

    private final By qualifierExamFallback = By.xpath(
            "//*[contains(@class,'qualifier') or contains(@id,'qualifier')]");

    private final By interimEvaluation = By.xpath(
            "//*[contains(normalize-space(),'Interim') or contains(normalize-space(),'interim')]");

    private final By interimEvaluationFallback = By.xpath(
            "//*[contains(@class,'interim') or contains(@id,'interim')]");

    private final By finalEvaluation = By.xpath(
            "//*[contains(normalize-space(),'Final') or contains(normalize-space(),'final')]");

    private final By finalEvaluationFallback = By.xpath(
            "//*[contains(@class,'final') or contains(@id,'final')]");

    private final By overallProgressCard = By.xpath(
            "//*[contains(normalize-space(),'Overall Progress') or contains(normalize-space(),'Weeks Remaining')]");

    private final By traineesTable = By.cssSelector("table.table, .table-responsive table");
    private final By traineesRows  = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");

    private final By crudButtons = By.xpath(
            "//button[contains(normalize-space(),'Create') " +
                    "or contains(normalize-space(),'Edit') " +
                    "or contains(normalize-space(),'Delete') " +
                    "or contains(normalize-space(),'Add Trainee')]");

    private final By logoutDirect = By.xpath(
            "//button[contains(normalize-space(),'Logout') " +
                    "or contains(normalize-space(),'Sign Out') " +
                    "or contains(normalize-space(),'Log Out')]" +
                    " | //a[contains(normalize-space(),'Logout')]" +
                    " | //*[@aria-label='Logout' or @aria-label='Sign out' or @title='Logout']");

    private final By userMenuTrigger = By.cssSelector(
            "[class*='user-menu'],[class*='avatar'],[class*='account'],[class*='profile-icon']," +
                    "[class*='user-icon'],[class*='dropdown-toggle'],[class*='user-btn']");

    public CRDashboardPage(WebDriver driver, String cohortId) {
        super(driver);
        this.cohortId = cohortId;
    }

    // ── URL ───────────────────────────────────────────────────────────────────

    public void waitForCrRoute() {
        wait.until(ExpectedConditions.urlContains("/cr/"));
    }

    public boolean isUrlContainsCohortId() {
        return driver.getCurrentUrl().contains(cohortId);
    }

    // ── Alert handling ────────────────────────────────────────────────────────

    public boolean dismissAlertIfPresent() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            return true;
        } catch (Exception e) { return false; }
    }

    // ── Welcome greeting (FRD 12.2) ───────────────────────────────────────────

    public boolean isWelcomeGreetingVisible() {
        return isDisplayed(welcomeGreeting);
    }

    public WebElement getWelcomeGreetingElement() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeGreeting));
    }

    public String getPageBodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    // ── Sidebar (FRD 12.3) ────────────────────────────────────────────────────

    public boolean isCohortIdVisibleOnPage() {
        By sidebarEntry = By.xpath(
                "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
                        "//*[contains(text(),'" + cohortId + "')]");
        By anyEntry = By.xpath("//*[contains(text(),'" + cohortId + "')]");
        return elementExists(sidebarEntry) || elementExists(anyEntry);
    }

    public WebElement getCohortIdElement() {
        By sidebarEntry = By.xpath(
                "//*[contains(@class,'sidebar') or contains(@class,'nav') or contains(@class,'side')]" +
                        "//*[contains(text(),'" + cohortId + "')]");
        if (elementExists(sidebarEntry)) return driver.findElement(sidebarEntry);
        return driver.findElement(By.xpath("//*[contains(text(),'" + cohortId + "')]"));
    }

    // ── Summary header cards (FRD 12.2.1) ────────────────────────────────────

    public List<WebElement> getSummaryHeaderCards() {
        try { return driver.findElements(summaryHeaderCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean isTotalMembersCardVisible() {
        if (elementExists(totalMembersCard)) return true;
        By fallback = By.xpath(
                "//*[contains(normalize-space(),'Member') or contains(normalize-space(),'Intern')]");
        return elementExists(fallback);
    }

    public WebElement getTotalMembersCardElement() {
        if (elementExists(totalMembersCard)) return driver.findElement(totalMembersCard);
        return driver.findElement(By.xpath(
                "//*[contains(normalize-space(),'Member') or contains(normalize-space(),'Intern')]"));
    }

    public boolean isLearningPathCardVisible() {
        return elementExists(learningPathCard);
    }

    public WebElement getLearningPathCardElement() {
        return driver.findElement(learningPathCard);
    }

    public boolean isStatusCardVisible() {
        return elementExists(statusCard);
    }

    public WebElement getStatusCardElement() {
        return driver.findElement(statusCard);
    }

    // ── Cohort metadata panel (FRD 12.2.2) ───────────────────────────────────

    public boolean isBatchOwnerFieldVisible() {
        return elementExists(batchOwnerField);
    }

    public boolean isStartDateFieldVisible() {
        return elementExists(startDateField);
    }

    public boolean isTotalInternsFieldVisible() {
        return elementExists(totalInternsField);
    }

    public boolean isCurrentProgressFieldVisible() {
        return elementExists(currentProgressField);
    }

    // ── Training Timeline (FRD 12.2.3) ───────────────────────────────────────

    public boolean isTrainingTimelineSectionVisible() {
        return elementExists(trainingTimelineSection);
    }

    public WebElement getTrainingTimelineSectionElement() {
        return driver.findElement(trainingTimelineSection);
    }

    public List<WebElement> getWeekButtons() {
        try { return driver.findElements(weekButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Evaluation panel (FRD 12.2.4) ────────────────────────────────────────

    /**
     * FIX: Uses an explicit wait (up to 5 seconds) before falling back to a DOM check.
     * Also tries a CSS class/id fallback if text-based XPath finds nothing.
     * Prints the page body text excerpt to aid debugging if still not found.
     */
    public boolean isQualifierExamVisible() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(qualifierExam),
                    ExpectedConditions.presenceOfElementLocated(qualifierExamFallback)
            ));
        } catch (Exception ignored) {}

        if (elementExists(qualifierExam)) return true;
        if (elementExists(qualifierExamFallback)) return true;

        // Debug: print relevant portion of page text to help identify the actual label
        String bodyText = driver.findElement(By.tagName("body")).getText();
        System.out.println("[CRDashboardPage] 'Qualifier' not found. Page body excerpt (first 800 chars):\n"
                + bodyText.substring(0, Math.min(800, bodyText.length())));
        return false;
    }

    public boolean isInterimEvaluationVisible() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(interimEvaluation),
                    ExpectedConditions.presenceOfElementLocated(interimEvaluationFallback)
            ));
        } catch (Exception ignored) {}

        if (elementExists(interimEvaluation)) return true;
        return elementExists(interimEvaluationFallback);
    }

    public boolean isFinalEvaluationVisible() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(finalEvaluation),
                    ExpectedConditions.presenceOfElementLocated(finalEvaluationFallback)
            ));
        } catch (Exception ignored) {}

        if (elementExists(finalEvaluation)) return true;
        return elementExists(finalEvaluationFallback);
    }

    // ── Overall progress & trainees table ─────────────────────────────────────

    public boolean isOverallProgressCardVisible() {
        return elementExists(overallProgressCard);
    }

    public boolean isTraineesTableVisible() {
        return isDisplayed(traineesTable);
    }

    public List<WebElement> getTraineesTableRows() {
        try { return driver.findElements(traineesRows); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Read-only / Access control (FRD 12.5) ────────────────────────────────

    public boolean areCrudButtonsPresent() {
        return elementExists(crudButtons);
    }

    // ── Logout (FRD 12.6) ────────────────────────────────────────────────────

    public boolean isLogoutDirectlyVisible() {
        return elementExists(logoutDirect);
    }

    public boolean isUserMenuTriggerVisible() {
        return elementExists(userMenuTrigger);
    }

    public void clickUserMenuTrigger() {
        driver.findElement(userMenuTrigger).click();
    }

    public boolean isLogoutVisibleAfterMenuOpen() {
        return elementExists(logoutDirect);
    }

    public void clickLogout() {
        driver.findElement(logoutDirect).click();
    }

    // ── Additional element getters ─────────────────────────────────────────────

    public WebElement getBatchOwnerFieldElement()       { return driver.findElement(batchOwnerField); }
    public WebElement getStartDateFieldElement()        { return driver.findElement(startDateField); }
    public WebElement getTotalInternsFieldElement()     { return driver.findElement(totalInternsField); }
    public WebElement getCurrentProgressFieldElement()  { return driver.findElement(currentProgressField); }

    public WebElement getQualifierExamElement()     { return driver.findElement(qualifierExam); }
    public WebElement getInterimEvaluationElement() { return driver.findElement(interimEvaluation); }
    public WebElement getFinalEvaluationElement()   { return driver.findElement(finalEvaluation); }

    public WebElement getOverallProgressCardElement() {
        if (elementExists(overallProgressCard)) return driver.findElement(overallProgressCard);
        return driver.findElement(By.cssSelector("canvas,[class*='progress'],[role='progressbar'],[class*='bar']"));
    }

    public boolean isOverallProgressFallbackVisible() {
        return elementExists(By.cssSelector("canvas,[class*='progress'],[role='progressbar'],[class*='bar']"));
    }

    public WebElement getTraineesTableElement()       { return driver.findElement(traineesTable); }
    public List<WebElement> getTraineesTableHeaders() {
        return driver.findElements(By.cssSelector("table thead th, table th"));
    }

    public List<WebElement> getCrudButtonElements() {
        try { return driver.findElements(crudButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getLogoutDirectElements() {
        try { return driver.findElements(logoutDirect); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getUserMenuTriggerElements() {
        try { return driver.findElements(userMenuTrigger); }
        catch (Exception e) { return Collections.emptyList(); }
    }
}