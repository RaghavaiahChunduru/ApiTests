package com.example.driver;

import com.example.driver.entity.WebDriverData;
import com.example.driver.factory.DriverFactory;
import com.example.enums.Browser;
import com.example.enums.BrowserRemoteType;
import com.example.enums.RunMode;
import org.openqa.selenium.WebDriver;
import java.util.Objects;
import static com.example.utils.ConfigUtil.CONFIG;
import static com.example.driver.DriverManager.getDriver;
import static com.example.driver.DriverManager.setDriver;
import static com.example.driver.DriverManager.unLoad;

public final class Driver {

    private Driver() {
    }

    public static void initDriverForWeb() {
        if (Objects.isNull(getDriver())) {
            WebDriverData webDriverData = WebDriverData.builder()
                    .setBrowserRemoteType(CONFIG.getEnum(BrowserRemoteType.class, "browserRemoteType"))
                    .setBrowser(CONFIG.getEnum(Browser.class, "browser"))
                    .build();

            WebDriver driver = DriverFactory
                    .getDriverForWeb(CONFIG.getEnum(RunMode.class, "runMode"))
                    .getDriver(webDriverData);

            setDriver(driver);
            loadURL();
        }
    }

    private static void loadURL() {
        DriverManager.getDriver().get(CONFIG.getString("URL"));
        DriverManager.getDriver().manage().window().maximize();
    }

    public static void quitDriver() {
        if (Objects.nonNull(getDriver())) {
            getDriver().quit();
            unLoad();
        }
    }
}
