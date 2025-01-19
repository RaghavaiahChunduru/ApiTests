package com.infogain.constants;

public final class FrameWorkConstants {
  private FrameWorkConstants() {
  };

  private static final String RESOURCE_PATH = System.getProperty("user.dir") + "/src/test/resources";
  private static final String REPORT_PATH = System.getProperty("user.dir") + "/index.html";
  private static final String EXTENT_CONFIG_PATH = REPORT_PATH + "/extent_config.json";
  private static final String MISMATCHES_JSON_PATH = RESOURCE_PATH + "/mismatches.json";
  private static final String MISMATCHES_QUERY_PATH = RESOURCE_PATH + "/mismatches_query.sql";

  public static String getReportPath() {
    return REPORT_PATH;
  }

  public static String getExtentConfigPath() {
    return EXTENT_CONFIG_PATH;
  }

  public static String getMismatchesJsonPath() {
    return MISMATCHES_JSON_PATH;
  }

  public static String getMismatchesQueryPath() {
    return MISMATCHES_QUERY_PATH;
  }
}
