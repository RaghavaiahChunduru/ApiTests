package com.infogain.asserts;

import static com.infogain.utils.ConfigUtil.CONFIG;
import com.infogain.api.usermanagement.User;
import com.machinezoo.noexception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public final class ValidateDB {

    private ValidateDB() {
    }

    private static final ValidateDB instance = new ValidateDB();

    public static ValidateDB getInstance() {
        return instance;
    }

    private Connection connection;

    private static final String DB_URL = CONFIG.getString("DB_URL");
    private static final String DB_USERNAME = CONFIG.getString("DB_USERNAME");
    private static final String DB_PASSWORD = CONFIG.getString("DB_PASSWORD");

    public ValidateDB connectToDatabase() {
        Exceptions.wrap(e -> new RuntimeException("Failed to establish a connection to the database", e))
                .run(() -> {
                    connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    log.info("Successfully connected to the database.");
                });
        return this;
    }

    public ValidateDB validateUserInDatabase(User expectedUser, Long userId) {
        String query = "SELECT * FROM user WHERE id = ?";
        validateNonNullAndNonEmpty(userId, "User ID");

        Exceptions.wrap(e -> new RuntimeException("Failed to validate user in the database", e))
                .run(() -> {
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setLong(1, userId);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                Assertions.assertThat(resultSet.getLong("id")).isEqualTo(userId);
                                Assertions.assertThat(resultSet.getString("username"))
                                        .isEqualTo(expectedUser.getUserName());
                                Assertions.assertThat(resultSet.getString("email")).isEqualTo(expectedUser.getEmail());
                                Assertions.assertThat(resultSet.getString("phone")).isEqualTo(expectedUser.getPhone());
                                Assertions.assertThat(resultSet.getInt("role_id")).isEqualTo(expectedUser.getRoleId());

                                log.info("User validation successful for userId: {}", userId);
                            } else {
                                throw new RuntimeException("No user found in the database with id: " + userId);
                            }
                        }
                    }
                });
        return this;
    }

    public ValidateDB validateUserNotInDatabase(Long userId) {
        validateNonNullAndNonEmpty(userId, "User ID");

        String query = "SELECT * FROM user WHERE id = ?";
        Exceptions.wrap(e -> new RuntimeException("Failed to validate absence of user in the database", e))
                .run(() -> {
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setLong(1, userId);

                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                throw new RuntimeException("User with id " + userId + " still exists in the database.");
                            } else {
                                log.info("User with id {} is successfully deleted from the database.", userId);
                            }
                        }
                    }
                });
        return this;
    }

    public void closeConnection() {
        Exceptions.wrap(e -> new RuntimeException("Failed to close the database connection", e))
                .run(() -> {
                    if (connection != null) {
                        connection.close();
                        log.info("Database connection closed successfully.");
                    }
                });
    }

    private void validateNonNullAndNonEmpty(Object value, String fieldName) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }
}