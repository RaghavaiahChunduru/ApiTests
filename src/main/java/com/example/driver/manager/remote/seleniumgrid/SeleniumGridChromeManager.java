package com.example.driver.manager.remote.seleniumgrid;

import com.example.enums.Browser;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import static com.example.utils.ConfigUtil.CONFIG;
import java.net.URL;

public final class SeleniumGridChromeManager {

  private SeleniumGridChromeManager() {
  }

  @SneakyThrows
  public static WebDriver getDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setBrowserName(String.valueOf(Browser.CHROME));

    URL seleniumGridURL = new URL(CONFIG.getString("seleniumGridURL"));
    return new RemoteWebDriver(seleniumGridURL, capabilities);
  }
}
