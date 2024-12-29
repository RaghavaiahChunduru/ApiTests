package com.example.driver.manager.remote.selenoid;

import static com.example.utils.ConfigUtil.CONFIG;

import java.net.URI;
import java.net.URL;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class SelenoidFireFoxManager {

  private SelenoidFireFoxManager() {}

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();

    capabilities.setCapability("browserName", "firefox");
    capabilities.setCapability("browserVersion", "120");
    capabilities.setCapability("enableVNC", true);
    capabilities.setCapability("enableVideo", false);

    URL selenoidURL = URI.create(CONFIG.getString("selenoidURL")).toURL();
    return new RemoteWebDriver(selenoidURL, capabilities);
  }
}
