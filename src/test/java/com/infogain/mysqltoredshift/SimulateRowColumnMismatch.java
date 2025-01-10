package com.infogain.mysqltoredshift;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static com.infogain.constants.FrameWorkConstants.getMismatchesJsonPath;
import static java.util.concurrent.TimeUnit.DAYS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.machinezoo.noexception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class SimulateRowColumnMismatch {

    private static final String redshiftDbUrl = CONFIG.getString("REDSHIFT_DB_URL");
    private static final String redshiftUserName = CONFIG.getString("REDSHIFT_DB_USERNAME");
    private static final String redshiftPassword = CONFIG.getString("REDSHIFT_DB_PASSWORD");

    private static final String mysqlDbUrl = CONFIG.getString("MYSQL_DB_URL");
    private static final String mysqlUserName = CONFIG.getString("MYSQL_DB_USERNAME");
    private static final String mysqlPassword = CONFIG.getString("MYSQL_DB_PASSWORD");

    private static final Faker faker = new Faker();

    public static void main(String[] args) {
        Exceptions.wrap(e -> new RuntimeException("Failed during database operations", e))
                .run(() -> {
                    try (Connection redshiftConnection = createRedshiftConnection();
                            Connection mysqlConnection = createMySQLConnection()) {

                        fetchCurrentRowAndColumnMismatches(redshiftConnection);

                        int maxSalesId = getMaxSalesId(mysqlConnection);
                        int newSalesId = maxSalesId + 1;
                        String insertQuery = generateInsertQuery(newSalesId);

                        Exceptions.wrap(ex -> new RuntimeException("Failed to insert record", ex))
                                .run(() -> insertNewRecordToSimulateRowCountMismatch(mysqlConnection, insertQuery,
                                        newSalesId));

                        Exceptions.wrap(ex -> new RuntimeException("Failed to update records", ex))
                                .run(() -> updateExistingRecordsToSimulateColumnValueMismatches(mysqlConnection));
                    }
                });
    }

    private static Connection createRedshiftConnection() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to connect to Redshift database", e))
                .get(() -> DriverManager.getConnection(redshiftDbUrl, redshiftUserName, redshiftPassword));
    }

    private static Connection createMySQLConnection() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to connect to MySQL database", e))
                .get(() -> DriverManager.getConnection(mysqlDbUrl, mysqlUserName, mysqlPassword));
    }

    private static void fetchCurrentRowAndColumnMismatches(Connection redshiftConnection) {
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

        Exceptions.wrap(e -> new RuntimeException("Error fetching mismatches from Redshift", e))
                .run(() -> {
                    try (PreparedStatement preparedStatement = redshiftConnection.prepareStatement(query);
                            ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            String mismatchType = resultSet.getString("mismatch_type");
                            int mismatchCount = resultSet.getInt("mismatch_count");

                            mismatchMap.put(mismatchType, mismatchCount);
                        }
                    }
                });
        writeMismatchesToJsonFile(mismatchMap);
    }

    private static void writeMismatchesToJsonFile(Map<String, Integer> mismatches) {
        ObjectMapper objectMapper = new ObjectMapper();
        Exceptions.wrap(e -> new RuntimeException("Error writing mismatch data to JSON file", e))
                .run(() -> {
                    Path filePath = Paths.get(getMismatchesJsonPath());
                    Files.createDirectories(filePath.getParent());
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), mismatches);
                    log.info("Mismatch data has been written to {}", filePath.toAbsolutePath());
                });
    }

    private static int getMaxSalesId(Connection mysqlConnection) {
        String query = "SELECT COALESCE(MAX(sales_id), 0) AS max_sales_id FROM sales_fact_2;";
        return Exceptions.wrap(e -> new RuntimeException("Failed to fetch max sales_id from MySQL", e))
                .get(() -> {
                    try (PreparedStatement preparedStatement = mysqlConnection.prepareStatement(query);
                            ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt("max_sales_id");
                        }
                    }
                    return 0;
                });
    }

    private static String generateInsertQuery(int newSalesId) {

        int productId = faker.number().numberBetween(1, 1000);
        int customerId = faker.number().numberBetween(1, 1000);
        String salesDate = formatDate(faker.date().past(365, DAYS));
        double quantity = faker.number().randomDouble(2, 1, 100);
        double pricePerUnit = faker.number().randomDouble(2, 1, 500);
        double totalAmount = quantity * pricePerUnit;
        String createdAt = formatDateTime(faker.date().past(365, DAYS));
        String updatedAt = formatDateTime(faker.date().past(365, DAYS));

        return String.format("INSERT INTO sales_fact_2 " +
                "(sales_id, product_id, customer_id, sales_date, quantity, price_per_unit, total_amount, created_at, updated_at) "
                +
                "VALUES (%d, %d, %d, '%s', %.2f, %.2f, %.2f, '%s', '%s');",
                newSalesId, productId, customerId, salesDate, quantity, pricePerUnit, totalAmount, createdAt,
                updatedAt);
    }

    private static void insertNewRecordToSimulateRowCountMismatch(Connection mysqlConnection, String query,
            int newSalesId) {
        Exceptions.wrap(e -> new RuntimeException("Failed to insert new record into MySQL", e))
                .run(() -> {
                    try (PreparedStatement preparedStatement = mysqlConnection.prepareStatement(query)) {
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            log.info("New record inserted successfully with sales_id: {}", newSalesId);
                        }
                    }
                });
    }

    private static void updateExistingRecordsToSimulateColumnValueMismatches(Connection mysqlConnection) {
        String[] updateQueries = {
                generateUpdateQuery("product_id", faker.number().numberBetween(1, 1000)),
                generateUpdateQuery("customer_id", faker.number().numberBetween(1, 1000)),
                generateUpdateQuery("sales_date", formatDate(faker.date().past(1000, DAYS))),
                generateUpdateQuery("quantity", faker.number().randomDouble(2, 1, 500)),
                generateUpdateQuery("price_per_unit", faker.number().randomDouble(2, 1, 1000)),
                generateUpdateQuery("total_amount", faker.number().randomDouble(2, 100, 5000)),
                generateUpdateQuery("created_at", formatDateTime(faker.date().past(365, DAYS))),
                generateUpdateQuery("updated_at", formatDateTime(faker.date().past(100, DAYS)))
        };

        Exceptions.wrap(e -> new RuntimeException("Error updating records, transaction rolled back.", e))
                .run(() -> {
                    // Start a transaction
                    mysqlConnection.setAutoCommit(false);
                    try (Statement statement = mysqlConnection.createStatement()) {
                        for (String query : updateQueries) {
                            statement.addBatch(query);
                        }
                        statement.executeBatch();
                        // Commit the transaction
                        mysqlConnection.commit();
                        log.info("All records updated successfully.");
                    } catch (SQLException e) {
                        // Rollback in case of an error
                        Exceptions.wrap(rollbackEx -> new RuntimeException("Error during rollback", rollbackEx))
                                .run(mysqlConnection::rollback);
                        throw e; // Rethrow to handle in the outer wrap
                    } finally {
                        // Restore auto-commit mode
                        mysqlConnection.setAutoCommit(true);
                    }
                });
    }

    private static String generateUpdateQuery(String columnName, Object value) {
        int salesId = faker.number().numberBetween(100, 1000000);
        String formattedValue = value instanceof String ? "'" + value + "'" : value.toString();
        return String.format(
                "UPDATE sales_fact_2 SET %s=%s WHERE sales_id=%d;",
                columnName, formattedValue, salesId);
    }

    private static String formatDate(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private static String formatDateTime(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
