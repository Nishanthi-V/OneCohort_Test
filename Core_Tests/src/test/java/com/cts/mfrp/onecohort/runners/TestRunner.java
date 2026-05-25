package com.cts.mfrp.onecohort.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Cucumber TestNG Runner for OneCohort BDD test suite.
 *
 * Run full suite : mvn test
 * Run by tag     : mvn test -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.cts.mfrp.onecohort.stepdefinitions",
                "com.cts.mfrp.onecohort.hooks"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
}