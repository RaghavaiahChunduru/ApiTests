package com.infogain.driver.impl;

import com.infogain.driver.IWebDriver;
import com.infogain.driver.entity.WebDriverData;
import com.infogain.driver.factory.local.LocalWebDriverFactory;
import org.openqa.selenium.WebDriver;

public class LocalWebDriverImpl implements IWebDriver {

  @Override
  public WebDriver getDriver(WebDriverData webDriverData) {
    return LocalWebDriverFactory.getDriver(webDriverData.getBrowser());
  }
}
