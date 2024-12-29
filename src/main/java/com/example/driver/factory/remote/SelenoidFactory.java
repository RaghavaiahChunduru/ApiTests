package com.example.driver.factory.remote;

import com.example.driver.manager.remote.selenoid.SelenoidChromeManager;
import com.example.driver.manager.remote.selenoid.SelenoidFireFoxManager;
import com.example.enums.Browser;
import org.openqa.selenium.WebDriver;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SelenoidFactory {

  private SelenoidFactory() {
  }

  private static final Map<Browser, Supplier<WebDriver>> MAP = new EnumMap<>(Browser.class);
  private static final Supplier<WebDriver> CHROME = SelenoidChromeManager::getDriver;
  private static final Supplier<WebDriver> FIREFOX = SelenoidFireFoxManager::getDriver;

  static {
    MAP.put(Browser.CHROME, CHROME);
    MAP.put(Browser.FIREFOX, FIREFOX);
  }

  public static WebDriver getDriver(Browser browser) {
    return MAP.get(browser).get();
  }
}
