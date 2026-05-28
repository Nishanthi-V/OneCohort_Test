package com.cts.mfrp.onecohort.hooks;

import com.cts.mfrp.onecohort.context.TestContext;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

public class Hooks {

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        // Fresh SoftAssert for every scenario — collects all failures until @After flushes them
        context.softAssert = new SoftAssert();

        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();


        if (ConfigReader.isHeadless()) {
            opts.addArguments("--headless=new");
        }

        context.driver = new ChromeDriver(opts);

        context.driver.manage().window().maximize();
        context.driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
    }

    @After
    public void tearDown(Scenario scenario) {
        // Flush all accumulated soft assertions.
        // Any failures are captured here so ALL assertion messages appear in one report,
        // regardless of which step they were recorded in.
        AssertionError softAssertError = null;
        try {
            context.softAssert.assertAll();
        } catch (AssertionError e) {
            softAssertError = e;
            // Attach the full failure detail as plain text for the Cucumber HTML report
            scenario.attach(e.getMessage().getBytes(),
                    "text/plain", "Soft Assertion Failures");
        }

        // Take screenshot if any step failed OR if soft assertions failed
        if ((scenario.isFailed() || softAssertError != null) && context.driver != null) {
            byte[] screenshot = ((TakesScreenshot) context.driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }

        if (context.driver != null) {
            context.driver.quit();
        }

        // Re-throw so Cucumber marks the scenario as FAILED in its reports
        if (softAssertError != null) {
            throw softAssertError;
        }
    }
}