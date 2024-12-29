package com.example.constants;

public final class FrameWorkConstants {
    private FrameWorkConstants() {
    };

    @SuppressWarnings("unused")
    private final static String RESOURCE_PATH = System.getProperty("user.dir")+"/src/test/resources";
    private static final String REPORT_PATH = System.getProperty("user.dir") + "/index.html";
    private static final String EXTENT_CONFIG_PATH = REPORT_PATH + "/extent_config.json";

    public static String getReportPath() {
        return REPORT_PATH;
    }

    public static String getExtentConfigPath() {
        return EXTENT_CONFIG_PATH;
    }
}