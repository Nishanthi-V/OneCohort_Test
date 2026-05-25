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
        if (ConfigReader.isHeadless()) {
            opts.addArguments("--headless", "--disable-gpu");
        }
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        context.driver = new ChromeDriver(opts);
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