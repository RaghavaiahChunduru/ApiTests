
package com.infogain.mysqltoredshift;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static com.infogain.constants.FrameWorkConstants.getMismatchesJsonPath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateMismatchSummary {

    private static final String redshiftDbUrl = CONFIG.getString("REDSHIFT_DB_URL");
    private static final String redshiftUserName = CONFIG.getString("REDSHIFT_DB_USERNAME");
    private static final String redshiftPassword = CONFIG.getString("REDSHIFT_DB_PASSWORD");

    public static void main(String[] args) {
        Exceptions.wrap(e -> new RuntimeException("Error during assertion process", e))
                .run(() -> {
                    try (Connection redshiftConnection = createRedshiftConnection()) {
                        Map<String, Integer> latestMismatches = fetchLatestMismatches(redshiftConnection);
                        Map<String, Integer> oldMismatches = loadOldMismatches();

                        assertMismatches(latestMismatches, oldMismatches);
                    }
                });
    }

    private static Connection createRedshiftConnection() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to connect to Redshift database", e))
                .get(() -> DriverManager.getConnection(redshiftDbUrl, redshiftUserName, redshiftPassword));
    }

    private static Map<String, Integer> fetchLatestMismatches(Connection redshiftConnection) throws Exception {
        String query = "" +
                "SELECT " +
                "    'Rows in sales_fact_1 but missing in sales_fact_2' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "LEFT JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s2.sales_id IS NULL " +
                "UNION ALL " +
                "SELECT " +
                "    'Rows in sales_fact_2 but missing in sales_fact_1' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_2 s2 " +
                "LEFT JOIN sales_fact_1 s1 ON s2.sales_id = s1.sales_id " +
                "WHERE s1.sales_id IS NULL " +
                "UNION ALL " +
                "SELECT " +
                "    'product_id mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.product_id <> s2.product_id " +
                "UNION ALL " +
                "SELECT " +
                "    'customer_id mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.customer_id <> s2.customer_id " +
                "UNION ALL " +
                "SELECT " +
                "    'sales_date mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.sales_date <> s2.sales_date " +
                "UNION ALL " +
                "SELECT " +
                "    'quantity mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.quantity <> s2.quantity " +
                "UNION ALL " +
                "SELECT " +
                "    'price_per_unit mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.price_per_unit <> s2.price_per_unit " +
                "UNION ALL " +
                "SELECT " +
                "    'total_amount mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.total_amount <> s2.total_amount " +
                "UNION ALL " +
                "SELECT " +
                "    'created_at mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.created_at <> s2.created_at " +
                "UNION ALL " +
                "SELECT " +
                "    'updated_at mismatch' AS mismatch_type, COUNT(*) AS mismatch_count " +
                "FROM sales_fact_1 s1 " +
                "JOIN sales_fact_2 s2 ON s1.sales_id = s2.sales_id " +
                "WHERE s1.updated_at <> s2.updated_at;";

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

    private static Map<String, Integer> loadOldMismatches() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to load mismatches from JSON file", e))
                .get(() -> {
                    Path filePath = Path.of(getMismatchesJsonPath());
                    ObjectMapper objectMapper = new ObjectMapper();

                    if (!Files.exists(filePath)) {
                        throw new RuntimeException("JSON file not found at " + filePath.toAbsolutePath());
                    }

                    return objectMapper.readValue(filePath.toFile(), new TypeReference<Map<String, Integer>>() {
                    });
                });
    }

    private static void assertMismatches(Map<String, Integer> latestMismatches,
            Map<String, Integer> expectedMismatches) {
        Exceptions.wrap(e -> new RuntimeException("Failed during mismatch assertion", e))
                .run(() -> {
                    List<String> assertionErrors = new ArrayList<>();

                    System.out.printf("%-40s | %-20s | %-20s%n",
                            "Mismatch Type", "Expected Difference", "Actual Difference");
                    System.out.println("----------------------------------------------------------------------------");

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

                        // Print mismatch details in tabular format
                        System.out.printf("%-40s | %-20d | %-20d%n",
                                mismatchType, allowedDifference, actualDifference);

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

                    System.out.println("\nAll mismatch counts match the expected differences.");
                });
    }
}
