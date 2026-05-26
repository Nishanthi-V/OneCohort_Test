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

import java.time.Duration;

public class Hooks {

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();

        // Always set an explicit window size so the layout is identical in
        // both headed and headless modes (maximise() is a no-op in headless).
        opts.addArguments("--window-size=1920,1080");
        opts.addArguments("--no-sandbox");
        opts.addArguments("--disable-dev-shm-usage");   // prevents shared-memory crashes
        opts.addArguments("--disable-extensions");
        opts.addArguments("--disable-infobars");

        if (ConfigReader.isHeadless()) {
            // "--headless=new" (Chrome 112+) renders identically to headed mode,
            // unlike the legacy "--headless" flag which had different viewport
            // behaviour and caused dropdown overlays to block subsequent clicks.
            opts.addArguments("--headless=new");
        }

        context.driver = new ChromeDriver(opts);

        // maximize() works in headed mode; in headless the --window-size arg above
        // already guarantees 1920x1080 — calling maximize() is harmless either way.
        context.driver.manage().window().maximize();
        context.driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
    }

    @After
    public void tearDown(Scenario scenario) {
        // Take screenshot on failure
        if (scenario.isFailed() && context.driver != null) {
            byte[] screenshot = ((TakesScreenshot) context.driver)
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }
        if (context.driver != null) {
            context.driver.quit();
        }
    }
}