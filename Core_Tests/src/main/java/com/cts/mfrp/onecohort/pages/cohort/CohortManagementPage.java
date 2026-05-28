package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CohortManagementPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    // Table / grid
    private final By dataTable           = By.cssSelector("table.table, .table-responsive table");
    private final By specificTable       = By.cssSelector("table.table.table-hover.align-middle");
    private final By tableHeaders        = By.cssSelector("table.table thead th, table.table thead td");
    private final By tableHeaderLight    = By.cssSelector("thead.table-light");
    private final By tableRows           = By.cssSelector("table.table tbody tr, .table-responsive table tbody tr");
    private final By tableRowsAligned    = By.cssSelector("tbody tr.align-middle");
    private final By cohortIdSpan        = By.cssSelector("span.fw-bold.text-primary");
    private final By statusBadge         = By.cssSelector("span.badge.rounded-pill");

    // Page info
    private final By pageTitle           = By.cssSelector("h2.fw-bold");
    private final By pageSubtitle        = By.cssSelector("p.text-muted");
    private final By totalRecordsLabel   = By.cssSelector("span.text-muted b");
    private final By loadedCountLabel    = By.cssSelector("span.text-primary.px-2.border-start b");

    // Controls
    private final By createCohortBtn     = By.xpath(
            "//button[contains(normalize-space(),'Create Cohort') " +
            "or contains(normalize-space(),'Add Cohort') " +
            "or contains(normalize-space(),'New Cohort')]");
    private final By createBtnPrimary    = By.cssSelector("button.btn.btn-primary");
    private final By filtersBtn          = By.cssSelector("button.btn.btn-outline-secondary");
    private final By filterSelect        = By.cssSelector("select.form-select");
    private final By searchInput         = By.cssSelector(
            "input[type='search'], " +
            "input[type='text'][placeholder*='Search'], " +
            "input[type='text'][placeholder*='search'], " +
            "input[placeholder='Search by Cohort ID or Name...'], " +
            "input[formcontrolname*='search']");

    // Row action buttons
    private final By editBtn             = By.cssSelector("button.btn.btn-link.text-muted.p-1");
    private final By deleteBtn           = By.cssSelector("button.btn.btn-link.text-danger.p-1");

    // Modal
    private final By modalCard           = By.cssSelector("div.modal-card");
    private final By modalTitle          = By.cssSelector("div.modal-header h5");
    private final By modalOverlay        = By.cssSelector("[class*='modal'], [role='dialog']");
    private final By cancelBtn           = By.cssSelector("button.btn-modal-secondary");

    // ── CREATE modal form fields ───────────────────────────────────────────────
    // Service Line in CREATE = dropdown (<select name="serviceLine">)
    private final By createServiceLineDd = By.cssSelector("select[name='serviceLine']");
    private final By learningPathDd      = By.cssSelector("select[name='learningPath']");
    private final By batchOwnerDd        = By.cssSelector("select[name='batchOwner']");
    private final By trainerDd           = By.xpath("//select[.//option[text()='Select Trainer']]");
    private final By dateInputsByType    = By.cssSelector("input[type='date']");

    // ── CREATE modal form fields — modal-body scoped (more specific) ──────────
    // Using div.modal-body scope prevents matching page-level selects if any exist.
    private final By modalServiceLine    = By.cssSelector("div.modal-body select[name='serviceLine']");
    private final By modalLearningPath   = By.cssSelector("div.modal-body select[name='learningPath']");
    private final By modalBatchOwner     = By.cssSelector("div.modal-body select[name='batchOwner']");
    private final By modalTrainer        = By.xpath(
            "//div[contains(@class,'modal-body')]" +
            "//label[contains(normalize-space(.),'Trainer')]" +
            "/following-sibling::select[1]");
    private final By modalEmploymentType = By.cssSelector("div.modal-body select[name='employmentType']");
    private final By modalDateInputs     = By.cssSelector("div.modal-body input[type='date']");
    private final By modalSubmitBtn      = By.cssSelector("button.btn-modal-primary");

    // ── Filter panel ──────────────────────────────────────────────────────────
    private final By filterPanelSelects  = By.cssSelector("select.form-select");

    // ── EDIT modal form fields ─────────────────────────────────────────────────
    // Service Line in EDIT = disabled text input (locked after creation per FRD FR-017)
    private final By editSLLocked        = By.cssSelector("input.form-control-disabled");
    // Edit-only fields exposed for EditDeleteCohortTest
    private final By editBatchOwnerDd    = By.name("batchOwner");
    private final By editTrainerDd       = By.name("trainer");
    private final By updateCohortBtn     = By.cssSelector("button.btn-modal-primary");
    private final By disabledModalInputs = By.cssSelector(".modal-body input[disabled]");

    // Empty state
    private final By emptyState          = By.xpath(
            "//*[contains(normalize-space(),'No data found') " +
            "or contains(normalize-space(),'No records')]");

    // Page heading (FRD 2.2)
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h4]" +
            "[contains(text(),'Cohort Management')]");

    public CohortManagementPage(WebDriver driver) {
        super(driver);
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    public boolean isTableVisible() {
        return isDisplayed(dataTable);
    }

    /** Alias for isTableVisible() — kept for backward compatibility with EmployeeTest. */
    public boolean isGridLoaded() {
        return isTableVisible();
    }

    public List<WebElement> getTableHeaders() {
        try {
            return driver.findElements(tableHeaders);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<WebElement> getTableRows() {
        try {
            return driver.findElements(tableRows);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Returns action buttons/icons from the last cell of the first data row. */
    public List<WebElement> getFirstRowActionElements() {
        try {
            List<WebElement> rows = getTableRows();
            if (rows.isEmpty()) return Collections.emptyList();
            WebElement lastCell = rows.get(0).findElement(By.cssSelector("td:last-child"));
            List<WebElement> actions = lastCell.findElements(
                    By.cssSelector("button, a, [class*='action'], [class*='icon']"));
            return actions;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        return isDisplayed(searchInput);
    }

    /**
     * Types a keyword into the search bar and waits for results to appear.
     *
     * How it works:
     *   1. Clears the field via JavaScript so Angular's change-detection fires.
     *   2. Types the keyword character-by-character using sendKeys.
     *   3. Waits 1.5 seconds — Angular's debounceTime(~300 ms) + network round-trip.
     *      Searching can return fewer OR more rows than the original list
     *      (cohorts are added/removed by other users in real time), so callers
     *      must NOT rely on an exact row count — only on "at least 1 row returned".
     */
    public void searchByKeyword(String keyword) {
        try {
            WebElement input = waitForVisible(searchInput);

            // Step 1: clear the field via JS so Angular detects the change
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value='';" +
                    "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
                    input);
            Thread.sleep(300); // short pause before typing

            // Step 2: type the keyword
            if (!keyword.isEmpty()) {
                input.sendKeys(keyword);
            }

            // Step 3: wait for Angular debounce + API response
            // 1 500 ms gives Angular time to debounce and fetch filtered results.
            Thread.sleep(1500);
        } catch (Exception ignored) {}
    }

    /**
     * Clears the search box and waits for the table to show rows again.
     *
     * Why we don't compare with a stored "rowsBefore" count:
     *   Cohorts can be added or deleted by other users while the test is running.
     *   The restored count may legitimately differ from the original count, so
     *   demanding rows >= rowsBefore would cause a false timeout failure.
     *   We only verify that the table is not empty after clearing.
     */
    public void clearSearch() {
        try {
            WebElement input = waitForVisible(searchInput);

            // Clear via JS + fire Angular's input event
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value='';" +
                    "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
                    input);

            // Wait for Angular debounce + API to reload all rows
            Thread.sleep(1500);

            // Wait until at least one row appears in the table.
            // Use the broad tableRows selector (not the stricter tableRowsAligned)
            // so we catch rows regardless of whether they have the align-middle class.
            try {
                wait.until(d -> !d.findElements(tableRows).isEmpty());
            } catch (Exception ignored) {
                // If still empty after the wait, the caller's soft-assert will catch it
            }
        } catch (Exception ignored) {}
    }

    /**
     * Returns the total-record count from the "span.text-muted > b" label.
     * Returns -1 if the label cannot be parsed.
     */
    public int getTotalRecordCount() {
        try {
            List<WebElement> els = driver.findElements(totalRecordsLabel);
            if (els.isEmpty()) return -1;
            return Integer.parseInt(els.get(0).getText().trim());
        } catch (Exception e) { return -1; }
    }

    // ── Create Cohort ─────────────────────────────────────────────────────────

    public boolean isCreateCohortButtonVisible() {
        return isDisplayed(createCohortBtn);
    }

    public void clickCreateCohort() {
        // If a previous modal's overlay is still fading out it intercepts normal clicks.
        // Wait for it to disappear first, then fall back to a JS click if needed.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.modal-overlay")));
        } catch (Exception ignored) {}

        try {
            click(createCohortBtn);
        } catch (Exception e) {
            jsClick(driver.findElement(createCohortBtn));
        }
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(emptyState);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    public WebElement getPageHeadingElement() {
        return waitForVisible(pageHeading);
    }

    // ── Page title / subtitle (FRD 2.2 specific CSS) ─────────────────────────

    public boolean isPageTitleVisible() {
        return isDisplayed(pageTitle);
    }

    public String getPageTitleText() {
        return getText(pageTitle);
    }

    public boolean isPageSubtitleVisible() {
        return isDisplayed(pageSubtitle);
    }

    // ── Record count labels ───────────────────────────────────────────────────

    public boolean isTotalRecordsLabelVisible() {
        return isDisplayed(totalRecordsLabel);
    }

    public String getTotalRecordsText() {
        try { return getText(totalRecordsLabel); } catch (Exception e) { return ""; }
    }

    public boolean isLoadedCountLabelVisible() {
        return isDisplayed(loadedCountLabel);
    }

    // ── Specific table (Bootstrap styled) ────────────────────────────────────

    public boolean isSpecificTableVisible() {
        return isDisplayed(specificTable);
    }

    public boolean isTableHeaderLightVisible() {
        return isDisplayed(tableHeaderLight);
    }

    public List<WebElement> getAlignedRows() {
        try { return driver.findElements(tableRowsAligned); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Cohort ID spans & status badges ──────────────────────────────────────

    public List<WebElement> getCohortIdSpans() {
        try { return driver.findElements(cohortIdSpan); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getStatusBadges() {
        try { return driver.findElements(statusBadge); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // Status badges in table rows (scoped)
    public List<WebElement> getStatusBadgesInTable() {
        List<WebElement> result = driver.findElements(By.cssSelector(
                "table tbody tr td [class*='badge'], " +
                "table tbody tr td [class*='status'], " +
                "table tbody tr td .chip, " +
                "table tbody tr td .tag"));
        if (result.isEmpty()) {
            result = driver.findElements(By.cssSelector("table tbody tr td:nth-child(4)"));
        }
        return result;
    }

    // ── Filters button & select ───────────────────────────────────────────────

    public boolean isFiltersBtnVisible() {
        return isDisplayed(filtersBtn);
    }

    public WebElement getFiltersBtnElement() {
        return driver.findElement(filtersBtn);
    }

    /** Clicks the Filters toggle button (opens or closes the inline filter panel). */
    public void clickFiltersBtn() {
        click(filtersBtn);
    }

    /**
     * Returns all native {@code <select class="form-select">} elements in the
     * filter panel. Call after {@link #clickFiltersBtn()} and waiting for them to appear.
     */
    public List<WebElement> getFilterPanelDropdowns() {
        try { return driver.findElements(filterPanelSelects); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean isFilterSelectVisible() {
        return isDisplayed(filterSelect);
    }

    public WebElement getFilterSelectElement() {
        return driver.findElement(filterSelect);
    }

    // ── Primary create button (Bootstrap) ────────────────────────────────────

    public WebElement getCreateBtnPrimaryElement() {
        return driver.findElement(createBtnPrimary);
    }

    // ── Edit / Delete action buttons ─────────────────────────────────────────

    public List<WebElement> getEditButtons() {
        try { return driver.findElements(editBtn); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getDeleteButtons() {
        try { return driver.findElements(deleteBtn); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Modal card (Bootstrap modal) ─────────────────────────────────────────

    public boolean isModalCardVisible() {
        return isDisplayed(modalCard);
    }

    public WebElement getModalCardElement() {
        return driver.findElement(modalCard);
    }

    public boolean isModalTitleVisible() {
        return isDisplayed(modalTitle);
    }

    public String getModalTitleText() {
        try { return getText(modalTitle); } catch (Exception e) { return ""; }
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    /** Closes any open modal — tries Cancel/Close button first, falls back to JS Escape.
     *  Waits for the overlay to fully disappear before returning so callers are not
     *  surprised by ElementClickInterceptedException from a fading Angular transition. */
    public void closeModal() {
        try {
            List<WebElement> closeBtns = driver.findElements(By.xpath(
                    "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Cancel') " +
                    "or contains(normalize-space(),'Close') " +
                    "or contains(normalize-space(),'×')]"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
            } else {
                // Fallback — dispatch Escape key via JavaScript
                ((JavascriptExecutor) driver).executeScript(
                        "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
            }
        } catch (Exception ignored) {}

        // Wait for the Angular CSS transition to complete — the overlay must be gone
        // before the next click, otherwise ElementClickInterceptedException fires.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.modal-overlay")));
        } catch (Exception ignored) {}
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }

    // ── Additional element getters ────────────────────────────────────────────

    public WebElement getPageTitleElement()    { return driver.findElement(pageTitle); }
    public WebElement getPageSubtitleElement() { return driver.findElement(pageSubtitle); }
    public WebElement getSearchInputElement()  { return driver.findElement(searchInput); }

    public List<WebElement> getFilterSelectElements() {
        return driver.findElements(filterSelect);
    }

    public WebElement getSpecificTableElement()  { return driver.findElement(specificTable); }

    public String getTableHeaderText() {
        try { return getText(tableHeaderLight); } catch (Exception e) { return ""; }
    }

    public String getLoadedCountText() {
        try { return getText(loadedCountLabel); } catch (Exception e) { return ""; }
    }

    public WebElement getTotalRecordsElement() { return driver.findElement(totalRecordsLabel); }
    public WebElement getLoadedCountElement()  { return driver.findElement(loadedCountLabel); }

    // ── Modal element getters ─────────────────────────────────────────────────

    public WebElement getCancelBtnElement()    { return driver.findElement(cancelBtn); }

    // ── CREATE modal form element getters ─────────────────────────────────────

    public WebElement getCreateServiceLineDropdown()    { return driver.findElement(createServiceLineDd); }
    public WebElement getLearningPathDropdown()         { return driver.findElement(learningPathDd); }
    public WebElement getBatchOwnerDropdown()           { return driver.findElement(batchOwnerDd); }
    public WebElement getTrainerDropdown()              { return driver.findElement(trainerDd); }
    public List<WebElement> getDateInputs()             { return driver.findElements(dateInputsByType); }

    // ── EDIT modal element getters ────────────────────────────────────────────

    public WebElement getEditServiceLineLockedElement() { return driver.findElement(editSLLocked); }

    // ── Row-scoped helpers ────────────────────────────────────────────────────

    /** Returns the cohort-ID span element scoped to the given table row. */
    public WebElement getCohortIdSpanInRow(WebElement row)       { return row.findElement(cohortIdSpan); }

    /** Returns all status-badge elements scoped to the given table row. */
    public List<WebElement> getStatusBadgesInRow(WebElement row) { return row.findElements(statusBadge); }

    // ── Waits ─────────────────────────────────────────────────────────────────

    /**
     * Waits for the cohort table to be visible and populated with at least one row.
     * Also pauses briefly to allow Angular data-binding to settle.
     */
    public void waitForTableToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(specificTable));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(tableRowsAligned, 0));
        } catch (Exception ignored) {}
    }

    public void waitForModalVisible()         { wait.until(ExpectedConditions.visibilityOfElementLocated(modalCard)); }
    public void waitForModalInvisible()       { wait.until(ExpectedConditions.invisibilityOfElementLocated(modalCard)); }
    public void waitForEditSLLockedPresent()  { wait.until(ExpectedConditions.presenceOfElementLocated(editSLLocked)); }

    // ── JS-backed actions ─────────────────────────────────────────────────────

    /**
     * JS-clicks the "Create" (primary) button and waits for the modal to appear.
     */
    public void openCreateModal() {
        WebElement btn = driver.findElement(createBtnPrimary);
        jsClick(btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalCard));
    }

    /** JS-clicks the modal Cancel button. */
    public void clickCancelBtn() {
        jsClick(driver.findElement(cancelBtn));
    }

    /** JS-clicks the first Edit button in the Actions column. */
    public void clickFirstEditBtn() {
        List<WebElement> edits = driver.findElements(editBtn);
        if (!edits.isEmpty()) jsClick(edits.get(0));
    }

    /** JS-clicks the first Delete button in the Actions column. */
    public void clickFirstDeleteBtn() {
        List<WebElement> deletes = driver.findElements(deleteBtn);
        if (!deletes.isEmpty()) jsClick(deletes.get(0));
    }

    /** JS-clicks the cohort-ID span inside the given row — navigates to the detail page. */
    public void clickCohortIdSpanInRow(WebElement row) {
        jsClick(row.findElement(cohortIdSpan));
    }

    // ── Edit modal dropdown/button getters ────────────────────────────────────

    public WebElement getEditBatchOwnerDropdown()    { return driver.findElement(editBatchOwnerDd); }
    public WebElement getEditTrainerDropdown()       { return driver.findElement(editTrainerDd); }
    public WebElement getUpdateCohortButton()        { return driver.findElement(updateCohortBtn); }
    public List<WebElement> getDisabledModalInputs() { return driver.findElements(disabledModalInputs); }

    public void selectTrainerByIndex(int index) {
        try {
            new Select(driver.findElement(editTrainerDd))
                    .selectByIndex(index);
        } catch (Exception ignored) {}
    }

    public void clickUpdateCohort() {
        jsClick(driver.findElement(updateCohortBtn));
    }

    public boolean isUpdateCohortButtonPresent() {
        return isDisplayed(updateCohortBtn);
    }

    // ── Create modal — submit ─────────────────────────────────────────────────

    /**
     * Clicks the primary submit button inside the Create / Edit modal.
     * Falls back to a JS click if the button is intercepted.
     */
    public void clickCreateCohortSubmitBtn() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn));
            btn.click();
        } catch (Exception e) {
            try { jsClick(driver.findElement(modalSubmitBtn)); }
            catch (Exception ignored) {}
        }
    }

    // ── Create modal — form fill helpers ──────────────────────────────────────

    /**
     * Selects a visible-text option in a native {@code <select>} inside the modal.
     * Warns (but does not throw) if the option is not found, so downstream
     * assertions report the real failure with context.
     */
    private void selectOptionByText(By selectLocator, String visibleText) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(selectLocator));
            new Select(el).selectByVisibleText(visibleText);
        } catch (Exception e) {
            System.out.println("[CohortPage] WARN – could not select '" + visibleText +
                    "' in " + selectLocator + ": " + e.getMessage());
        }
    }

    /**
     * Selects the first non-placeholder, non-disabled option in a native {@code <select>}.
     * Placeholder options typically have an empty or blank value attribute.
     */
    private void selectFirstOption(By selectLocator) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(selectLocator));
            Select sel = new Select(el);
            for (WebElement opt : sel.getOptions()) {
                String val = opt.getAttribute("value");
                boolean dis = Boolean.parseBoolean(opt.getAttribute("disabled"));
                if (val != null && !val.isBlank() && !dis) {
                    sel.selectByValue(val);
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("[CohortPage] WARN – selectFirstOption failed for " +
                    selectLocator + ": " + e.getMessage());
        }
    }

    /**
     * Sets a date input value via JavaScript so the browser's native date-picker
     * is bypassed (avoids locale/format popup issues).
     *
     * IMPORTANT — format must be "yyyy-MM-dd" (e.g. "2026-06-01").
     * The UI shows dates as "dd-MM-yyyy" but HTML <input type="date"> stores
     * its internal value in ISO format (yyyy-MM-dd). Passing "dd-MM-yyyy"
     * would silently be rejected and the date field would remain empty.
     *
     * @param input   the date {@code <input type="date">} element
     * @param isoDate date string in {@code "yyyy-MM-dd"} format (e.g. "2026-06-01")
     */
    private void setDateInputElement(WebElement input, String isoDate) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];" +
                    "arguments[0].dispatchEvent(new Event('input',  {bubbles:true}));" +
                    "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                    input, isoDate);
        } catch (Exception e) {
            System.out.println("[CohortPage] WARN – setDate failed: " + e.getMessage());
        }
    }

    // ── Create modal — full form fill ─────────────────────────────────────────

    /**
     * Fills all required fields in the Create Cohort modal.
     *
     * <p>Angular loads options asynchronously: selecting a Service Line triggers
     * an API call that populates the Learning Path dropdown; selecting a Learning
     * Path triggers another call that populates Batch Owner. Each step waits up to
     * 30 s for the next dropdown to become available before proceeding.
     *
     * @param serviceLine    visible text of the service line (e.g. "Cloud &amp; Data Enterprise")
     * @param learningPath   visible text of the learning path (e.g. "Generative AI")
     * @param employmentType visible text of the employment type (e.g. "Intern")
     */
    public void fillCreateCohortForm(String serviceLine,
                                     String learningPath,
                                     String employmentType) {
        waitForModalVisible();
        wait.until(ExpectedConditions.presenceOfElementLocated(modalServiceLine));

        // 1. Service Line
        selectOptionByText(modalServiceLine, serviceLine);

        // 2. Wait for Learning Path options to load (Angular async)
        wait.until(d -> {
            try {
                return new Select(d.findElement(modalLearningPath)).getOptions().size() > 1;
            } catch (Exception e) { return false; }
        });

        // 3. Learning Path
        selectOptionByText(modalLearningPath, learningPath);

        // 4. Wait for Batch Owner options to load (Angular async after LP selection)
        wait.until(d -> {
            try {
                return new Select(d.findElement(modalBatchOwner)).getOptions().size() > 1;
            } catch (Exception e) { return false; }
        });

        // 5. Batch Owner — first available (auto-selected; not test-case specific)
        selectFirstOption(modalBatchOwner);

        // 6. Trainer — pre-loaded; scroll into view first (it may be off-screen)
        try {
            WebElement trainerEl = driver.findElement(modalTrainer);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", trainerEl);
            Thread.sleep(200);
        } catch (Exception ignored) {}
        selectFirstOption(modalTrainer);

        // 7. Employment Type
        selectOptionByText(modalEmploymentType, employmentType);
    }

    // ── Create modal — date inputs ────────────────────────────────────────────

    /**
     * Sets the Start Date field via JavaScript.
     * @param isoDate {@code "yyyy-MM-dd"}, e.g. {@code "2026-06-01"}
     */
    public void setStartDate(String isoDate) {
        List<WebElement> dates = driver.findElements(modalDateInputs);
        if (!dates.isEmpty()) setDateInputElement(dates.get(0), isoDate);
    }

    /**
     * Sets the End Date field via JavaScript.
     * @param isoDate {@code "yyyy-MM-dd"}, e.g. {@code "2026-07-01"}
     */
    public void setEndDate(String isoDate) {
        List<WebElement> dates = driver.findElements(modalDateInputs);
        if (!dates.isEmpty()) setDateInputElement(dates.get(dates.size() - 1), isoDate);
    }

    // ── Create modal — service-line learning-path check (TC-016 bug) ─────────

    /**
     * Opens the service line dropdown, selects the given service line, and
     * checks whether the Learning Path dropdown subsequently receives any options.
     *
     * <p>Used by TC-COHORT-016 to detect service lines that exist in the UI but
     * have no associated learning path IDs in the backend.
     *
     * @param serviceLine visible text of the service line to test
     * @return {@code true} if at least one LP option (beyond the placeholder) appeared
     *         within the wait timeout; {@code false} if the dropdown remained empty
     */
    public boolean doesServiceLineHaveLearningPaths(String serviceLine) {
        try {
            waitForModalVisible();
            wait.until(ExpectedConditions.presenceOfElementLocated(modalServiceLine));
            selectOptionByText(modalServiceLine, serviceLine);

            try {
                wait.until(d -> {
                    try {
                        return new Select(d.findElement(modalLearningPath)).getOptions().size() > 1;
                    } catch (Exception e) { return false; }
                });
                return true;  // options appeared — service line is valid
            } catch (Exception e) {
                return false; // timeout — no LP options loaded for this service line
            }
        } catch (Exception e) {
            return false;
        }
    }

    // ── Modal date input inspection ───────────────────────────────────────────

    /** Returns all {@code <input type="date">} elements inside the modal body. */
    public List<WebElement> getModalDateInputs() {
        try { return driver.findElements(modalDateInputs); }
        catch (Exception e) { return Collections.emptyList(); }
    }
}
