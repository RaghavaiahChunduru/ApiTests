package com.example.driver.impl;

import com.example.driver.IWebDriver;
import com.example.driver.entity.WebDriverData;
import com.example.driver.factory.local.LocalWebDriverFactory;
import org.openqa.selenium.WebDriver;

public class LocalWebDriverImpl implements IWebDriver {

  @Override
  public WebDriver getDriver(WebDriverData webDriverData) {
    return LocalWebDriverFactory.getDriver(webDriverData.getBrowser());
  }
}
