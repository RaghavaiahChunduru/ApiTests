package com.example.driver.factory;

import com.example.driver.IWebDriver;
import com.example.driver.impl.LocalWebDriverImpl;
import com.example.driver.impl.RemoteWebDriverImpl;
import com.example.enums.RunMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DriverFactory {

  private DriverFactory() {
  }

  private static final Map<RunMode, Supplier<IWebDriver>> WEB = new EnumMap<>(RunMode.class);

  static {
    WEB.put(RunMode.LOCAL, LocalWebDriverImpl::new);
    WEB.put(RunMode.REMOTE, RemoteWebDriverImpl::new);
  }

  public static IWebDriver getDriverForWeb(RunMode runMode) {

    return WEB.get(runMode).get();
  }
}
