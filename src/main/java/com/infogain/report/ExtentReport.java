package com.infogain.report;

import static com.infogain.constants.FrameWorkConstants.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.infogain.enums.Author;
import com.infogain.enums.Category;
import com.infogain.enums.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class ExtentReport {
  private ExtentReport() {
  }

  private static ExtentReports extentreports;
  private static ExtentTest extenttest;

  public static void initReports() {
    if (Objects.isNull(extentreports)) {
      extentreports = new ExtentReports();
      ExtentSparkReporter spark = new ExtentSparkReporter(getReportPath());

      // after changing extent to latest version uncomment below
      // Load configuration from JSON file
      // File configJson = new File("extent_config.json");
      // try {
      // spark.loadJSONConfig(configJson);
      // } catch (IOException e) {
      // throw new RuntimeException("Failed to load Extent Report configuration from
      // JSON file", e);

      // }
      extentreports.attachReporter(spark);

      spark.config().setTheme(Theme.STANDARD);
      spark.config().setDocumentTitle("Automation Report");
      spark.config().setReportName("ApiTests Report");
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

  public static void addAuthors(List<Author> authors) {
    for (Author author : authors) {
      ExtentReportManager.getExtent().assignAuthor(author.toString());
    }
  }

  public static void addCategories(List<Category> categories) {
    for (Category category : categories) {
      ExtentReportManager.getExtent().assignCategory(category.toString());
    }
  }

  public static void addService(Service service) {
    ExtentReportManager.getExtent().assignCategory(service.toString());
  }
}
