package com.infogain.driver.entity;

import com.infogain.enums.Browser;
import com.infogain.enums.BrowserRemoteType;
import com.infogain.enums.RunMode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "set")
public class WebDriverData {

  private RunMode runMode;
  private BrowserRemoteType browserRemoteType;
  private Browser browser;
}
