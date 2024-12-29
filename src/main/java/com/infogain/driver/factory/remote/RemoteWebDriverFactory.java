package com.infogain.driver.factory.remote;

import com.infogain.enums.Browser;
import com.infogain.enums.BrowserRemoteType;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.openqa.selenium.WebDriver;

public final class RemoteWebDriverFactory {

  private RemoteWebDriverFactory() {}

  private static final Map<BrowserRemoteType, Function<Browser, WebDriver>> MAP =
      new EnumMap<>(BrowserRemoteType.class);

  private static final Function<Browser, WebDriver> SELENIUM_GRID = SeleniumGridFactory::getDriver;
  private static final Function<Browser, WebDriver> SELENOID = SelenoidFactory::getDriver;
  private static final Function<Browser, WebDriver> BROWSER_STACK = BrowserStackFactory::getDriver;

  static {
    MAP.put(BrowserRemoteType.SELENIUM_GRID, SELENIUM_GRID);
    MAP.put(BrowserRemoteType.SELENOID, SELENOID);
    MAP.put(BrowserRemoteType.BROWSER_STACK, BROWSER_STACK);
  }

  public static WebDriver getDriver(BrowserRemoteType remoteType, Browser browser) {
    return MAP.get(remoteType).apply(browser);
  }
}
