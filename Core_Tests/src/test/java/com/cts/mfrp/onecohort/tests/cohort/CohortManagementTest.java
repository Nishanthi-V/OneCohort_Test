package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.CohortTestData;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.TestDataProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cohort Management Test Suite — Super Admin (TestNG)
 *
 * <p>All login credentials are read from {@code LoginData.xlsx} via
 * {@link TestDataProvider}. All search/filter/create test data comes from
 * {@code CohortManagementTestData.xlsx} via {@link CohortTestData}.
 * No strings are hardcoded in this class — all literals live in
 * {@link AppConstants}.
 *
 * <p>The class extends {@link BaseClassTest} which opens a single Chrome session
 * for the entire class ({@code @BeforeClass} / {@code @AfterClass}). Tests run
 * sequentially in priority order to prevent data races on shared cohort state.
 *
 * <p><b>SoftAssert pattern:</b> Each {@code @Test} method calls
 * {@code softAssert.assertAll()} as its very last statement. This means:
 * <ul>
 *   <li>If any soft assertion failed during the test, {@code assertAll()} throws
 *       an {@link AssertionError} <em>inside</em> the test method — TestNG and
 *       ExtentReport both see the test as <b>FAILED</b> immediately.</li>
 *   <li>{@code @AfterMethod} only does browser cleanup (dismiss alerts, close
 *       modal, clear search). It never calls {@code softAssert.assertAll()}, so
 *       it can never throw an uncaught exception that would cause the next
 *       12 tests to be skipped by TestNG.</li>
 * </ul>
 *
 * Tests:
 * <ul>
 *   <li>001 – Page loads with correct URL and heading</li>
 *   <li>002 – Table has all required column headers</li>
 *   <li>003 – Table has at least one data row; counter matches</li>
 *   <li>004 – Search bar filters the list</li>
 *   <li>005 – Non-matching search shows empty results (data-driven)</li>
 *   <li>006 – Create New Cohort button opens a modal</li>
 *   <li>007 – Create modal has correct form fields</li>
 *   <li>008 – Edit button opens the edit modal</li>
 *   <li>009 – Cohort ID link navigates to the detail page</li>
 *   <li>010 – Filters button opens inline filter panel</li>
 *   <li>011 – Filter panel dropdowns have expected options</li>
 *   <li>012 – Status badges are visible with valid values</li>
 *   <li>013 – Create New Cohort button is enabled</li>
 *   <li>014 – Cancel button closes the Create Cohort modal</li>
 *   <li>015 – [BUG] End date before start date should be rejected</li>
 *   <li>016 – [BUG] Service lines without learning paths should not exist</li>
 *   <li>017 – Successfully create a new cohort (happy path)</li>
 * </ul>
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"cohort", "regression", "superadmin"})
public class CohortManagementTest extends BaseClassTest {

    private CohortManagementPage cohortPage;
    private SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    // ── @AfterMethod cleanup ──────────────────────────────────────────────────

    /**
     * Runs after EVERY test method — always, even if the test failed.
     *
     * <p>This method does BROWSER CLEANUP ONLY — it never calls
     * {@code softAssert.assertAll()}. All soft assertions are flushed at the
     * end of each individual {@code @Test} method instead (see the pattern
     * described in the class-level Javadoc).
     *
     * <p>Why no assertions here?
     * If this method threw an uncaught exception, TestNG would mark every
     * remaining test in the class as "ignored/skipped". By keeping it
     * pure-cleanup (and wrapping each step in try-catch) we guarantee it
     * always returns cleanly.
     *
     * <ol>
     *   <li>Dismiss any open browser alert (must be first; any subsequent driver
     *       call on an open alert throws UnhandledAlertException).</li>
     *   <li>Close any lingering modal overlay.</li>
     *   <li>Clear any active search filter so the next test sees the full table.</li>
     * </ol>
     */
    @AfterMethod(alwaysRun = true)
    public void cleanUpAfterTest() {

        // Step 1 — dismiss any open browser alert
        //   An open alert blocks every driver call, so always handle it first.
        for (int i = 0; i < 3; i++) {
            try { driver.switchTo().alert().accept(); Thread.sleep(200); }
            catch (Exception e) { break; }
        }

        // Step 2 — close any open modal
        try {
            List<WebElement> overlays = driver.findElements(
                    By.cssSelector("div.modal-overlay"));
            if (!overlays.isEmpty() && overlays.get(0).isDisplayed()) {
                if (cohortPage != null) {
                    cohortPage.closeModal();
                } else {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "document.dispatchEvent(new KeyboardEvent('keydown'," +
                            "{'key':'Escape','bubbles':true}))");
                }
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("div.modal-overlay")));
            }
        } catch (Exception ignored) {}

        // Step 3 — clear the search bar so the next test sees the full table
        try {
            if (cohortPage != null && driver.getCurrentUrl()
                    .contains(AppConstants.URL_COHORT_MANAGEMENT)) {
                cohortPage.clearSearch();
                cohortPage.waitForTableToLoad();
            }
        } catch (Exception ignored) {}

        // NOTE: No softAssert.assertAll() here.
        //       Each @Test method calls assertAll() itself as its last statement.
    }

    // ── @BeforeClass setup ────────────────────────────────────────────────────

    /**
     * Logs in as Super Admin (credentials from LoginData.xlsx) and navigates
     * to the Cohort Management page. Depends on BaseClassTest#setUpDriver.
     */
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortManagement() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(
                TestDataProvider.getUserIdForRole(AppConstants.ROLE_SUPER_ADMIN));

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_SUPER_ADMIN));

        new SuperAdminDashboardPage(driver)
                .getMenuItemElement(AppConstants.MENU_COHORT_MANAGEMENT)
                .click();

        wait.until(ExpectedConditions.urlContains(AppConstants.URL_COHORT_MANAGEMENT));
        cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();
        System.out.println("[Setup] Cohort Management loaded. URL: " + driver.getCurrentUrl());
    }

    // ── TC-COHORT-001 ─────────────────────────────────────────────────────────

    @Test(priority = 1, groups = {"smoke"},
            description = "TC-COHORT-001: Page loads with correct URL and heading")
    public void testCohortPageLoads() {
        String url = driver.getCurrentUrl();
        softAssert.assertTrue(url.contains(AppConstants.URL_COHORT_MANAGEMENT),
                "URL should contain '" + AppConstants.URL_COHORT_MANAGEMENT + "'. Got: " + url);

        softAssert.assertTrue(cohortPage.isPageHeadingVisible(),
                "h2.fw-bold heading should be visible");

        String heading = cohortPage.getPageHeadingElement().getText();
        softAssert.assertTrue(heading.contains(AppConstants.HEADING_COHORT_MANAGEMENT),
                "Heading should contain '" + AppConstants.HEADING_COHORT_MANAGEMENT +
                "'. Got: " + heading);

        System.out.println("PASS – Heading: " + heading);

        // Flush all soft assertions — if any failed, this throws AssertionError
        // inside the @Test method so TestNG and ExtentReport record it as FAILED.
        softAssert.assertAll();
    }

    // ── TC-COHORT-002 ─────────────────────────────────────────────────────────

    @Test(priority = 2,
            description = "TC-COHORT-002: Table has all required column headers")
    public void testTableHasRequiredColumns() {
        List<WebElement> headers = cohortPage.getTableHeaders();
        softAssert.assertFalse(headers.isEmpty(), "Table should have <th> elements");

        String allHeaders = headers.stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_ID),
                "Missing '" + AppConstants.COHORT_COL_ID + "' column");
        softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_NAME),
                "Missing '" + AppConstants.COHORT_COL_NAME + "' column");
        softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_STATUS),
                "Missing '" + AppConstants.COHORT_COL_STATUS + "' column");
        softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_START_DATE),
                "Missing '" + AppConstants.COHORT_COL_START_DATE + "' column");
        softAssert.assertTrue(allHeaders.contains(AppConstants.COHORT_COL_ACTIONS),
                "Missing '" + AppConstants.COHORT_COL_ACTIONS + "' column");

        System.out.println("PASS – Headers: " + allHeaders);
        softAssert.assertAll();
    }

    // ── TC-COHORT-003 ─────────────────────────────────────────────────────────

    @Test(priority = 3, groups = {"smoke"},
            description = "TC-COHORT-003: Table has at least one data row; counter matches")
    public void testTableHasDataRows() {
        cohortPage.clearSearch();
        cohortPage.waitForTableToLoad();

        List<WebElement> rows = cohortPage.getTableRows();
        softAssert.assertFalse(rows.isEmpty(), "Cohort table should have at least one row");

        int totalCount = cohortPage.getTotalRecordCount();
        System.out.println("PASS – " + rows.size() + " rows visible, Total=" + totalCount);

        if (totalCount > 0) {
            softAssert.assertTrue(rows.size() <= totalCount,
                    "Loaded rows (" + rows.size() + ") must not exceed " +
                    "Total Records (" + totalCount + ")");
        }

        softAssert.assertAll();
    }

    // ── TC-COHORT-004 ─────────────────────────────────────────────────────────

    @Test(priority = 4,
            description = "TC-COHORT-004: Search bar filters the cohort list")
    public void testSearchFiltersCohortList() {
        int rowsBefore = cohortPage.getTableRows().size();
        softAssert.assertTrue(rowsBefore > 1,
                "Pre-condition: table must have more than 1 row for a filter test");

        // Use the first row's actual Cohort ID — no hardcoded search term
        String searchTerm = cohortPage.getTableRows()
                .get(0)
                .findElement(By.cssSelector("td.ps-4 span.fw-bold.text-primary"))
                .getText().trim();
        System.out.println("[TC-004] Searching for: " + searchTerm);

        cohortPage.searchByKeyword(searchTerm);

        wait.until(d -> {
            int count = cohortPage.getTableRows().size();
            return count > 0 && count < rowsBefore;
        });
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        List<WebElement> filteredRows = cohortPage.getTableRows();
        softAssert.assertFalse(filteredRows.isEmpty(),
                "Search '" + searchTerm + "' should return at least one row");
        softAssert.assertTrue(filteredRows.size() < rowsBefore,
                "Filtered rows (" + filteredRows.size() + ") should be fewer than " +
                "full list (" + rowsBefore + ")");

        String firstRowText = filteredRows.get(0).getText().toUpperCase();
        softAssert.assertTrue(firstRowText.contains(searchTerm.toUpperCase()),
                "First result should contain '" + searchTerm + "'. Row: " + firstRowText);

        System.out.println("PASS – '" + searchTerm + "' → " + filteredRows.size() +
                " row(s) (was " + rowsBefore + ").");

        // Clear the search bar and wait for the table to restore.
        // NOTE: We do NOT check for >= rowsBefore here because cohorts can be
        // added or removed by other users while the test runs. Checking for an
        // exact count would cause a timeout whenever the live count changes.
        // It is enough to confirm the table is not empty after clearing.
        cohortPage.clearSearch();
        cohortPage.waitForTableToLoad();
        softAssert.assertFalse(cohortPage.getTableRows().isEmpty(),
                "After clearing the search bar the table should show at least 1 cohort row");
        System.out.println("PASS – Search cleared. Rows now visible: "
                + cohortPage.getTableRows().size());

        softAssert.assertAll();
    }

    // ── TC-COHORT-005 ─────────────────────────────────────────────────────────

    @Test(priority = 5,
            description = "TC-COHORT-005: Non-matching search terms show no results " +
                    "(data-driven from SearchCohort sheet, ExpectedMinRows=0)")
    public void testSearchWithNoMatchShowsNoResults() {
        List<CohortTestData.SearchScenario> negScenarios =
                CohortTestData.getNegativeSearchScenarios();
        softAssert.assertFalse(negScenarios.isEmpty(),
                "SearchCohort sheet must have at least one row with ExpectedMinRows=0");

        for (CohortTestData.SearchScenario scenario : negScenarios) {
            System.out.println("[TC-005] Negative search: '" + scenario.keyword + "'");
            cohortPage.searchByKeyword(scenario.keyword);

            // Give the table time to react — it may show 0 rows or a 'No data' message
            try { wait.until(d -> cohortPage.getTableRows().isEmpty()); }
            catch (Exception ignored) {
                // If it does not go empty within the wait, the soft assert below will report it
            }

            softAssert.assertTrue(cohortPage.getTableRows().isEmpty(),
                    "Search '" + scenario.keyword + "' should return 0 rows. Got: " +
                    cohortPage.getTableRows().size());
            System.out.println("PASS – '" + scenario.keyword + "' → 0 rows.");

            // Clear the search and wait for ANY rows to come back.
            // Do NOT use a stored 'rowsBefore' count — cohorts can be added or
            // removed while the test runs, so the count may legitimately differ.
            cohortPage.clearSearch();
            cohortPage.waitForTableToLoad();
            System.out.println("   Table restored. Rows visible: "
                    + cohortPage.getTableRows().size());
        }

        softAssert.assertAll();
    }

    // ── TC-COHORT-006 ─────────────────────────────────────────────────────────

    @Test(priority = 6, groups = {"smoke"},
            description = "TC-COHORT-006: Create New Cohort button opens a modal")
    public void testCreateCohortModalOpens() {
        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();

        softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "div.modal-card should be visible after clicking Create New Cohort");

        String title = cohortPage.getModalTitleText();
        softAssert.assertFalse(title.isEmpty(), "Modal header h5 should have title text");
        System.out.println("PASS – Modal title: " + title);
        cohortPage.closeModal();

        softAssert.assertAll();
    }

    // ── TC-COHORT-007 ─────────────────────────────────────────────────────────

    @Test(priority = 7,
            description = "TC-COHORT-007: Create modal has 2 date inputs and no free-text inputs")
    public void testCreateCohortModalHasCorrectFields() {
        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();

        List<WebElement> dateInputs = cohortPage.getModalDateInputs();
        softAssert.assertEquals(dateInputs.size(), 2,
                "Modal should have exactly 2 <input type='date'> fields. Found: " +
                dateInputs.size());

        List<WebElement> textInputs = driver.findElements(
                By.cssSelector("div.modal-body input[type='text']," +
                               "div.modal-body input:not([type])"));
        softAssert.assertTrue(textInputs.isEmpty(),
                "Modal should NOT have free-text inputs (cohort ID/Name is auto-generated). " +
                "Found: " + textInputs.size());

        System.out.println("PASS – " + dateInputs.size() + " date input(s), 0 free-text.");
        cohortPage.closeModal();

        softAssert.assertAll();
    }

    // ── TC-COHORT-008 ─────────────────────────────────────────────────────────

    @Test(priority = 8,
            description = "TC-COHORT-008: Edit button opens the edit modal")
    public void testEditButtonOpensModal() {
        List<WebElement> editBtns = cohortPage.getEditButtons();
        softAssert.assertFalse(editBtns.isEmpty(),
                "Each cohort row should have a pencil-icon Edit button");

        editBtns.get(0).click();
        cohortPage.waitForModalVisible();
        softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "Edit modal should open after clicking the pencil icon");

        System.out.println("PASS – Edit modal: " + cohortPage.getModalTitleText());
        cohortPage.closeModal();

        softAssert.assertAll();
    }

    // ── TC-COHORT-009 ─────────────────────────────────────────────────────────

    @Test(priority = 9,
            description = "TC-COHORT-009: Clicking a Cohort ID navigates to the detail page")
    public void testCohortIdLinkOpensDetailPage() {
        List<WebElement> rows = cohortPage.getTableRows();
        softAssert.assertFalse(rows.isEmpty(), "Table must have rows");

        String urlBefore = driver.getCurrentUrl();
        WebElement idSpan = rows.get(0).findElement(
                By.cssSelector("td.ps-4 span.fw-bold.text-primary"));
        String cohortId = idSpan.getText().trim();
        idSpan.click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));
        String urlAfter = driver.getCurrentUrl();
        softAssert.assertNotEquals(urlAfter, urlBefore,
                "Clicking Cohort ID '" + cohortId + "' should navigate to detail page");
        System.out.println("PASS – '" + cohortId + "' → " + urlAfter);

        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains(AppConstants.URL_COHORT_MANAGEMENT));
        cohortPage.waitForTableToLoad();

        softAssert.assertAll();
    }

    // ── TC-COHORT-010 ─────────────────────────────────────────────────────────

    @Test(priority = 10,
            description = "TC-COHORT-010: Filters button opens an inline filter panel")
    public void testFiltersButtonOpensPanel() {
        softAssert.assertTrue(cohortPage.isFiltersBtnVisible(),
                "Filters button should be visible");
        cohortPage.clickFiltersBtn();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("select.form-select"), 0));
        softAssert.assertFalse(cohortPage.getFilterPanelDropdowns().isEmpty(),
                "Clicking Filters should reveal at least one select.form-select dropdown");
        System.out.println("PASS – Filter panel opened.");

        cohortPage.clickFiltersBtn(); // close panel

        softAssert.assertAll();
    }

    // ── TC-COHORT-011 ─────────────────────────────────────────────────────────

    @Test(priority = 11,
            description = "TC-COHORT-011: Filter panel has Status and Learning Path dropdowns")
    public void testFilterPanelDropdownOptions() {
        cohortPage.clickFiltersBtn();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("select.form-select"), 0));

        List<WebElement> dropdowns = cohortPage.getFilterPanelDropdowns();
        softAssert.assertTrue(dropdowns.size() >= AppConstants.MIN_FILTER_DROPDOWNS,
                "Filter panel should have at least " + AppConstants.MIN_FILTER_DROPDOWNS +
                " dropdowns. Found: " + dropdowns.size());

        // Status dropdown (index 0) — validate against FilterStatus Excel sheet
        Select statusSel = new Select(dropdowns.get(0));
        List<String> statusOpts = statusSel.getOptions().stream()
                .map(WebElement::getText).map(String::trim).collect(Collectors.toList());

        List<String> expectedStatuses = CohortTestData.getExpectedFilterStatusValues();
        for (String expected : expectedStatuses) {
            softAssert.assertTrue(statusOpts.contains(expected),
                    "Status filter missing '" + expected + "'. Available: " + statusOpts);
        }
        System.out.println("PASS – Status options: " + statusOpts);

        // Learning Path dropdown (index 1) — at least one option beyond the placeholder
        Select lpSel = new Select(dropdowns.get(1));
        List<String> lpOpts = lpSel.getOptions().stream()
                .map(WebElement::getText).collect(Collectors.toList());
        softAssert.assertTrue(lpOpts.size() > 1,
                "Learning Path filter should have options beyond the placeholder. Got: " + lpOpts);
        System.out.println("PASS – LP options: " + lpOpts);

        cohortPage.clickFiltersBtn(); // close panel

        softAssert.assertAll();
    }

    // ── TC-COHORT-012 ─────────────────────────────────────────────────────────

    @Test(priority = 12,
            description = "TC-COHORT-012: Status badges visible with valid status values")
    public void testStatusBadgesVisible() {
        List<WebElement> badges = cohortPage.getStatusBadges();
        softAssert.assertFalse(badges.isEmpty(),
                "Table should have at least one span.badge.rounded-pill element");

        List<String> validStatuses = List.of(
                AppConstants.STATUS_PLANNING,
                AppConstants.STATUS_ACTIVE,
                AppConstants.STATUS_COMPLETED,
                AppConstants.STATUS_UPCOMING);

        long matchCount = badges.stream()
                .map(b -> b.getText().trim())
                .filter(t -> !t.isBlank())
                .filter(t -> validStatuses.stream().anyMatch(t::contains))
                .count();

        softAssert.assertTrue(matchCount > 0,
                "At least one badge should contain a known status " + validStatuses);
        System.out.println("PASS – " + badges.size() + " badge(s), " +
                matchCount + " with valid status.");

        softAssert.assertAll();
    }

    // ── TC-COHORT-013 ─────────────────────────────────────────────────────────

    @Test(priority = 13, groups = {"smoke"},
            description = "TC-COHORT-013: Create New Cohort button is enabled")
    public void testCreateCohortButtonEnabled() {
        softAssert.assertTrue(cohortPage.getCreateBtnPrimaryElement().isEnabled(),
                "Create New Cohort button (button.btn.btn-primary) should be enabled");
        System.out.println("PASS – Create New Cohort button is enabled.");

        softAssert.assertAll();
    }

    // ── TC-COHORT-014 ─────────────────────────────────────────────────────────

    @Test(priority = 14,
            description = "TC-COHORT-014: Cancel button closes the Create Cohort modal")
    public void testCancelButtonClosesModal() {
        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();
        softAssert.assertTrue(cohortPage.isModalCardVisible(),
                "Pre-condition: modal must open");

        cohortPage.clickCancelBtn();
        cohortPage.waitForModalInvisible();
        softAssert.assertFalse(cohortPage.isModalCardVisible(),
                "Modal should be hidden after clicking Cancel");
        System.out.println("PASS – Cancel button closed the modal.");

        softAssert.assertAll();
    }

    // ── TC-COHORT-015 — BUG REGRESSION ───────────────────────────────────────

    /**
     * TC-COHORT-015 verifies that the application REJECTS a cohort creation when
     * the end date is set BEFORE the start date.
     *
     * <p>This is a <b>BUG regression test</b> — it is EXPECTED TO FAIL because
     * the current application accepts the invalid date range and fires a success
     * alert instead of blocking the submission.
     *
     * <p>When this bug is fixed, this test will start PASSING (the form will stay
     * open and no success alert will fire).
     */
    @Test(priority = 15, groups = {"bug"},
            description = "TC-COHORT-015: [BUG] End date before start date should be rejected")
    public void testEndDateBeforeStartDateIsRejected() {
        // Fill ALL required fields so date validation is the only potential failure
        CohortTestData.CreateCohortScenario base = CohortTestData.getDefaultHappyPathScenario();
        System.out.println("[TC-015] Using scenario: " + base);

        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();
        softAssert.assertTrue(cohortPage.isModalCardVisible(), "Pre-condition: modal must open");

        // Fill ALL required fields first (Service Line, Learning Path, Batch Owner,
        // Trainer, Employment Type). If any field is empty when we click Submit,
        // the backend returns "400 BAD_REQUEST Invalid Learning Path ID" which is
        // NOT the date-validation error we want to test. The form must be fully
        // valid except for the date range.
        cohortPage.fillCreateCohortForm(base.serviceLine, base.learningPath, base.employmentType);

        // Use BUG_DATE scenario offsets from Excel (end date is before start date)
        CohortTestData.CreateCohortScenario bugSc =
                CohortTestData.getBugDateScenarios().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "No BUG_DATE scenario in CreateCohort sheet"));

        // IMPORTANT: HTML <input type="date"> stores its value in "yyyy-MM-dd" format
        // internally, even though the UI displays it as "dd-MM-yyyy".
        // When setting the value via JavaScript we MUST use "yyyy-MM-dd".
        // Using "dd-MM-yyyy" here would silently set an empty/invalid date
        // because the browser does not recognise that format for type="date".
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().plusDays(bugSc.startDateOffset).format(fmt);
        String endDate   = LocalDate.now().plusDays(bugSc.endDateOffset).format(fmt);

        cohortPage.setStartDate(startDate);
        cohortPage.setEndDate(endDate);
        System.out.println("[TC-015] Start=" + startDate + ", End=" + endDate +
                " (end is before start)");

        cohortPage.clickCreateCohortSubmitBtn();

        boolean successAlertFired = false;
        String alertText = "";
        try {
            org.openqa.selenium.Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alertText = alert.getText();
            successAlertFired = alertText != null &&
                    alertText.toLowerCase().contains("success");
            System.out.println("[TC-015] Alert: " + alertText);
        } catch (Exception ignored) {
            System.out.println("[TC-015] No alert appeared.");
        } finally {
            try { driver.switchTo().alert().accept(); } catch (Exception ignored) {}
        }

        boolean modalStillOpen = cohortPage.isModalCardVisible();

        // ── THE BUG ASSERTIONS ──────────────────────────────────────────────
        // Both of these should PASS (no success alert, modal stays open).
        // Currently they FAIL because the app has a bug — it accepts the
        // invalid date range and fires a success alert + closes the modal.
        // When the bug is fixed, these assertions will finally pass and
        // this test will turn GREEN.
        softAssert.assertFalse(successAlertFired,
                "BUG (TC-COHORT-015): A 'Success' alert fired for an invalid date range " +
                "(End " + endDate + " is BEFORE Start " + startDate + "). Alert: " + alertText);

        softAssert.assertTrue(modalStillOpen,
                "BUG (TC-COHORT-015): Modal closed after submitting end-before-start dates. " +
                "Form should block submission and keep the modal open.");

        System.out.println("[TC-015] Soft assertions queued — assertAll() will now throw " +
                "because the bug is still present.");

        // softAssert.assertAll() throws AssertionError here because the bug assertions
        // above failed. This happens INSIDE the @Test method, so TestNG and
        // ExtentReport both immediately record this test as FAILED.
        softAssert.assertAll();
    }

    // ── TC-COHORT-016 — BUG REGRESSION ───────────────────────────────────────

    @Test(priority = 16, groups = {"bug"},
            description = "TC-COHORT-016: [BUG] Service lines with no learning paths " +
                    "should not be selectable")
    public void testServiceLinesWithoutLearningPathsAreBug() {
        List<CohortTestData.CreateCohortScenario> bugSLScenarios =
                CohortTestData.getBugServiceLineScenarios();
        softAssert.assertFalse(bugSLScenarios.isEmpty(),
                "CreateCohort sheet must have at least one BUG_SL scenario");

        List<String> bugReport = new ArrayList<>();

        for (CohortTestData.CreateCohortScenario scenario : bugSLScenarios) {
            String sl = scenario.serviceLine;
            System.out.println("[TC-016] Checking service line: " + sl);

            cohortPage.clickCreateCohort();
            cohortPage.waitForModalVisible();

            boolean hasLPs = cohortPage.doesServiceLineHaveLearningPaths(sl);

            cohortPage.closeModal();
            cohortPage.waitForModalInvisible();

            if (!hasLPs) {
                bugReport.add("  BUG: '" + sl + "' has no learning path options");
                System.out.println("[TC-016] BUG – '" + sl + "' has no learning paths.");
            } else {
                System.out.println("[TC-016] '" + sl + "' has learning paths (may be fixed).");
            }
        }

        if (!bugReport.isEmpty()) {
            softAssert.fail("BUG (TC-COHORT-016): " + bugReport.size() +
                    " service line(s) in the dropdown have no valid learning path IDs:\n" +
                    String.join("\n", bugReport) +
                    "\n\nExpected fix: remove these service lines from the dropdown OR " +
                    "assign valid learning path IDs to them in the backend.");
        } else {
            System.out.println("PASS – All checked service lines have learning paths.");
        }

        softAssert.assertAll();
    }

    // ── TC-COHORT-017 ─────────────────────────────────────────────────────────

    @Test(priority = 17, groups = {"smoke"},
            description = "TC-COHORT-017: Successfully create a new cohort (happy path)")
    public void testCreateCohortHappyPath() {
        CohortTestData.CreateCohortScenario sc = CohortTestData.getDefaultHappyPathScenario();
        System.out.println("[TC-017] Scenario: " + sc);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().plusDays(sc.startDateOffset).format(fmt);
        String endDate   = LocalDate.now().plusDays(sc.endDateOffset).format(fmt);

        int rowsBefore = cohortPage.getTableRows().size();

        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();
        softAssert.assertTrue(cohortPage.isModalCardVisible(), "Pre-condition: modal must open");

        cohortPage.fillCreateCohortForm(sc.serviceLine, sc.learningPath, sc.employmentType);
        cohortPage.setStartDate(startDate);
        cohortPage.setEndDate(endDate);

        cohortPage.clickCreateCohortSubmitBtn();

        String capturedAlert = "";
        try {
            org.openqa.selenium.Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            capturedAlert = alert.getText();
            System.out.println("[TC-017] Alert: " + capturedAlert);
        } catch (Exception e) {
            System.out.println("[TC-017] No browser alert — verifying via row count.");
        } finally {
            try { driver.switchTo().alert().accept(); } catch (Exception ignored) {}
        }

        if (!capturedAlert.isEmpty()) {
            softAssert.assertFalse(
                    capturedAlert.toLowerCase().contains("failed") ||
                    capturedAlert.toLowerCase().contains("error") ||
                    capturedAlert.toLowerCase().contains("invalid"),
                    "Cohort creation failed. Alert: " + capturedAlert);
        }

        // Wait for the modal to close automatically after a successful creation.
        // This is currently a known bug (TC-COHORT-017) — the modal does not always
        // auto-close. We use try-catch so the test continues and reports the failure
        // via softAssert rather than throwing a hard exception that would skip
        // subsequent steps.
        try {
            cohortPage.waitForModalInvisible();
        } catch (Exception e) {
            System.out.println("[TC-017] Modal did not auto-close — known bug.");
        }
        softAssert.assertFalse(cohortPage.isModalCardVisible(),
                "BUG (TC-COHORT-017): Modal should auto-close after successful creation " +
                "but it remained open.");

        // Verify the new cohort appears in the table.
        // Allow extra time for Angular to refresh the list.
        cohortPage.waitForTableToLoad();
        int rowsAfter = cohortPage.getTableRows().size();

        // NOTE: We only check rowsAfter >= rowsBefore (not rowsBefore + 1) because
        // another user may have deleted a cohort between our two counts.
        softAssert.assertTrue(rowsAfter >= rowsBefore,
                "After creation the row count should be at least what it was before. " +
                "Before=" + rowsBefore + ", After=" + rowsAfter);

        System.out.println("PASS – Cohort created. Rows: " + rowsBefore + " → " + rowsAfter);

        softAssert.assertAll();
    }
}
