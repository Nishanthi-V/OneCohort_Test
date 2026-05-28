package com.cts.mfrp.onecohort.utils;

import com.cts.mfrp.onecohort.constants.AppConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Apache POI reader for {@code CohortManagementTestData.xlsx}.
 *
 * <p>Loaded once at JVM start via a static initialiser — all public accessors
 * are O(1) list lookups after that. No I/O happens at test runtime.
 *
 * <p>Sheet structure (matches the Excel file at
 * {@code src/test/resources/testdata/CohortManagementTestData.xlsx}):
 *
 * <pre>
 * SearchCohort  — SearchKeyword | Description | ExpectedMinRows
 * FilterStatus  — StatusValue | ShouldExistInDropdown | Notes
 * CreateCohort  — ScenarioId | ScenarioType | ServiceLine | LearningPath |
 *                 EmploymentType | StartDateOffset | EndDateOffset |
 *                 ExpectedOutcome | Notes
 * </pre>
 *
 * ScenarioType values:
 * <ul>
 *   <li>{@code HAPPY_PATH} — all fields valid, expect SUCCESS alert</li>
 *   <li>{@code BUG_DATE}   — end date before start date, should be REJECTED (bug)</li>
 *   <li>{@code BUG_SL}     — service line has no learning paths (bug)</li>
 * </ul>
 */
public final class CohortTestData {

    private CohortTestData() {}

    // ── Inner model classes ───────────────────────────────────────────────────

    public static final class SearchScenario {
        public final String keyword;
        public final String description;
        public final int    expectedMinRows;   // 0 = expect no results

        SearchScenario(String keyword, String description, int expectedMinRows) {
            this.keyword         = keyword;
            this.description     = description;
            this.expectedMinRows = expectedMinRows;
        }

        @Override
        public String toString() {
            return "Search['" + keyword + "', minRows=" + expectedMinRows + "]";
        }
    }

    public static final class FilterStatusRow {
        public final String  statusValue;
        public final boolean shouldExist;   // true when ShouldExistInDropdown == "YES"
        public final String  notes;

        FilterStatusRow(String statusValue, boolean shouldExist, String notes) {
            this.statusValue = statusValue;
            this.shouldExist = shouldExist;
            this.notes       = notes;
        }
    }

    public static final class CreateCohortScenario {
        public final String scenarioId;
        public final String scenarioType;     // HAPPY_PATH | BUG_DATE | BUG_SL
        public final String serviceLine;
        public final String learningPath;
        public final String employmentType;
        public final int    startDateOffset;  // days from today (always positive)
        public final int    endDateOffset;    // days from today (negative = before start = bug)
        public final String expectedOutcome;  // SUCCESS | REJECTED
        public final String notes;

        CreateCohortScenario(String scenarioId, String scenarioType,
                             String serviceLine, String learningPath,
                             String employmentType, int startDateOffset,
                             int endDateOffset, String expectedOutcome,
                             String notes) {
            this.scenarioId      = scenarioId;
            this.scenarioType    = scenarioType;
            this.serviceLine     = serviceLine;
            this.learningPath    = learningPath;
            this.employmentType  = employmentType;
            this.startDateOffset = startDateOffset;
            this.endDateOffset   = endDateOffset;
            this.expectedOutcome = expectedOutcome;
            this.notes           = notes;
        }

        public boolean isHappyPath() { return AppConstants.SCENARIO_HAPPY_PATH.equals(scenarioType); }
        public boolean isBugDate()   { return AppConstants.SCENARIO_BUG_DATE.equals(scenarioType);   }
        public boolean isBugSL()     { return AppConstants.SCENARIO_BUG_SL.equals(scenarioType);     }

        @Override
        public String toString() {
            return scenarioId + "[" + scenarioType + ": " + serviceLine + " / " + learningPath + "]";
        }
    }

    // ── Static cache (populated once at class-load time) ─────────────────────

    private static final List<SearchScenario>       searchScenarios = new ArrayList<>();
    private static final List<FilterStatusRow>      filterStatuses  = new ArrayList<>();
    private static final List<CreateCohortScenario> createScenarios = new ArrayList<>();

    static {
        String path = AppConstants.COHORT_MGMT_TESTDATA_CLASSPATH;
        try (InputStream is = CohortTestData.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException(
                        "CohortTestData: cannot find '" + path + "' on the classpath. " +
                        "Place CohortManagementTestData.xlsx under " +
                        "src/test/resources/testdata/");
            }
            try (Workbook wb = new XSSFWorkbook(is)) {
                loadSearchCohort(wb.getSheet(AppConstants.SHEET_SEARCH_COHORT));
                loadFilterStatus(wb.getSheet(AppConstants.SHEET_FILTER_STATUS));
                loadCreateCohort(wb.getSheet(AppConstants.SHEET_CREATE_COHORT));
            }
        } catch (IOException e) {
            throw new RuntimeException("CohortTestData: failed to load " + path, e);
        }
    }

    // ── Sheet loaders ─────────────────────────────────────────────────────────

    private static void loadSearchCohort(Sheet sheet) {
        requireSheet(sheet, AppConstants.SHEET_SEARCH_COHORT);
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isBlankRow(row)) continue;
            searchScenarios.add(new SearchScenario(
                    str(row, 0),        // SearchKeyword
                    str(row, 1),        // Description
                    (int) num(row, 2)   // ExpectedMinRows
            ));
        }
    }

    private static void loadFilterStatus(Sheet sheet) {
        requireSheet(sheet, AppConstants.SHEET_FILTER_STATUS);
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isBlankRow(row)) continue;
            filterStatuses.add(new FilterStatusRow(
                    str(row, 0),                                    // StatusValue
                    "YES".equalsIgnoreCase(str(row, 1)),            // ShouldExistInDropdown
                    str(row, 2)                                     // Notes
            ));
        }
    }

    private static void loadCreateCohort(Sheet sheet) {
        requireSheet(sheet, AppConstants.SHEET_CREATE_COHORT);
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isBlankRow(row)) continue;
            createScenarios.add(new CreateCohortScenario(
                    str(row, 0),        // ScenarioId
                    str(row, 1),        // ScenarioType
                    str(row, 2),        // ServiceLine
                    str(row, 3),        // LearningPath
                    str(row, 4),        // EmploymentType
                    (int) num(row, 5),  // StartDateOffset
                    (int) num(row, 6),  // EndDateOffset
                    str(row, 7),        // ExpectedOutcome
                    str(row, 8)         // Notes
            ));
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** All search scenarios (positive and negative). */
    public static List<SearchScenario> getAllSearchScenarios() {
        return Collections.unmodifiableList(searchScenarios);
    }

    /** Scenarios where ExpectedMinRows {@literal >} 0 (should return results). */
    public static List<SearchScenario> getPositiveSearchScenarios() {
        List<SearchScenario> out = new ArrayList<>();
        for (SearchScenario s : searchScenarios)
            if (s.expectedMinRows > 0) out.add(s);
        return out;
    }

    /** Scenarios where ExpectedMinRows == 0 (no results expected). */
    public static List<SearchScenario> getNegativeSearchScenarios() {
        List<SearchScenario> out = new ArrayList<>();
        for (SearchScenario s : searchScenarios)
            if (s.expectedMinRows == 0) out.add(s);
        return out;
    }

    /** All filter status rows. */
    public static List<FilterStatusRow> getFilterStatuses() {
        return Collections.unmodifiableList(filterStatuses);
    }

    /** Status values expected to appear in the filter dropdown (ShouldExistInDropdown=YES). */
    public static List<String> getExpectedFilterStatusValues() {
        List<String> out = new ArrayList<>();
        for (FilterStatusRow r : filterStatuses)
            if (r.shouldExist) out.add(r.statusValue);
        return out;
    }

    /** All create-cohort scenarios. */
    public static List<CreateCohortScenario> getAllCreateScenarios() {
        return Collections.unmodifiableList(createScenarios);
    }

    /** Only HAPPY_PATH scenarios. */
    public static List<CreateCohortScenario> getHappyPathScenarios() {
        List<CreateCohortScenario> out = new ArrayList<>();
        for (CreateCohortScenario s : createScenarios)
            if (s.isHappyPath()) out.add(s);
        return out;
    }

    /** Only BUG_DATE scenarios (end date before start). */
    public static List<CreateCohortScenario> getBugDateScenarios() {
        List<CreateCohortScenario> out = new ArrayList<>();
        for (CreateCohortScenario s : createScenarios)
            if (s.isBugDate()) out.add(s);
        return out;
    }

    /** Only BUG_SL scenarios (service lines with no learning paths). */
    public static List<CreateCohortScenario> getBugServiceLineScenarios() {
        List<CreateCohortScenario> out = new ArrayList<>();
        for (CreateCohortScenario s : createScenarios)
            if (s.isBugSL()) out.add(s);
        return out;
    }

    /**
     * Returns the first HAPPY_PATH scenario — used as a single valid data set
     * when a test needs ALL fields correct (e.g. TC-015 which only varies the dates).
     *
     * @throws RuntimeException if no HAPPY_PATH row exists in the CreateCohort sheet
     */
    public static CreateCohortScenario getDefaultHappyPathScenario() {
        return getHappyPathScenarios().stream().findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "CohortTestData: no HAPPY_PATH row found in sheet '" +
                        AppConstants.SHEET_CREATE_COHORT + "'"));
    }

    // ── Apache POI cell helpers ───────────────────────────────────────────────

    private static String str(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (Exception ignored) {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            default: return "";
        }
    }

    private static double num(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try { return Double.parseDouble(str(row, col)); } catch (Exception e) { return 0; }
    }

    private static boolean isBlankRow(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            if (!str(row, c).isEmpty()) return false;
        }
        return true;
    }

    private static void requireSheet(Sheet sheet, String name) {
        if (sheet == null) throw new RuntimeException(
                "CohortTestData: sheet '" + name + "' not found in " +
                AppConstants.COHORT_MGMT_TESTDATA_CLASSPATH);
    }
}
