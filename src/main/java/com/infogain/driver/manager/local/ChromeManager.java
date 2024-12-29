package com.infogain.driver.manager.local;

import org.openqa.selenium.chrome.ChromeDriver;

public final class ChromeManager {

  private ChromeManager() {}

  public static ChromeDriver getDriver() {
    return new ChromeDriver();
  }
}
