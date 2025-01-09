package com.infogain.extensions;

import com.infogain.annotations.FrameworkAnnotations;
import com.infogain.report.ExtentLogger;
import com.infogain.report.ExtentReport;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.extension.*;

@Slf4j
public class ReportingExtension
    implements TestWatcher, BeforeTestExecutionCallback, BeforeAllCallback, AfterAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) {
    ExtentReport.initReports();
    log.info("Extent Report Initialized");
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    String testName = context.getTestMethod()
        .map(method -> method.getName())
        .orElse(context.getDisplayName());

    String parameterDetails = context.getDisplayName().contains(" => ")
        ? context.getDisplayName().substring(context.getDisplayName().indexOf(" => ") + 4)
        : "";

    String finalTestName = parameterDetails.isEmpty()
        ? testName
        : testName + " [" + parameterDetails + "]";

    ExtentReport.createTest(finalTestName);
    log.info("Extent test created for: " + finalTestName);

    context.getTestMethod().ifPresentOrElse(
        method -> {
          log.info("Test method found: " + method.getName());

          FrameworkAnnotations annotation = method.getAnnotation(FrameworkAnnotations.class);
          if (Objects.nonNull(annotation)) {
            log.info("FrameworkAnnotations found on method: Author = " + Arrays.toString(annotation.Author()) +
                ", Category = " + annotation.Category() +
                ", Service = " + annotation.Service());
            ExtentReport.addAuthors(Arrays.asList(annotation.Author()));
            ExtentReport.addCategories(Arrays.asList(annotation.Category()));
            ExtentReport.addService(annotation.Service());
          } else {
            log.warn("FrameworkAnnotations not found on method: " + method.getName() + ", checking at class level");

            context.getTestClass().ifPresent(clazz -> {
              FrameworkAnnotations classAnnotation = clazz.getAnnotation(FrameworkAnnotations.class);
              if (Objects.nonNull(classAnnotation)) {
                log.info("FrameworkAnnotations found on class: Author = " + Arrays.toString(classAnnotation.Author()) +
                    ", Category = " + classAnnotation.Category() +
                    ", Service = " + classAnnotation.Service());

                ExtentReport.addAuthors(Arrays.asList(classAnnotation.Author()));
                ExtentReport.addCategories(Arrays.asList(classAnnotation.Category()));
                ExtentReport.addService(classAnnotation.Service());
              } else {
                log.warn("FrameworkAnnotations not found on class: " + clazz.getName());
              }
            });
          }
        },
        () -> log.warn("No test method found in context"));
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    String methodName = context.getTestMethod().map(method -> method.getName()).orElse("Unknown Method");
    String displayName = formatDisplayName(context.getDisplayName());
    ExtentLogger.pass("Method: " + methodName + " | Parameters: " + displayName + " **is passed**.");
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    String methodName = context.getTestMethod().map(method -> method.getName()).orElse("Unknown Method");
    String displayName = formatDisplayName(context.getDisplayName());
    ExtentLogger.fail("Method: " + methodName + " | Parameters: " + displayName + " **is failed**.");
    ExtentLogger.fail("Reason: " + Optional.ofNullable(cause.getMessage()).orElse("No message available"));
    ExtentLogger.fail("Stack Trace: " + Arrays.toString(cause.getStackTrace()));
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    String methodName = context.getTestMethod().map(method -> method.getName()).orElse("Unknown Method");
    String displayName = formatDisplayName(context.getDisplayName());
    ExtentLogger.skip("Method: " + methodName + " | Parameters: " + displayName + " **was aborted**.");
    if (cause != null) {
      ExtentLogger.skip("Reason: " + Optional.ofNullable(cause.getMessage()).orElse("No message available"));
    }
  }

  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    String methodName = context.getTestMethod().map(method -> method.getName()).orElse("Unknown Method");
    String displayName = formatDisplayName(context.getDisplayName());
    ExtentLogger.skip("Method: " + methodName + " | Parameters: " + displayName + " **was disabled**.");
    ExtentLogger.skip("Reason: " + reason.orElse("No reason provided"));
  }

  private String formatDisplayName(String displayName) {
    // Removes leading numeric index (e.g., "9 => ") and leaves parameters unquoted
    return displayName.replaceFirst("^\\d+ => ", "");
  }

  @Override
  public void afterAll(ExtensionContext context) {
    ExtentReport.flushReports();
    log.info("Extent Report Flushed");
  }
}