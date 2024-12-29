package com.example.driver.manager.remote.browserstack;

import static com.example.utils.ConfigUtil.CONFIG;

import java.net.URI;
import java.net.URL;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BrowserStackFireFoxManager {

  private BrowserStackFireFoxManager() {}

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();

    capabilities.setCapability("browser", "firefox");
    capabilities.setCapability("browser_version", "latest");
    capabilities.setCapability("os", "Windows");
    capabilities.setCapability("os_version", "10");

    URL browserStackUrl = URI.create(CONFIG.getString("browserStackURL")).toURL();
    return new RemoteWebDriver(browserStackUrl, capabilities);
  }
}
