package com.infogain.driver.manager.local;

import org.openqa.selenium.firefox.FirefoxDriver;

public final class FireFoxManager {

  private FireFoxManager() {}

  public static FirefoxDriver getDriver() {
    return new FirefoxDriver();
  }
}
