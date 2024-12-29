package com.infogain.extensions;

import com.infogain.annotations.FrameworkAnnotaions;
import com.infogain.report.ExtentLogger;
import com.infogain.report.ExtentReport;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.extension.*;

public class ReportingExtension
    implements TestWatcher, BeforeTestExecutionCallback, BeforeAllCallback, AfterAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) {
    ExtentReport.initReports();
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    ExtentReport.createTest(context.getDisplayName());

    context
        .getTestMethod()
        .ifPresent(
            method -> {
              FrameworkAnnotaions annotation = method.getAnnotation(FrameworkAnnotaions.class);
              if (annotation != null) {
                ExtentReport.addAuthors(annotation.Author());
              }
            });
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    ExtentLogger.pass(context.getDisplayName() + " is passed");
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    ExtentLogger.fail(context.getDisplayName() + " is failed");
    ExtentLogger.fail(Optional.ofNullable(cause.getMessage()).orElse("No message available"));
    ExtentLogger.fail(Arrays.toString(cause.getStackTrace()));
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    ExtentLogger.skip(context.getDisplayName() + " was aborted.");
  }

  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    ExtentLogger.skip(
        context.getDisplayName() + " was disabled: " + reason.orElse("No reason provided"));
  }

  @Override
  public void afterAll(ExtensionContext context) {
    ExtentReport.flushReports();
  }
}
