package com.example.driver.entity;

import com.example.enums.Browser;
import com.example.enums.BrowserRemoteType;
import com.example.enums.RunMode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "set")
public class WebDriverData {

  private RunMode runMode;
  private BrowserRemoteType browserRemoteType;
  private Browser browser;
}
