package com.cts.mfrp.onecohort.constants;

public class AppConstants {

    private AppConstants() {}

    // ── Page titles ───────────────────────────────────────────────────────────
    public static final String LOGIN_PAGE_TITLE     = "OneCohort | Login";
    public static final String DASHBOARD_PAGE_TITLE = "OneCohort | Dashboard";

    // ── Role names ────────────────────────────────────────────────────────────
    public static final String ROLE_SUPER_ADMIN  = "Super Admin";
    public static final String ROLE_MANAGER      = "Manager";
    public static final String ROLE_LEADER       = "Leader";
    public static final String ROLE_BATCH_OWNER  = "Batch Owner";
    public static final String ROLE_CR           = "CR";

    // ── Excel column names (LoginData.xlsx and other sheets) ──────────────────
    public static final String COL_ROLE         = "Role";
    public static final String COL_USER_ID      = "UserId";
    public static final String COL_COHORT_ID    = "CohortId";
    public static final String COL_SERVICE_LINE = "ServiceLine";
    public static final String COL_POC_ID       = "PocId";
    public static final String COL_SEARCH_TERM  = "SearchTerm";

    // ── config.properties key names ───────────────────────────────────────────
    public static final String PROP_BASE_URL             = "base.url";
    public static final String PROP_BROWSER              = "browser";
    public static final String PROP_HEADLESS             = "headless";
    public static final String PROP_IMPLICIT_WAIT        = "implicit.wait";
    public static final String PROP_EXPLICIT_WAIT        = "explicit.wait";
    public static final String PROP_PAGE_LOAD_TIMEOUT    = "page.load.timeout";
    public static final String PROP_USERNAME             = "username";
    public static final String PROP_PASSWORD             = "password";
    public static final String PROP_SUPER_ADMIN_USER_ID  = "super.admin.user.id";
    public static final String PROP_LEADER_USER_ID       = "leader.user.id";
    public static final String PROP_MANAGER_USER_ID      = "manager.user.id";
    public static final String PROP_VALID_SERVICE_LINE   = "valid.service.line.id";
    public static final String PROP_VALID_POC_ID         = "valid.poc.id";
    public static final String PROP_VALID_COHORT_ID      = "valid.cohort.id";

    // ── Test data file paths ──────────────────────────────────────────────────
    public static final String TESTDATA_PATH             = "src/test/resources/testdata/LoginData.xlsx";
    public static final String MANAGER_TESTDATA_PATH     = "src/test/resources/testdata/ManagerTestData.xlsx";
    public static final String BATCH_OWNER_TESTDATA_PATH = "src/test/resources/testdata/BatchOwnerTestData.xlsx";
    public static final String COHORT_TESTDATA_PATH      = "src/test/resources/testdata/CohortTestData.xlsx";

    // ── Excel sheet names ─────────────────────────────────────────────────────
    public static final String LOGIN_DATA_SHEET       = "LoginData";
    public static final String MANAGER_DATA_SHEET     = "ManagerTestData";
    public static final String BATCH_OWNER_DATA_SHEET = "BatchOwnerTestData";
    public static final String COHORT_DATA_SHEET      = "CohortTestData";

    // ── Report and screenshot paths ───────────────────────────────────────────
    public static final String REPORT_PATH     = "test-output/reports/ExtentReport.html";
    public static final String SCREENSHOT_PATH = "test-output/screenshots/";

    // ── Timeouts (seconds) ────────────────────────────────────────────────────
    public static final int SHORT_WAIT           = 5;
    public static final int MEDIUM_WAIT          = 10;
    public static final int LONG_WAIT            = 20;
    public static final int NAVIGATION_WAIT      = 15;
    public static final int TABLE_SETTLE_WAIT    = 10;
    public static final int TABLE_SETTLE_FAST    = 5;
    public static final int LOGIN_REDIRECT_WAIT  = 60;

    // ── Assertion counts ──────────────────────────────────────────────────────
    public static final int SYSTEM_CONFIG_CARD_COUNT = 4;
    public static final int MIN_LEADER_NAV_LINKS     = 2;
    public static final int MIN_TABLE_ROWS           = 1;

    // ── Login validation alert messages ──────────────────────────────────────
    public static final String ALERT_EMPTY_USER_ID       = "Please enter a User ID";
    public static final String ALERT_SELECT_SERVICE_LINE = "Please select a Service Line";
    public static final String ALERT_ENTER_POC_ID        = "Please enter a POC ID";
    public static final String ALERT_ENTER_COHORT_ID     = "Please enter a Cohort ID";

    // ── URL path segments ─────────────────────────────────────────────────────
    public static final String URL_SUPER_ADMIN    = "/super-admin";
    public static final String URL_MANAGER        = "/manager/";
    public static final String URL_LEADER         = "/leader/";
    public static final String URL_BATCH_OWNER    = "/batch-owner/";
    public static final String URL_CR             = "/cr/";
    public static final String URL_DASHBOARD      = "/dashboard";
    public static final String URL_MANAGE_COHORTS = "/manage-cohorts";
    public static final String URL_COHORTS        = "/cohorts";
    public static final String URL_SYSTEM_CONFIG  = "system-config";

    // ── KPI card names (Manager / Leader dashboards) ──────────────────────────
    public static final String KPI_SERVICE_LINES       = "Service Lines";
    public static final String KPI_LEARNING_PATHS      = "Learning Paths";
    public static final String KPI_AVG_COMPLETION_RATE = "Avg. Completion Rate";

    // ── Summary card names (Batch Owner dashboard) ────────────────────────────
    public static final String CARD_TOTAL_COHORTS = "Total Cohorts";
    public static final String CARD_ACTIVE        = "Active";
    public static final String CARD_COMPLETED     = "Completed";
    public static final String CARD_UPCOMING      = "Upcoming";

    // ── Evaluation section names (CR dashboard) ───────────────────────────────
    public static final String EVAL_QUALIFIER = "Qualifier";
    public static final String EVAL_INTERIM   = "Interim";
    public static final String EVAL_FINAL     = "Final";

    // ── Sidebar navigation link labels ────────────────────────────────────────
    public static final String NAV_COHORTS   = "Cohorts";
    public static final String NAV_DASHBOARD = "Dashboard";
}
