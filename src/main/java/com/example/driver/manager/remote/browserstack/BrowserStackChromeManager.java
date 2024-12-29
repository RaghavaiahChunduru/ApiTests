package com.example.driver.manager.remote.browserstack;

import static com.example.utils.ConfigUtil.CONFIG;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import lombok.SneakyThrows;

public final class BrowserStackChromeManager {

  private BrowserStackChromeManager() {
  }

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();

    capabilities.setCapability("browser", "chrome");
    capabilities.setCapability("browser_version", "latest");
    capabilities.setCapability("os", "Windows");
    capabilities.setCapability("os_version", "10");

    URL browserStackUrl = new URL(CONFIG.getString("browserStackURL"));
    return new RemoteWebDriver(browserStackUrl, capabilities);
  }
}
