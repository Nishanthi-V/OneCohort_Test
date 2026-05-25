package com.cts.mfrp.onecohort.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.cts.mfrp.onecohort.utils.ConfigReader;

import java.time.Duration;

/**
 * Shared context object injected into all step definition classes
 * via PicoContainer dependency injection.
 *
 * Holds the WebDriver instance and any state that needs to be
 * shared between step definition classes within a scenario.
 */
public class TestContext {

    public WebDriver driver;

    public WebDriverWait getWait() {
        return new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }
}