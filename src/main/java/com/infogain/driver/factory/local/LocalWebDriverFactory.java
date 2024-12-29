package com.infogain.driver.factory.local;

import com.infogain.driver.manager.local.ChromeManager;
import com.infogain.driver.manager.local.FireFoxManager;
import com.infogain.enums.Browser;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.openqa.selenium.WebDriver;

public final class LocalWebDriverFactory {

  private LocalWebDriverFactory() {}

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
