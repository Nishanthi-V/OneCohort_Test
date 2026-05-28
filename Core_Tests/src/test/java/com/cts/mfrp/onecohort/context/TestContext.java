package com.cts.mfrp.onecohort.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

/**
 * Shared context object injected into all step definition classes
 * via PicoContainer dependency injection.
 *
 * Holds the WebDriver instance, a per-scenario {@link SoftAssert} instance,
 * and any state that needs to be shared between step definition classes
 * within a scenario.
 *
 * <p>{@code softAssert} is reset at the start of every scenario by {@code Hooks.setUp()}
 * and flushed (via {@code assertAll()}) in {@code Hooks.tearDown()} so all assertion
 * failures within a scenario are collected and reported together.
 */
public class TestContext {

    public WebDriver driver;

    /** Per-scenario SoftAssert — reset in @Before, flushed in @After. */
    public SoftAssert softAssert;

    public WebDriverWait getWait() {
        return new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }
}