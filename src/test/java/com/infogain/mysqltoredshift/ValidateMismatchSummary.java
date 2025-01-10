
package com.infogain.mysqltoredshift;

import static com.infogain.utils.StringUtil.loadQueryFromFile;
import static com.infogain.utils.DBUtil.*;
import static com.infogain.constants.FrameWorkConstants.*;
import static com.infogain.utils.JsonUtil.writeFromJsonToMap;

import com.machinezoo.noexception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ValidateMismatchSummary {

    public static void main(String[] args) {
        Exceptions.wrap(e -> new RuntimeException("Error during assertion process", e))
                .run(() -> {
                    try (Connection redshiftConnection = createRedshiftConnection()) {
                        Map<String, Integer> latestMismatches = fetchLatestMismatches(redshiftConnection);
                        Map<String, Integer> oldMismatches = writeFromJsonToMap(getMismatchesJsonPath());

                        assertMismatches(latestMismatches, oldMismatches);
                    }
                });
    }

    private static Map<String, Integer> fetchLatestMismatches(Connection redshiftConnection) {
        String query = loadQueryFromFile(getMismatchesQueryPath());
        Map<String, Integer> mismatchMap = new HashMap<>();

        return Exceptions.wrap(e -> new RuntimeException("Error while fetching mismatch data", e))
                .get(() -> {
                    try (PreparedStatement preparedStatement = redshiftConnection.prepareStatement(query);
                            ResultSet resultSet = preparedStatement.executeQuery()) {

                        while (resultSet.next()) {
                            String mismatchType = resultSet.getString("mismatch_type");
                            int mismatchCount = resultSet.getInt("mismatch_count");

                            mismatchMap.put(mismatchType, mismatchCount);
                        }
                    }
                    return mismatchMap;
                });
    }

    private static void assertMismatches(Map<String, Integer> latestMismatches,
            Map<String, Integer> expectedMismatches) {
        Exceptions.wrap(e -> new RuntimeException("Failed during mismatch assertion", e))
                .run(() -> {
                    List<String> assertionErrors = new ArrayList<>();

                    log.info(String.format("%-40s | %-20s | %-20s",
                            "Mismatch Type", "Expected Difference", "Actual Difference"));
                    log.info("----------------------------------------------------------------------------");

                    for (Map.Entry<String, Integer> entry : latestMismatches.entrySet()) {
                        String mismatchType = entry.getKey();
                        int latestCount = entry.getValue();
                        int expectedCount = expectedMismatches.getOrDefault(mismatchType, -1);

                        int allowedDifference;
                        if (mismatchType.equals("Rows in sales_fact_1 but missing in sales_fact_2")) {
                            allowedDifference = 0;
                        } else if (mismatchType.equals("updated_at mismatch")) {
                            allowedDifference = 8;
                        } else {
                            allowedDifference = 1;
                        }

                        int actualDifference = Math.abs(latestCount - expectedCount);

                        // Log mismatch details in tabular format
                        log.info(String.format("%-40s | %-20d | %-20d",
                                mismatchType, allowedDifference, actualDifference));

                        // Collect assertion errors instead of throwing immediately
                        if (actualDifference != allowedDifference) {
                            assertionErrors.add(String.format(
                                    "Mismatch for type '%s': Expected difference %d but got %d",
                                    mismatchType, allowedDifference, actualDifference));
                        }
                    }

                    // Throw collected errors if any
                    if (!assertionErrors.isEmpty()) {
                        throw new AssertionError("Assertion errors:\n" + String.join("\n", assertionErrors));
                    }
                    log.info("All mismatch counts match the expected differences.");
                });
    }
}
