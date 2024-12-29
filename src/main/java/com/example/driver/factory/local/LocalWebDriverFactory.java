package com.example.driver.factory.local;

import com.example.driver.manager.local.ChromeManager;
import com.example.driver.manager.local.FireFoxManager;
import com.example.enums.Browser;
import org.openqa.selenium.WebDriver;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class LocalWebDriverFactory {

    private LocalWebDriverFactory() {
    }

    private static final Map<Browser, Supplier<WebDriver>> MAP = new EnumMap<>(Browser.class);
    private static final Supplier<WebDriver> CHROME = ChromeManager::getDriver;
    private static final Supplier<WebDriver> FIREFOX = FireFoxManager::getDriver;

    static {
        MAP.put(Browser.CHROME, CHROME);
        MAP.put(Browser.FIREFOX, FIREFOX);
    }

    public static WebDriver getDriver(Browser browser) {
        return MAP.get(browser).get();
    }
}
