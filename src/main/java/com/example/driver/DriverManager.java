package com.example.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private DriverManager() {
    }

    private static final ThreadLocal<WebDriver> WEB_DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return WEB_DRIVER.get();
    }

    public static void setDriver(WebDriver driver) {
        WEB_DRIVER.set(driver);
    }

    public static void unLoad() {
        WEB_DRIVER.remove();
    }
}
