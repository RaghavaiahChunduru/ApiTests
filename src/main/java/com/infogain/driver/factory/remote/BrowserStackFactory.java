package com.infogain.driver.factory.remote;

import com.infogain.driver.manager.remote.browserstack.BrowserStackChromeManager;
import com.infogain.driver.manager.remote.browserstack.BrowserStackFireFoxManager;
import com.infogain.enums.Browser;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.openqa.selenium.WebDriver;

public final class BrowserStackFactory {

  private BrowserStackFactory() {}

  private static final Map<Browser, Supplier<WebDriver>> MAP = new EnumMap<>(Browser.class);
  private static final Supplier<WebDriver> CHROME = BrowserStackChromeManager::getDriver;
  private static final Supplier<WebDriver> FIREFOX = BrowserStackFireFoxManager::getDriver;

  static {
    MAP.put(Browser.CHROME, CHROME);
    MAP.put(Browser.FIREFOX, FIREFOX);
  }

  public static WebDriver getDriver(Browser browser) {
    return MAP.get(browser).get();
  }
}
