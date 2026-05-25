package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    private final By userIdInput         = By.cssSelector("input[placeholder='e.g. 123456']");
    private final By roleDropdown        = By.cssSelector("div.space-y-5 select");
    private final By serviceLineDropdown = By.xpath("/html/body/app-root/app-login/div/div/div[2]/div[3]/select");
    private final By pocIdInput          = By.cssSelector("input[placeholder='e.g. USR-40002']");
    private final By cohortIdInput       = By.cssSelector("input[placeholder='e.g. COH-10001']");
    private final By loginButton         = By.xpath("//button[normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public LoginPage enterUserId(String userId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(userIdInput));
        driver.findElement(userIdInput).clear();
        driver.findElement(userIdInput).sendKeys(userId);
        return this;
    }

    /**
     * FIX: After selecting the role, wait for the service line dropdown
     * to appear — it only renders after a role that requires it is chosen.
     * This prevents selectServiceLine() from failing immediately.
     */
    public LoginPage selectRole(String role) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(roleDropdown));
        new Select(driver.findElement(roleDropdown)).selectByVisibleText(role);

        // If this role requires a service line, wait for the dropdown to appear
        List<String> rolesWithServiceLine = List.of("Manager", "Leader", "Batch Owner");
        if (rolesWithServiceLine.contains(role)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(serviceLineDropdown));
        }
        return this;
    }

    /**
     * FIX: Uses a 30-second WebDriverWait to wait for options to populate.
     * In Cucumber each scenario opens a fresh browser so the page loads
     * from scratch every time — the dropdown needs time to load its options
     * from the backend before selectByVisibleText() is called.
     */
    public LoginPage selectServiceLine(String serviceLineId) {
        // 1. Wait for the dropdown to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(serviceLineDropdown));

        // 2. Wait up to 30 seconds for real options to load (more than just the placeholder)
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(driver -> {
            List<WebElement> options = driver.findElements(
                    By.xpath("/html/body/app-root/app-login/div/div/div[2]/div[3]/select/option"));
            return options.size() > 1;
        });

        Select select = new Select(driver.findElement(serviceLineDropdown));

        // 3. Log available options to help diagnose mismatches
        List<String> availableOptions = select.getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        System.out.println("[LoginPage] Available service line options: " + availableOptions);

        // 4. Check the desired option exists before selecting
        boolean optionExists = availableOptions.stream()
                .anyMatch(opt -> opt.trim().equals(serviceLineId.trim()));

        if (!optionExists) {
            throw new RuntimeException(
                    "[LoginPage] Service line '" + serviceLineId + "' not found in dropdown. " +
                            "Available options: " + availableOptions + ". " +
                            "Please update 'valid.service.line.id' in config.properties.");
        }

        select.selectByVisibleText(serviceLineId);
        return this;
    }

    public LoginPage enterPocId(String pocId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pocIdInput));
        driver.findElement(pocIdInput).clear();
        driver.findElement(pocIdInput).sendKeys(pocId);
        return this;
    }

    public LoginPage enterCohortId(String cohortId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cohortIdInput));
        driver.findElement(cohortIdInput).clear();
        driver.findElement(cohortIdInput).sendKeys(cohortId);
        return this;
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        driver.findElement(loginButton).click();
    }

    public String acceptAlertAndGetMessage() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        return message;
    }

    public boolean isOnLoginPage() {
        return !driver.findElements(userIdInput).isEmpty();
    }

    public boolean isUserIdInputVisible() {
        return !driver.findElements(userIdInput).isEmpty();
    }

    public HomePage loginAsSuperAdmin(String userId) {
        enterUserId(userId);
        selectRole("Super Admin");
        clickLoginButton();
        return new HomePage(driver);
    }

    public void loginAsManager(String userId, String serviceLineId) {
        enterUserId(userId);
        selectRole("Manager");
        selectServiceLine(serviceLineId);
        clickLoginButton();
    }

    public void loginAsLeader(String userId, String serviceLineId) {
        enterUserId(userId);
        selectRole("Leader");
        selectServiceLine(serviceLineId);
        clickLoginButton();
    }

    public void loginAsBatchOwner(String userId, String serviceLineId, String pocId) {
        enterUserId(userId);
        selectRole("Batch Owner");
        selectServiceLine(serviceLineId);
        enterPocId(pocId);
        clickLoginButton();
    }

    public void loginAsCR(String userId, String cohortId) {
        enterUserId(userId);
        selectRole("CR");
        enterCohortId(cohortId);
        clickLoginButton();
    }
}