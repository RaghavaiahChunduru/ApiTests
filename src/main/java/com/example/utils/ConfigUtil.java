package com.example.utils;

import com.example.config.TestConfig;
import com.typesafe.config.Config;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static final Config CONFIG = TestConfig.getInstance().getConfig();
}
