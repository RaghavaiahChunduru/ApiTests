package com.infogain.driver;

import static com.infogain.driver.DriverManager.getDriver;
import static com.infogain.driver.DriverManager.setDriver;
import static com.infogain.driver.DriverManager.unLoad;
import static com.infogain.utils.ConfigUtil.CONFIG;

import com.infogain.driver.entity.WebDriverData;
import com.infogain.driver.factory.DriverFactory;
import com.infogain.enums.Browser;
import com.infogain.enums.BrowserRemoteType;
import com.infogain.enums.RunMode;
import java.util.Objects;
import org.openqa.selenium.WebDriver;

public final class Driver {

  private Driver() {}

  public static void initDriverForWeb() {
    if (Objects.isNull(getDriver())) {
      WebDriverData webDriverData =
          WebDriverData.builder()
              .setBrowserRemoteType(CONFIG.getEnum(BrowserRemoteType.class, "browserRemoteType"))
              .setBrowser(CONFIG.getEnum(Browser.class, "browser"))
              .build();

      WebDriver driver =
          DriverFactory.getDriverForWeb(CONFIG.getEnum(RunMode.class, "runMode"))
              .getDriver(webDriverData);

      setDriver(driver);
      loadURL();
    }
  }

  private static void loadURL() {
    DriverManager.getDriver().get(CONFIG.getString("URL"));
    DriverManager.getDriver().manage().window().maximize();
  }

  public static void quitDriver() {
    if (Objects.nonNull(getDriver())) {
      getDriver().quit();
      unLoad();
    }
  }
}
