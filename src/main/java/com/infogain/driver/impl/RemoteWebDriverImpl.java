package com.infogain.driver.impl;

import com.infogain.driver.IWebDriver;
import com.infogain.driver.entity.WebDriverData;
import com.infogain.driver.factory.remote.RemoteWebDriverFactory;
import org.openqa.selenium.WebDriver;

public class RemoteWebDriverImpl implements IWebDriver {

  @Override
  public WebDriver getDriver(WebDriverData webDriverData) {
    return RemoteWebDriverFactory.getDriver(
        webDriverData.getBrowserRemoteType(), webDriverData.getBrowser());
  }
}
