package com.infogain.asserts;

import static com.infogain.extensions.HikariCPExtension.getDataSource;
import static com.infogain.utils.ConfigUtil.CONFIG;

import com.infogain.api.usermanagement.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;

@Slf4j
public final class ValidateDB {

  private ValidateDB() {
  }

  private static final ValidateDB instance = new ValidateDB();

  public static ValidateDB getInstance() {
    return instance;
  }

  private static final int AWAITILITY_TIMEOUT = CONFIG.getInt("AWAITILITY_TIMEOUT_IN_SECONDS");
  private static final int AWAITILITY_POLL_INTERVAL = CONFIG.getInt("AWAITILITY_POLL_INTERVAL_IN_SECONDS");
  private static final int AWAITILITY_POLL_DELAY = CONFIG.getInt("AWAITILITY_POLL_DELAY_IN_SECONDS");

  public ValidateDB validateUserInDatabase(User expectedUser, Long userId) {
    String query = "SELECT * FROM user WHERE id = ?";
    validateNonNullAndNonEmpty(userId, "User ID");

    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          try (Connection connection = getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
              if (resultSet.next()) {
                validateResultSet(resultSet, expectedUser, userId);
                log.info("User validation successful for userId: {}", userId);
              } else {
                throw new RuntimeException("No user found in the database with id: " + userId);
              }
            }
          } catch (Exception e) {
            throw new RuntimeException("Failed to validate user in the database", e);
          }
        });

    return this;
  }

  private void validateResultSet(ResultSet resultSet, User expectedUser, Long userId) {
    Assertions.assertThat(resultSet)
        .as("Validating user details in the database for userId: %d", userId)
        .satisfies(rs -> {
          Assertions.assertThat(rs.getLong("id"))
              .as("User ID mismatch")
              .withFailMessage("Expected ID '%d', but found '%d'", userId, rs.getLong("id"))
              .isEqualTo(userId);

          Assertions.assertThat(rs.getString("username"))
              .as("Username mismatch")
              .withFailMessage("Expected username '%s', but found '%s'", expectedUser.getUserName(),
                  rs.getString("username"))
              .isEqualTo(expectedUser.getUserName());

          Assertions.assertThat(rs.getString("email"))
              .as("Email mismatch")
              .withFailMessage("Expected email '%s', but found '%s'", expectedUser.getEmail(), rs.getString("email"))
              .isEqualTo(expectedUser.getEmail());

          Assertions.assertThat(rs.getString("phone"))
              .as("Phone number mismatch")
              .withFailMessage("Expected phone '%s', but found '%s'", expectedUser.getPhone(), rs.getString("phone"))
              .isEqualTo(expectedUser.getPhone());

          Assertions.assertThat(rs.getInt("role_id"))
              .as("Role ID mismatch")
              .withFailMessage("Expected role ID '%d', but found '%d'", expectedUser.getRoleId(), rs.getInt("role_id"))
              .isEqualTo(expectedUser.getRoleId());
        });
  }

  public ValidateDB validateUserNotInDatabase(Long userId) {
    validateNonNullAndNonEmpty(userId, "User ID");

    String query = "SELECT * FROM user WHERE id = ?";
    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          try (Connection connection = getDataSource().getConnection();
              PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
              if (resultSet.next()) {
                throw new RuntimeException("User with id " + userId + " still exists in the database.");
              } else {
                log.info("User with id {} is successfully deleted from the database.", userId);
              }
            }
          } catch (Exception e) {
            throw new RuntimeException("Failed to validate absence of user in the database", e);
          }
        });

    return this;
  }

  private void validateNonNullAndNonEmpty(Object value, String fieldName) {
    if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }
}
