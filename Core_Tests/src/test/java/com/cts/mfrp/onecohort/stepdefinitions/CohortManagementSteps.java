package com.cts.mfrp.onecohort.stepdefinitions;

import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.CohortTestData;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
// SoftAssert accessed via context.softAssert (injected by Hooks.setUp)

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Step definitions for CohortManagement.feature (TC-COHORT-001 to TC-COHORT-017).
 *
 * <p>All test data (search keywords, filter status values, create-cohort scenarios)
 * are read from {@code CohortManagementTestData.xlsx} via {@link CohortTestData}.
 * Login credentials come from {@code LoginData.xlsx} via the shared
 * {@link com.cts.mfrp.onecohort.utils.TestDataProvider} used in LoginSteps.
 *
 * <p>Instance fields carry state within a single Cucumber scenario.
 * PicoContainer creates a new instance per scenario, so fields are automatically
 * reset between scenarios — no explicit cleanup needed here.
 */
public class CohortManagementSteps {

    private final TestContext context;
    private CohortManagementPage cohortPage;

    // ── Per-scenario state (PicoContainer resets these for every scenario) ────
    private int    rowCountBeforeSearch;    // TC-004: full-list size before filtering
    private String cohortIdSearched;        // TC-004: the cohort ID we searched for
    private String urlBeforeCohortClick;    // TC-009: URL before clicking cohort ID
    private int    rowCountBeforeCreate;    // TC-017: table size before creation
    private List<String> bugSlFailures;     // TC-016: service lines with no LP options

    public CohortManagementSteps(TestContext context) {
        this.context = context;
    }

    // ── Navigation (Background) ───────────────────────────────────────────────

    /**
     * Navigates from the Super Admin dashboard to the Cohort Management page.
     * Re-uses the {@link SuperAdminDashboardPage#getMenuItemElement(String)} helper
     * which locates nav links by their visible text — no XPath/CSS in this step.
     */
    @And("I navigate to Cohort Management")
    public void iNavigateToCohortManagement() {
        context.getWait().until(ExpectedConditions.urlContains(AppConstants.URL_SUPER_ADMIN));

        new SuperAdminDashboardPage(context.driver)
                .getMenuItemElement(AppConstants.MENU_COHORT_MANAGEMENT)
                .click();

        context.getWait().until(
                ExpectedConditions.urlContains(AppConstants.URL_COHORT_MANAGEMENT));

        cohortPage = new CohortManagementPage(context.driver);
        cohortPage.waitForTableToLoad();
        System.out.println("[CohortSteps] Navigated to: " + context.driver.getCurrentUrl());
    }

    @Then("the cohort management page should load successfully")
    public void cohortManagementPageShouldLoadSuccessfully() {
        context.softAssert.assertTrue(cohortPage.isPageHeadingVisible(),
                "Cohort Management page heading should be visible after navigation");
        context.softAssert.assertTrue(cohortPage.isTableVisible(),
                "Cohort table should be visible after navigation");
    }

    // ── TC-COHORT-001 ─────────────────────────────────────────────────────────

    @And("the cohort page heading should contain {string}")
    public void cohortPageHeadingShouldContain(String expectedText) {
        String heading = cohortPage.getPageHeadingElement().getText();
        context.softAssert.assertTrue(heading.contains(expectedText),
                "Page heading should contain '" + expectedText + "'. Got: " + heading);
        System.out.println("PASS – Heading: " + heading);
    }

    // ── TC-COHORT-002 ─────────────────────────────────────────────────────────

    @Then("the cohort table should have all required column headers")
    public void cohortTableShouldHaveAllRequiredColumnHeaders() {
        List<WebElement> headers = cohortPage.getTableHeaders();
        context.softAssert.assertFalse(headers.isEmpty(), "Table should have <th> elements");

        String allHeaders = headers.stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        context.softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_ID),
                "Missing column: '" + AppConstants.COHORT_COL_ID + "'");
        context.softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_NAME),
                "Missing column: '" + AppConstants.COHORT_COL_NAME + "'");
        context.softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_STATUS),
                "Missing column: '" + AppConstants.COHORT_COL_STATUS + "'");
        context.softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_START_DATE),
                "Missing column: '" + AppConstants.COHORT_COL_START_DATE + "'");
        context.softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_ACTIONS),
                "Missing column: '" + AppConstants.COHORT_COL_ACTIONS + "'");

        System.out.println("PASS – Headers: " + allHeaders);
    }

    // ── TC-COHORT-003 ─────────────────────────────────────────────────────────

    @Then("the cohort table should have at least one data row")
    public void cohortTableShouldHaveAtLeastOneDataRow() {
        cohortPage.clearSearch(); // guard against stale filter from another scenario
        cohortPage.waitForTableToLoad();
        List<WebElement> rows = cohortPage.getTableRows();
        context.softAssert.assertFalse(rows.isEmpty(),
                "Cohort table should have at least one row. Found: 0");
        System.out.println("PASS – " + rows.size() + " row(s) in table");
    }

    @And("the loaded row count should not exceed the total record count")
    public void loadedRowCountShouldNotExceedTotalRecordCount() {
        int totalCount = cohortPage.getTotalRecordCount();
        int loadedRows = cohortPage.getTableRows().size();
        System.out.println("INFO – Loaded rows=" + loadedRows + ", Total records=" + totalCount);
        if (totalCount > 0) {
            context.softAssert.assertTrue(loadedRows <= totalCount,
                    "Loaded rows (" + loadedRows + ") must not exceed " +
                    "Total Records (" + totalCount + ")");
        }
    }

    // ── TC-COHORT-004 ─────────────────────────────────────────────────────────

    @When("I search the cohort list using the first visible cohort ID")
    public void iSearchUsingFirstVisibleCohortId() {
        cohortPage.waitForTableToLoad();
        List<WebElement> rows = cohortPage.getTableRows();
        context.softAssert.assertTrue(rows.size() > 1,
                "Pre-condition: table must have more than 1 row for a filter test. " +
                "Found: " + rows.size());

        rowCountBeforeSearch = rows.size();
        // Read the Cohort ID span from the first row — no hardcoded ID
        cohortIdSearched = rows.get(0)
                .findElement(By.cssSelector("td.ps-4 span.fw-bold.text-primary"))
                .getText().trim();
        System.out.println("[TC-004] Searching for Cohort ID: " + cohortIdSearched);

        cohortPage.searchByKeyword(cohortIdSearched);

        // Wait for Angular debounce to settle and row count to drop
        context.getWait().until(d -> {
            int count = cohortPage.getTableRows().size();
            return count > 0 && count < rowCountBeforeSearch;
        });
        // Extra render cycle
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    @Then("the filtered row count should be less than the unfiltered list")
    public void filteredRowCountShouldBeLessThanUnfilteredList() {
        int filteredCount = cohortPage.getTableRows().size();
        context.softAssert.assertTrue(filteredCount < rowCountBeforeSearch,
                "Filtered rows (" + filteredCount + ") should be fewer than " +
                "the full list (" + rowCountBeforeSearch + ")");
        System.out.println("PASS – Filtered: " + filteredCount +
                " / Original: " + rowCountBeforeSearch);
    }

    @And("the first search result should contain the cohort ID that was searched")
    public void firstSearchResultShouldContainSearchedCohortId() {
        List<WebElement> rows = cohortPage.getTableRows();
        context.softAssert.assertFalse(rows.isEmpty(),
                "Search '" + cohortIdSearched + "' returned 0 rows");

        String firstRowText = rows.get(0).getText().toUpperCase();
        context.softAssert.assertTrue(firstRowText.contains(cohortIdSearched.toUpperCase()),
                "First result should contain '" + cohortIdSearched +
                "'. Row text: " + firstRowText);

        System.out.println("PASS – '" + cohortIdSearched + "' found in first result.");

        // Restore full list
        cohortPage.clearSearch();
        context.getWait().until(d -> cohortPage.getTableRows().size() >= rowCountBeforeSearch);
    }

    // ── TC-COHORT-005 ─────────────────────────────────────────────────────────

    @When("I perform each negative search scenario from the test data")
    public void iPerformEachNegativeSearchScenario() {
        List<CohortTestData.SearchScenario> negScenarios =
                CohortTestData.getNegativeSearchScenarios();
        context.softAssert.assertFalse(negScenarios.isEmpty(),
                "CohortManagementTestData.xlsx SearchCohort sheet must have at least " +
                "one row with ExpectedMinRows=0");

        int rowsBefore = cohortPage.getTableRows().size();

        for (CohortTestData.SearchScenario scenario : negScenarios) {
            System.out.println("[TC-005] Negative search: '" + scenario.keyword +
                    "' (" + scenario.description + ")");
            cohortPage.searchByKeyword(scenario.keyword);

            try {
                context.getWait().until(d -> cohortPage.getTableRows().isEmpty());
            } catch (Exception ignored) {}

            context.softAssert.assertTrue(cohortPage.getTableRows().isEmpty(),
                    "Search '" + scenario.keyword + "' should return 0 rows. " +
                    "Got: " + cohortPage.getTableRows().size());
            System.out.println("PASS – '" + scenario.keyword + "' → 0 rows.");

            cohortPage.clearSearch();
            try { Thread.sleep(800); } catch (InterruptedException ignored) {}
            context.getWait().until(d -> cohortPage.getTableRows().size() >= rowsBefore);
        }
    }

    @Then("each non-matching search should return zero cohort rows")
    public void eachNonMatchingSearchShouldReturnZeroCohortRows() {
        // All assertions are made inline in the @When step above.
        // This @Then step exists purely for Gherkin readability.
        System.out.println("PASS – All negative search scenarios validated.");
    }

    // ── TC-COHORT-006 ─────────────────────────────────────────────────────────

    @When("I click Create New Cohort")
    public void iClickCreateNewCohort() {
        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();
    }

    @Then("the cohort creation modal should be visible")
    public void cohortCreationModalShouldBeVisible() {
        context.softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "Create Cohort modal (div.modal-card) should be visible");
    }

    @And("the modal title should not be empty")
    public void modalTitleShouldNotBeEmpty() {
        String title = cohortPage.getModalTitleText();
        context.softAssert.assertFalse(title.isEmpty(),
                "Modal header h5 should have non-empty title text");
        System.out.println("PASS – Modal title: " + title);
        cohortPage.closeModal();
    }

    // ── TC-COHORT-007 ─────────────────────────────────────────────────────────

    @Then("the create cohort modal should have exactly 2 date input fields")
    public void createModalShouldHaveExactlyTwoDateInputs() {
        List<WebElement> dateInputs = cohortPage.getModalDateInputs();
        context.softAssert.assertEquals(dateInputs.size(), 2,
                "Modal should have exactly 2 <input type='date'> fields. Found: " +
                dateInputs.size());
        System.out.println("PASS – " + dateInputs.size() + " date input(s).");
    }

    @And("the create cohort modal should have no free-text input fields")
    public void createModalShouldHaveNoFreeTextInputs() {
        List<WebElement> textInputs = context.driver.findElements(
                By.cssSelector("div.modal-body input[type='text']," +
                               "div.modal-body input:not([type])"));
        context.softAssert.assertTrue(textInputs.isEmpty(),
                "Modal should NOT have free-text inputs — cohort ID/Name is auto-generated. " +
                "Found: " + textInputs.size());
        System.out.println("PASS – 0 free-text inputs (as expected).");
        cohortPage.closeModal();
    }

    // ── TC-COHORT-008 ─────────────────────────────────────────────────────────

    @When("I click the first Edit button in the cohort table")
    public void iClickTheFirstEditButton() {
        List<WebElement> editBtns = cohortPage.getEditButtons();
        context.softAssert.assertFalse(editBtns.isEmpty(),
                "Each cohort row should have an Edit button");
        editBtns.get(0).click();
        cohortPage.waitForModalVisible();
    }

    @Then("the cohort edit modal should be visible")
    public void cohortEditModalShouldBeVisible() {
        context.softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "Edit modal should open after clicking the pencil icon");
        System.out.println("PASS – Edit modal: " + cohortPage.getModalTitleText());
        cohortPage.closeModal();
    }

    // ── TC-COHORT-009 ─────────────────────────────────────────────────────────

    @When("I click the first Cohort ID link in the cohort table")
    public void iClickTheFirstCohortIdLink() {
        List<WebElement> rows = cohortPage.getTableRows();
        context.softAssert.assertFalse(rows.isEmpty(), "Table must have rows to click a Cohort ID");

        urlBeforeCohortClick = context.driver.getCurrentUrl();
        WebElement idSpan = rows.get(0).findElement(
                By.cssSelector("td.ps-4 span.fw-bold.text-primary"));
        System.out.println("[TC-009] Clicking Cohort ID: " + idSpan.getText().trim());
        idSpan.click();

        context.getWait().until(
                ExpectedConditions.not(ExpectedConditions.urlToBe(urlBeforeCohortClick)));
    }

    @Then("the URL should change to the cohort detail page")
    public void urlShouldChangeToDetailPage() {
        String currentUrl = context.driver.getCurrentUrl();
        context.softAssert.assertNotEquals(currentUrl, urlBeforeCohortClick,
                "Clicking Cohort ID should navigate to a different URL (detail page)");
        System.out.println("PASS – Navigated to detail: " + currentUrl);
    }

    @And("the cohort management page should be accessible after navigating back")
    public void cohortManagementPageShouldBeAccessibleAfterBack() {
        context.driver.navigate().back();
        context.getWait().until(
                ExpectedConditions.urlContains(AppConstants.URL_COHORT_MANAGEMENT));
        cohortPage.waitForTableToLoad();
        context.softAssert.assertTrue(cohortPage.isTableVisible(),
                "Cohort table should be visible after navigating back");
        System.out.println("PASS – Back to cohort management.");
    }

    // ── TC-COHORT-010 ─────────────────────────────────────────────────────────

    @When("I click the Filters button on the cohort page")
    public void iClickTheFiltersButton() {
        context.softAssert.assertTrue(cohortPage.isFiltersBtnVisible(),
                "Filters button (button.btn.btn-outline-secondary) should be visible");
        cohortPage.clickFiltersBtn();
        context.getWait().until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("select.form-select"), 0));
    }

    @Then("the filter panel should reveal at least one dropdown")
    public void filterPanelShouldRevealAtLeastOneDropdown() {
        context.softAssert.assertFalse(cohortPage.getFilterPanelDropdowns().isEmpty(),
                "Clicking Filters should reveal at least one select.form-select dropdown");
        System.out.println("PASS – Filter panel opened.");
        cohortPage.clickFiltersBtn(); // close panel
    }

    // ── TC-COHORT-011 ─────────────────────────────────────────────────────────

    @Then("the Status filter dropdown should contain all expected values from the test data")
    public void statusFilterShouldContainExpectedValues() {
        List<WebElement> dropdowns = cohortPage.getFilterPanelDropdowns();
        context.softAssert.assertTrue(dropdowns.size() >= AppConstants.MIN_FILTER_DROPDOWNS,
                "Filter panel should have at least " + AppConstants.MIN_FILTER_DROPDOWNS +
                " dropdowns. Found: " + dropdowns.size());

        Select statusSel = new Select(dropdowns.get(0));
        List<String> availableOpts = statusSel.getOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());

        List<String> expectedStatuses = CohortTestData.getExpectedFilterStatusValues();
        for (String expected : expectedStatuses) {
            context.softAssert.assertTrue(availableOpts.contains(expected),
                    "Status filter missing '" + expected +
                    "'. Available: " + availableOpts);
        }
        System.out.println("PASS – Status filter options: " + availableOpts);
    }

    @And("the Learning Path filter should have at least one option")
    public void learningPathFilterShouldHaveAtLeastOneOption() {
        List<WebElement> dropdowns = cohortPage.getFilterPanelDropdowns();
        context.softAssert.assertTrue(dropdowns.size() >= 2,
                "Filter panel should have at least 2 dropdowns");

        Select lpSel = new Select(dropdowns.get(1));
        List<String> lpOpts = lpSel.getOptions().stream()
                .map(WebElement::getText).collect(Collectors.toList());
        context.softAssert.assertTrue(lpOpts.size() > 1,
                "Learning Path filter should have options beyond the placeholder. Got: " + lpOpts);
        System.out.println("PASS – LP filter options: " + lpOpts);
        cohortPage.clickFiltersBtn(); // close panel
    }

    // ── TC-COHORT-012 ─────────────────────────────────────────────────────────

    @Then("status badges should be visible in the cohort table")
    public void statusBadgesShouldBeVisibleInTable() {
        List<WebElement> badges = cohortPage.getStatusBadges();
        context.softAssert.assertFalse(badges.isEmpty(),
                "Table should have at least one span.badge.rounded-pill element");
        System.out.println("PASS – " + badges.size() + " badge(s) found.");
    }

    @And("at least one badge should display a known status value")
    public void atLeastOneBadgeShouldShowKnownStatus() {
        List<String> validStatuses = List.of(
                AppConstants.STATUS_PLANNING,
                AppConstants.STATUS_ACTIVE,
                AppConstants.STATUS_COMPLETED,
                AppConstants.STATUS_UPCOMING);

        long matchCount = cohortPage.getStatusBadges().stream()
                .map(b -> b.getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> validStatuses.stream().anyMatch(t::contains))
                .count();

        context.softAssert.assertTrue(matchCount > 0,
                "At least one badge should show a known status " + validStatuses);
        System.out.println("PASS – " + matchCount + " badge(s) with valid status.");
    }

    // ── TC-COHORT-013 ─────────────────────────────────────────────────────────

    @Then("the Create New Cohort button should be enabled")
    public void createNewCohortButtonShouldBeEnabled() {
        context.softAssert.assertTrue(cohortPage.getCreateBtnPrimaryElement().isEnabled(),
                "button.btn.btn-primary (Create New Cohort) should be enabled");
        System.out.println("PASS – Create New Cohort button is enabled.");
    }

    // ── TC-COHORT-014 ─────────────────────────────────────────────────────────

    @And("I click the Cancel button in the modal")
    public void iClickTheCancelButtonInModal() {
        cohortPage.clickCancelBtn();
    }

    @Then("the cohort creation modal should be closed")
    public void cohortCreationModalShouldBeClosed() {
        cohortPage.waitForModalInvisible();
        context.softAssert.assertFalse(cohortPage.isModalCardVisible(),
                "Modal should be hidden after clicking Cancel");
        System.out.println("PASS – Cancel button closed the modal.");
    }

    // ── TC-COHORT-015 (BUG) ──────────────────────────────────────────────────

    @And("I fill the cohort form with the default happy path scenario data")
    public void iFillCohortFormWithDefaultHappyPathData() {
        CohortTestData.CreateCohortScenario sc = CohortTestData.getDefaultHappyPathScenario();
        System.out.println("[CohortSteps] Using scenario: " + sc);
        cohortPage.fillCreateCohortForm(sc.serviceLine, sc.learningPath, sc.employmentType);
    }

    @And("I set the end date to before the start date")
    public void iSetEndDateBeforeStartDate() {
        CohortTestData.CreateCohortScenario bugSc =
                CohortTestData.getBugDateScenarios().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "No BUG_DATE scenario in CreateCohort sheet"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDate = LocalDate.now().plusDays(bugSc.startDateOffset).format(fmt);
        String endDate   = LocalDate.now().plusDays(bugSc.endDateOffset).format(fmt);

        cohortPage.setStartDate(startDate);
        cohortPage.setEndDate(endDate);
        System.out.println("[TC-015] Start=" + startDate + ", End=" + endDate +
                " (end is before start — intentional for bug test)");
    }

    @And("I submit the cohort creation form")
    public void iSubmitTheCohortCreationForm() {
        cohortPage.clickCreateCohortSubmitBtn();
    }

    @Then("the cohort creation should not have succeeded with a success alert")
    public void cohortCreationShouldNotHaveSucceeded() {
        boolean successAlertFired = false;
        String alertText = "";
        try {
            org.openqa.selenium.Alert alert = context.getWait().until(
                    ExpectedConditions.alertIsPresent());
            alertText = alert.getText();
            successAlertFired = alertText != null &&
                    alertText.toLowerCase().contains("success");
            System.out.println("[TC-015] Alert text: " + alertText);
        } catch (Exception ignored) {
            System.out.println("[TC-015] No alert appeared.");
        } finally {
            // Always dismiss so ExtentReport screenshot does not crash
            try { context.driver.switchTo().alert().accept(); } catch (Exception ignored) {}
        }

        context.softAssert.assertFalse(successAlertFired,
                "BUG (TC-COHORT-015): A 'Success' alert fired for an invalid date range " +
                "(End date is BEFORE Start date). Alert: " + alertText);
    }

    @And("the cohort creation modal should still be visible")
    public void cohortCreationModalShouldStillBeVisible() {
        context.softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "BUG (TC-COHORT-015): Modal closed after submitting an invalid date range. " +
                "Form should block submission and keep the modal open.");
        System.out.println("PASS – Form correctly rejected end-before-start date range.");
        cohortPage.closeModal();
        cohortPage.waitForModalInvisible();
    }

    // ── TC-COHORT-016 (BUG) ──────────────────────────────────────────────────

    @When("I check each bug service line scenario from the test data")
    public void iCheckEachBugServiceLineScenario() {
        List<CohortTestData.CreateCohortScenario> bugSLScenarios =
                CohortTestData.getBugServiceLineScenarios();
        context.softAssert.assertFalse(bugSLScenarios.isEmpty(),
                "CreateCohort sheet must have at least one BUG_SL scenario");

        bugSlFailures = new ArrayList<>();

        for (CohortTestData.CreateCohortScenario scenario : bugSLScenarios) {
            String sl = scenario.serviceLine;
            System.out.println("[TC-016] Checking service line: " + sl);

            cohortPage.clickCreateCohort();
            cohortPage.waitForModalVisible();

            boolean hasLPs = cohortPage.doesServiceLineHaveLearningPaths(sl);

            cohortPage.closeModal();
            cohortPage.waitForModalInvisible();

            if (!hasLPs) {
                bugSlFailures.add(sl);
                System.out.println("[TC-016] BUG – '" + sl + "' has no learning paths.");
            } else {
                System.out.println("[TC-016] '" + sl + "' has learning paths (may be fixed).");
            }
        }
    }

    @Then("each checked service line should have no available learning path options")
    public void eachBugServiceLineShouldHaveNoLearningPaths() {
        if (bugSlFailures != null && !bugSlFailures.isEmpty()) {
            StringBuilder report = new StringBuilder();
            bugSlFailures.forEach(sl -> report.append("\n  BUG: '").append(sl)
                    .append("' has no learning path options"));
            context.softAssert.fail("BUG (TC-COHORT-016): " + bugSlFailures.size() +
                    " service line(s) in the dropdown have no valid learning path IDs:" +
                    report +
                    "\n\nExpected fix: remove these service lines from the dropdown " +
                    "OR assign valid learning path IDs to them in the backend.");
        } else {
            System.out.println("PASS – All checked service lines have learning paths.");
        }
    }

    // ── TC-COHORT-017 ─────────────────────────────────────────────────────────

    @Given("I note the current cohort row count")
    public void iNoteCurrentCohortRowCount() {
        cohortPage.waitForTableToLoad();
        rowCountBeforeCreate = cohortPage.getTableRows().size();
        System.out.println("[TC-017] Row count before creation: " + rowCountBeforeCreate);
    }

    @And("I set valid future start and end dates")
    public void iSetValidFutureDates() {
        // Use the default HAPPY_PATH scenario's date offsets — always future, always valid.
        CohortTestData.CreateCohortScenario sc = CohortTestData.getDefaultHappyPathScenario();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().plusDays(sc.startDateOffset).format(fmt);
        String endDate   = LocalDate.now().plusDays(sc.endDateOffset).format(fmt);

        cohortPage.setStartDate(startDate);
        cohortPage.setEndDate(endDate);
        System.out.println("[TC-017] Start=" + startDate + ", End=" + endDate);
    }

    @Then("the cohort creation should succeed")
    public void cohortCreationShouldSucceed() {
        String alertText = "";
        try {
            org.openqa.selenium.Alert alert = context.getWait().until(
                    ExpectedConditions.alertIsPresent());
            alertText = alert.getText();
            System.out.println("[TC-017] Alert: " + alertText);
        } catch (Exception e) {
            System.out.println("[TC-017] No alert — verifying via row count.");
        } finally {
            try { context.driver.switchTo().alert().accept(); } catch (Exception ignored) {}
        }

        if (!alertText.isEmpty()) {
            context.softAssert.assertFalse(
                    alertText.toLowerCase().contains("failed") ||
                    alertText.toLowerCase().contains("error") ||
                    alertText.toLowerCase().contains("invalid"),
                    "Cohort creation failed. Alert: " + alertText);
        }

        cohortPage.waitForModalInvisible();
        context.softAssert.assertFalse(cohortPage.isModalCardVisible(),
                "Modal should auto-close after successful creation");
    }

    @And("the cohort table should have at least one more row than before")
    public void cohortTableShouldHaveAtLeastOneMoreRow() {
        cohortPage.waitForTableToLoad();
        final int expectedMin = rowCountBeforeCreate + 1;
        context.getWait().until(d -> cohortPage.getTableRows().size() >= expectedMin);

        int rowsAfter = cohortPage.getTableRows().size();
        context.softAssert.assertTrue(rowsAfter >= expectedMin,
                "Row count should increase by ≥ 1. Before=" + rowCountBeforeCreate +
                ", After=" + rowsAfter);
        System.out.println("PASS – Cohort created. Rows: " + rowCountBeforeCreate +
                " → " + rowsAfter);
    }
}
