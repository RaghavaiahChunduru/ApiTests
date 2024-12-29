package com.example.driver.impl;

import com.example.driver.IWebDriver;
import com.example.driver.entity.WebDriverData;
import com.example.driver.factory.remote.RemoteWebDriverFactory;
import org.openqa.selenium.WebDriver;

public class RemoteWebDriverImpl implements IWebDriver {

  @Override
  public WebDriver getDriver(WebDriverData webDriverData) {
    return RemoteWebDriverFactory.getDriver(webDriverData.getBrowserRemoteType(), webDriverData.getBrowser());
  }
}
