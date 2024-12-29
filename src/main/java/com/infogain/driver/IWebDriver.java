package com.infogain.driver;

import com.infogain.driver.entity.WebDriverData;
import org.openqa.selenium.WebDriver;

public interface IWebDriver {

  WebDriver getDriver(WebDriverData webDriverData);
}
