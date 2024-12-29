package com.example.driver.manager.remote.seleniumgrid;

import static com.example.utils.ConfigUtil.CONFIG;

import com.example.enums.Browser;
import java.net.URI;
import java.net.URL;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class SeleniumGridChromeManager {

  private SeleniumGridChromeManager() {}

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(String.valueOf(Browser.CHROME));

    URL seleniumGridURL = URI.create(CONFIG.getString("seleniumGridURL")).toURL();
    return new RemoteWebDriver(seleniumGridURL, capabilities);
  }
}
