package com.infogain.report;

import com.aventstack.extentreports.ExtentTest;
import java.util.Objects;

public final class ExtentReportManager {
  private ExtentReportManager() {}

  private static ThreadLocal<ExtentTest> threadLocal = new ThreadLocal<>();

  protected static ExtentTest getExtent() {
    return threadLocal.get();
  }

  protected static void setExtent(ExtentTest extenttest) {
    if (Objects.nonNull(extenttest)) threadLocal.set(extenttest);
  }

  protected static void unLoad() {
    threadLocal.remove();
  }
}
