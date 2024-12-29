package com.infogain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.infogain.annotations.FailingTest;
import com.infogain.annotations.FlakyTest;
import com.infogain.annotations.SmokeTest;
import com.infogain.config.TestConfig;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
public class TestSandBox {
  final Config CONFIG = TestConfig.getInstance().getConfig();

  @SmokeTest
  void assertThatWeCanGetUserConfig() {

    assertThat(CONFIG.getString("TEST_ENV"))
        .as("TEST_ENV should be LOCALHOST")
        .isEqualTo("LOCALHOST");

    assertThat(CONFIG.getString("BOOKING_ENDPOINT"))
        .as("BOOKING_ENDPOINT should be /booking")
        .isEqualTo("/booking");

    assertThat(CONFIG.getString("ADMIN_USERNAME"))
        .as("ADMIN_USERNAME should be admin")
        .isEqualTo("admin");

    assertThat(CONFIG.getString("ADMIN_PASSWORD"))
        .as("ADMIN_PASSWORD should be password123")
        .isEqualTo("password123");

    assertThat(CONFIG.getBoolean("TOGGLE")).as("TOGGLE should be false").isFalse();

    assertThat(CONFIG.getInt("NUM_OF_USERS")).as("NUM_OF_USERS should be 10").isEqualTo(10);

    assertThat(CONFIG.getDouble("PRICE")).as("PRICE should be 123.456").isEqualTo(123.456);
  }

  @SmokeTest
  void assertThatTrueIsTrue() {
    assertTrue(true, "true is true");
  }

  @FailingTest
  void assertThatDayIsADay() {
    assertEquals("day", "night", "day is a  day");
  }

  @FlakyTest
  void createFlakyTest() {
    long currentTimeStamp = System.currentTimeMillis();
    log.info("currentTimeStamp : {}", currentTimeStamp);
    if (currentTimeStamp % 2 == 0) {
      assertTrue(true, "time is even");
    } else {
      assertTrue(false, "time is odd");
    }
  }

  @SmokeTest
  void testLogLevels() {
    // Log at various levels
    log.trace("Testing TRACE level logging");
    log.debug("Testing DEBUG level logging");
    log.info("Testing INFO level logging");
    log.warn("Testing WARN level logging");
    log.error("Testing ERROR level logging");

    // Dynamic logging
    String testValue = "JUnit Test";
    log.info("Testing INFO level with dynamic value: {}", testValue);

    // Assert statements for logging are typically not used, as the purpose is to
    // verify output.
    // Instead, monitor the logs in your console to ensure they appear as expected.
  }

  @SmokeTest
  void assertThatBrowserLaunchedUsingSelenium() {
    ChromeOptions options = new ChromeOptions();
    // Suppress ChromeDriver logs
    options.addArguments("--log-level=3"); // 3 = SEVERE logs only

    System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
    WebDriver driver = new ChromeDriver(options);
    driver.get("https://www.google.com");
    driver.quit();
  }
}
