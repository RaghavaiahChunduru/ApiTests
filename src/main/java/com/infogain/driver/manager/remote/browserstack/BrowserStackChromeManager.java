package com.infogain.driver.manager.remote.browserstack;

import static com.infogain.utils.ConfigUtil.CONFIG;

import java.net.URI;
import java.net.URL;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class BrowserStackChromeManager {

  private BrowserStackChromeManager() {}

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();

    capabilities.setCapability("browser", "chrome");
    capabilities.setCapability("browser_version", "latest");
    capabilities.setCapability("os", "Windows");
    capabilities.setCapability("os_version", "10");

    URL browserStackUrl = URI.create(CONFIG.getString("browserStackURL")).toURL();
    return new RemoteWebDriver(browserStackUrl, capabilities);
  }
}
