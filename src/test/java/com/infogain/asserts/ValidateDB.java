package com.infogain.asserts;

import static com.infogain.extensions.HikariCPExtension.getDataSource;
import static com.infogain.utils.ConfigUtil.CONFIG;
import com.infogain.api.arithmetic.ValidArithmeticResponse;
import com.infogain.api.usermanagement.UserResponse;
import com.machinezoo.noexception.Exceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  public ValidateDB validateUserInDatabase(UserResponse expectedUser, Long userId) {
    String query = "SELECT * FROM user WHERE id = ?";
    validateNonNullAndNonEmpty(userId, "User ID");

    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> Exceptions.wrap(e -> new RuntimeException("Failed to validate user in the database", e))
            .run(() -> {
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
              }
            }));

    return this;
  }

  public ValidateDB validateUserNotInDatabase(Long userId) {
    validateNonNullAndNonEmpty(userId, "User ID");

    String query = "SELECT * FROM user WHERE id = ?";
    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(
            () -> Exceptions.wrap(e -> new RuntimeException("Failed to validate absence of user in the database", e))
                .run(() -> {
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
                  }
                }));

    return this;
  }

  public ValidateDB validateEntryInArithmeticOperationTable(ValidArithmeticResponse expectedEntry, Integer id) {
    validateNonNullAndNonEmpty(id, "ID");

    String query = "SELECT * FROM arithmetic_operation WHERE id = ?";
    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> Exceptions
            .wrap(e -> new RuntimeException("Failed to validate arithmetic operation entry in the database", e))
            .run(() -> {
              try (Connection connection = getDataSource().getConnection();
                  PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                  if (resultSet.next()) {
                    validateResultSet(resultSet, expectedEntry, id);
                    log.info("Arithmetic operation entry validated successfully for id: {}", id);
                  } else {
                    throw new RuntimeException("No arithmetic operation entry found in the database with id: " + id);
                  }
                }
              }
            }));

    return this;
  }

  public ValidateDB validateEntryInEventNotificationTable(ValidArithmeticResponse expectedEntry, Integer id) {
    String expectedMessage = String.format(
        "ArithmeticOperation(id=%d, firstOperand=%s, secondOperand=%s, operatorId=%d, result=%s)",
        expectedEntry.getId(),
        expectedEntry.getFirstOperand(),
        expectedEntry.getSecondOperand(),
        expectedEntry.getOperatorId(),
        expectedEntry.getResult());

    String query = "SELECT message FROM wss_wep_mysqldb.event_notification WHERE data_id = ? and type='arithmetic-operation'";
    validateNonNullAndNonEmpty(id, "ID");

    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> Exceptions
            .wrap(e -> new RuntimeException("Failed to validate event notification message in the database", e))
            .run(() -> {
              try (Connection connection = getDataSource().getConnection();
                  PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                  if (resultSet.next()) {
                    String actualMessage = resultSet.getString("message");

                    if (actualMessage == null || !actualMessage.equals(expectedMessage)) {
                      throw new RuntimeException("Message mismatch for event notification with id: " + id
                          + ". Expected: " + expectedMessage + ", Found: " + actualMessage);
                    }
                    log.info("Event notification message validated successfully for id: {}", id);
                  } else {
                    throw new RuntimeException("No event notification found in the database with id: " + id);
                  }
                }
              }
            }));

    return this;
  }

  public ValidateDB validateEntryInEventNotificationTable(UserResponse expectedUser, Long id) {
    String expectedMessage = String.format(
        "User(id=%d, username=%s, password=%s, email=%s, phone=%s, roleId=%d)",
        expectedUser.getId(),
        expectedUser.getUserName(),
        expectedUser.getPassword(),
        expectedUser.getEmail(),
        expectedUser.getPhone(),
        expectedUser.getRoleId());

    String query = "SELECT * FROM wss_wep_mysqldb.event_notification " +
        "WHERE data_id = ? AND type = 'user-management' " +
        "ORDER BY created_date DESC LIMIT 1;";

    validateNonNullAndNonEmpty(id, "ID");

    Awaitility.await()
        .atMost(AWAITILITY_TIMEOUT, TimeUnit.SECONDS)
        .pollInterval(AWAITILITY_POLL_INTERVAL, TimeUnit.SECONDS)
        .pollDelay(AWAITILITY_POLL_DELAY, TimeUnit.SECONDS)
        .untilAsserted(() -> Exceptions
            .wrap(e -> new RuntimeException("Failed to validate event notification message in the database", e))
            .run(() -> {
              try (Connection connection = getDataSource().getConnection();
                  PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                  if (resultSet.next()) {
                    String actualMessage = resultSet.getString("message");
                    if (actualMessage == null || !actualMessage.equals(expectedMessage)) {
                      throw new RuntimeException("Message mismatch for event notification with id: " + id
                          + ". Expected: " + expectedMessage + ", Found: " + actualMessage);
                    }
                    log.info("Event notification message validated successfully for id: {}", id);
                  } else {
                    throw new RuntimeException("No event notification found in the database with id: " + id);
                  }
                }
              }
            }));

    return this;
  }

  private void validateResultSet(ResultSet resultSet, ValidArithmeticResponse expectedEntry, Integer id)
      throws SQLException {
    Assertions.assertThat(resultSet)
        .as("Validating arithmetic operation details in the database for id: %d", id)
        .satisfies(rs -> {
          Assertions.assertThat(rs.getInt("id"))
              .as("ID mismatch")
              .withFailMessage("Expected ID '%d', but found '%d'", id, rs.getInt("id"))
              .isEqualTo(id);

          Assertions.assertThat(rs.getDouble("first_operand"))
              .as("First Operand mismatch")
              .withFailMessage("Expected firstOperand '%s', but found '%s'",
                  expectedEntry.getFirstOperand(), rs.getDouble("first_operand"))
              .isEqualTo(expectedEntry.getFirstOperand());

          if (expectedEntry.getSecondOperand() == null && rs.getObject("second_Operand") == null) {
            log.info("Both expected and actual secondOperand are null. Assertion passed.");
          } else if (expectedEntry.getSecondOperand() != null && rs.getObject("second_Operand") != null) {
            Assertions.assertThat(rs.getDouble("second_Operand"))
                .as("Second Operand mismatch")
                .withFailMessage(
                    "Expected secondOperand '%s', but found '%s'",
                    expectedEntry.getSecondOperand(),
                    rs.getDouble("second_Operand"))
                .isEqualTo(expectedEntry.getSecondOperand());
          }

          Assertions.assertThat(rs.getInt("operator_id"))
              .as("Operator ID mismatch")
              .withFailMessage("Expected operatorId '%d', but found '%d'",
                  expectedEntry.getOperatorId(), rs.getInt("operator_id"))
              .isEqualTo(expectedEntry.getOperatorId());

          Assertions.assertThat(rs.getDouble("result"))
              .as("Result mismatch")
              .withFailMessage("Expected result '%s', but found '%s'", expectedEntry.getResult(),
                  rs.getDouble("result"))
              .satisfies(r -> {
                if (r == Double.POSITIVE_INFINITY) {
                  Assertions.assertThat(r)
                      .withFailMessage("Result is not positive infinity as expected")
                      .isEqualTo(Double.POSITIVE_INFINITY);
                } else if (r == Double.NEGATIVE_INFINITY) {
                  Assertions.assertThat(r)
                      .withFailMessage("Result is not negative infinity as expected")
                      .isEqualTo(Double.NEGATIVE_INFINITY);
                } else {
                  Assertions.assertThat(r)
                      .withFailMessage("Result mismatch for normal number")
                      .isEqualTo(expectedEntry.getResult());
                }
              });
        });
  }

  private void validateResultSet(ResultSet resultSet, UserResponse expectedUser, Long userId) {
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

  private void validateNonNullAndNonEmpty(Object value, String fieldName) {
    if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }
}
