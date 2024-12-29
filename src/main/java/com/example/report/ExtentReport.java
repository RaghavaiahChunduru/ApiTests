package com.example.report;

import java.util.Objects;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import static com.example.constants.FrameWorkConstants.*;

public final class ExtentReport {
    private ExtentReport() {
    }

    private static ExtentReports extentreports;
    private static ExtentTest extenttest;

    public static void initReports() {
        if (Objects.isNull(extentreports)) {
            extentreports = new ExtentReports();
            ExtentSparkReporter spark = new ExtentSparkReporter(getReportPath());

            //after changing extent to latest version uncomment below
            // Load configuration from JSON file
            // File configJson = new File("extent_config.json");
            // try {
            // spark.loadJSONConfig(configJson);
            // } catch (IOException e) {
            // throw new RuntimeException("Failed to load Extent Report configuration from JSON file", e);

            // }
            extentreports.attachReporter(spark);

            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Automation Report");
            spark.config().setReportName("Regression Report");
        }
    }

    public static void flushReports() {
        if (Objects.nonNull(extentreports)) {
            extentreports.flush();
        }
        ExtentReportManager.unLoad();
    }

    public static void createTest(String testcasename) {
        extenttest = extentreports.createTest(testcasename);
        ExtentReportManager.setExtent(extenttest);
    }

    public static void addAuthors(String author) {
        ExtentReportManager.getExtent().assignAuthor(author);
    }
}