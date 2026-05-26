package com.cts.mfrp.onecohort.utils;

import com.cts.mfrp.onecohort.constants.AppConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads login / credentials test data from LoginData.xlsx (Apache POI) and
 * exposes role-based lookup helpers used by step definitions and test classes.
 *
 * Expected sheet columns: Role | UserId | CohortId | ServiceLine | PocId | SearchTerm
 *
 * This is the single source of truth for all test input data.
 * No credentials or test data should be hardcoded anywhere else.
 */
public final class TestDataProvider {

    /** Map of role name → row data (all column values keyed by header). */
    private static final Map<String, Map<String, String>> data = new HashMap<>();

    static {
        try {
            List<Map<String, String>> rows =
                    ExcelUtils.readSheet(AppConstants.TESTDATA_PATH, AppConstants.LOGIN_DATA_SHEET);
            for (Map<String, String> row : rows) {
                String role = row.getOrDefault(AppConstants.COL_ROLE,
                              row.getOrDefault(AppConstants.COL_ROLE.toLowerCase(), "")).trim();
                if (!role.isEmpty()) {
                    data.put(role, row);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Failed to load test data from Excel: " + AppConstants.TESTDATA_PATH
                    + " — " + e.getMessage(), e);
        }
    }

    private TestDataProvider() {}

    /**
     * Generic lookup: returns the cell value for {@code columnName} in the row
     * whose Role column equals {@code role}.  Performs a case-insensitive column
     * name fallback so headers are forgiving of capitalisation differences.
     */
    public static String getValue(String role, String columnName) {
        Map<String, String> row = data.get(role);
        if (row == null)
            throw new RuntimeException("No test data found for role: '" + role + "'");
        String v = row.get(columnName);
        if (v == null) {
            for (Map.Entry<String, String> e : row.entrySet()) {
                if (e.getKey().equalsIgnoreCase(columnName)) return e.getValue();
            }
            throw new RuntimeException(
                    "Column '" + columnName + "' not found for role: '" + role + "'");
        }
        return v.trim();
    }

    // ── Convenience accessors (all driven by AppConstants column names) ────────

    public static String getUserIdForRole(String role) {
        return getValue(role, AppConstants.COL_USER_ID);
    }

    public static String getCohortIdForRole(String role) {
        return getValue(role, AppConstants.COL_COHORT_ID);
    }

    public static String getServiceLineForRole(String role) {
        return getValue(role, AppConstants.COL_SERVICE_LINE);
    }

    public static String getPocIdForRole(String role) {
        return getValue(role, AppConstants.COL_POC_ID);
    }

    public static String getSearchTermForRole(String role) {
        return getValue(role, AppConstants.COL_SEARCH_TERM);
    }
}
