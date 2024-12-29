package com.example.report;

import java.util.Objects;
import com.aventstack.extentreports.ExtentTest;

public final class ExtentReportManager {
    private ExtentReportManager() {
    }

    private static ThreadLocal<ExtentTest> threadLocal = new ThreadLocal<>();

    protected static ExtentTest getExtent() {
        return threadLocal.get();
    }

    protected static void setExtent(ExtentTest extenttest) {
        if (Objects.nonNull(extenttest))
            threadLocal.set(extenttest);
    }

    protected static void unLoad() {
        threadLocal.remove();
    }
}
