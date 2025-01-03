package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.*;

import com.infogain.api.usermanagement.User;
import com.infogain.api.usermanagement.UserManagementAPI;
import com.infogain.api.usermanagement.UserResponse;
import com.infogain.asserts.ValidateDB;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class UserManagementTests {

  private static final String CREATE_USER_SCHEMA_FILE_PATH = "schemas/create-user-schema.json";
  private static final String READ_UPDATE_USER_SCHEMA_FILE_PATH = "schemas/read-update-user-schema.json";

  private ThreadLocal<Long> userId = new ThreadLocal<>();
  private ThreadLocal<User> user = new ThreadLocal<>();

  private final ValidateDB dbValidator = ValidateDB.getInstance();

  @BeforeEach
  public void setup() {

    // Arrange: Create a new user instance
    User newUser = User.getInstance();
    user.set(newUser);

    // Act: Send a POST request to create a new user
    Response response = UserManagementAPI.getInstance().newUser(newUser);

    // Assert API Response
    VerifyUserResponse.assertThat(response, VerifyUserResponse.class)
        .statusCodeIs(200)
        .responseTimeBelow(2000)
        .containsValue("id")
        .doesNotContains("error")
        .matchesSchema(CREATE_USER_SCHEMA_FILE_PATH)
        .assertAll();

    // Extract the ID from the response
    userId.set(response.jsonPath().getLong("id"));

    log.info("User created successfully with ID: {}", userId.get());
  }

  @AfterEach
  public void tearDown() {

    if (userId.get() != null) {
      // Act
      Response response = UserManagementAPI.getInstance().deleteUser(userId.get());

      // Assert API Response
      VerifyUserResponse.assertThat(response, VerifyUserResponse.class)
          .statusCodeIs(SC_NO_CONTENT)
          .responseTimeBelow(2000)
          .assertAll();

      // Assert Database Entry
      dbValidator.validateUserNotInDatabase(userId.get());

      log.info("User with ID: {} deleted successfully", userId.get());

      userId.remove();
      user.remove();
    }
  }

  @Test
  void assertThatAdminCanGetAnExistingUser() {

    // Act
    Response response = UserManagementAPI.getInstance().getUser(userId.get());
    UserResponse expectedUser = JsonUtil.deserialize(response.asString(), UserResponse.class);

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyUserResponse.assertThat(response, VerifyUserResponse.class)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateUserInDatabase(expectedUser, userId.get());
  }

  @Test
  void assertThatAdminCanUpdateAnExistingUser() {
    // Arrange
    User updatedUser = user.get().setUsername("UpdatedUserName");

    // Act
    Response response = UserManagementAPI.getInstance().updateUser(updatedUser, userId.get());
    UserResponse expectedUser = JsonUtil.deserialize(response.asString(), UserResponse.class);

    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyUserResponse.assertThat(response, VerifyUserResponse.class)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateUserInDatabase(expectedUser, userId.get());
  }

  @Test
  void assertThatAdminCanPartiallyUpdateAnExistingUser() {
    // Arrange
    User partiallyUpdatedUser = User.builder().setEmail("updatedemail@gmail.com").setPhone("1122334455").build();

    // Act
    Response response = UserManagementAPI.getInstance().patchUser(partiallyUpdatedUser, userId.get());
    UserResponse expectedUser = JsonUtil.deserialize(response.asString(), UserResponse.class);

    ExtentLogger.logResponse(response);

    VerifyUserResponse.assertThat(response, VerifyUserResponse.class)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .assertAll();

    // Assert Database State
    dbValidator.validateUserInDatabase(expectedUser, userId.get());
  }
}
