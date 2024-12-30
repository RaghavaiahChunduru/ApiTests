package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.SC_OK;

import com.infogain.annotations.RegressionTest;
import com.infogain.api.usermanagement.User;
import com.infogain.api.usermanagement.UserManagementAPI;
import com.infogain.asserts.ValidateDB;
import com.infogain.report.ExtentLogger;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@RegressionTest
public class UserManagementTests {

  public static final String READ_UPDATE_USER_SCHEMA_FILE_PATH = "schemas/read-update-user-schema.json";
  public static final String CREATE_USER_SCHEMA_FILE_PATH = "schemas/create-user-schema.json";

  private ThreadLocal<Long> userId = new ThreadLocal<>();
  private ThreadLocal<User> user = new ThreadLocal<>();

  private final ValidateDB dbValidator = ValidateDB.getInstance();

  @BeforeEach
  public void setup() {

    // Arrange
    dbValidator.connectToDatabase();
    User newUser = User.getInstance();
    user.set(newUser);

    // Act
    Response response = UserManagementAPI.getInstance().newUser(newUser);

    // Assert API Response
    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .containsValue("userId")
        .doesNotContains("error")
        .matchesSchema(CREATE_USER_SCHEMA_FILE_PATH)
        .postHasUser(newUser)
        .assertAll();

    // Set userId
    userId.set(response.body().jsonPath().getLong("userId"));

    // Assert Database Entry
    dbValidator.validateUserInDatabase(newUser, userId.get());
  }

  @AfterEach
  public void tearDown() {
    if (userId.get() != null) {
      // Act
      Response response = UserManagementAPI.getInstance().deleteUser(userId.get());

      // Assert API Response
      VerifyUserResponse.assertThat(response)
          .statusCodeIs(SC_OK)
          .responseTimeBelow(200)
          .assertAll();

      // Assert Database Entry
      dbValidator.validateUserNotInDatabase(userId.get());

      userId.remove();
      user.remove();
    }
    dbValidator.closeConnection();
  }

  @Test
  void assertThatAdminCanGetAnExistingUser() {
    // Act
    Response response = UserManagementAPI.getInstance().getUser(userId.get());
    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(user.get())
        .assertAll();

    // Assert Database Entry
    dbValidator.validateUserInDatabase(user.get(), userId.get());
  }

  @Test
  void assertThatAdminCanUpdateAnExistingUser() {
    // Arrange
    User updatedUser = user.get().setUserName("UpdatedUserName");

    // Act
    Response response = UserManagementAPI.getInstance().updateUser(updatedUser, userId.get());
    ExtentLogger.logResponse(response);

    // Assert API Response
    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(updatedUser)
        .assertAll();

    // Assert Database Entry
    dbValidator.validateUserInDatabase(updatedUser, userId.get());
  }

  @Test
  void assertThatAdminCanPartiallyUpdateAnExistingUser() {
    // Arrange
    User partiallyUpdatedUser = User.builder().setEmail("updatedemail@gmail.com").setPhone("1122334455").build();

    // Act
    Response response = UserManagementAPI.getInstance().patchUser(partiallyUpdatedUser, userId.get());
    ExtentLogger.logResponse(response);

    // Assert API Response
    User expectedUser = user.get()
        .setEmail(partiallyUpdatedUser.getEmail())
        .setPhone(partiallyUpdatedUser.getPhone());

    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(expectedUser)
        .assertAll();

    // Assert Database State
    dbValidator.validateUserInDatabase(expectedUser, userId.get());
  }
}
