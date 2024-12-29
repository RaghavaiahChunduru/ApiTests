package com.example.driver;

import com.example.driver.entity.WebDriverData;
import org.openqa.selenium.WebDriver;

public interface IWebDriver {

  WebDriver getDriver(WebDriverData webDriverData);
}
