package com.infogain.usermanagementapi;

import static org.apache.http.HttpStatus.*;

import com.infogain.annotations.RegressionTest;
import com.infogain.api.usermanagement.User;
import com.infogain.api.usermanagement.UserManagementAPI;
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

  @BeforeEach
  public void setup() {
    // Arrange
    User newUser = User.getInstance();
    user.set(newUser);

    // Act
    Response response = UserManagementAPI.getInstance().newUser(newUser);

    // Assert
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
  }

  @AfterEach
  public void tearDown() {
    if (userId.get() != null) {
      Response response = UserManagementAPI.getInstance().deleteUser(userId.get());

      VerifyUserResponse.assertThat(response)
          .statusCodeIs(SC_OK)
          .responseTimeBelow(200)
          .assertAll();

      userId.remove();
      user.remove();
    }
  }

  @Test
  void assertThatAdminCanGetAnExistingUser() {
    // Act
    Response response = UserManagementAPI.getInstance().getUser(userId.get());
    ExtentLogger.logResponse(response);

    // Assert
    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(user.get())
        .assertAll();
  }

  @Test
  void assertThatAdminCanUpdateAnExistingUser() {
    // Arrange
    User updatedUser = user.get().setUserName("NewUserName");

    // Act
    Response response = UserManagementAPI.getInstance().updateUser(updatedUser, userId.get());
    ExtentLogger.logResponse(response);

    // Assert
    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(updatedUser)
        .assertAll();
  }

  @Test
  void assertThatAdminCanPartiallyUpdateAnExistingUser() {
    // Arrange
    User partiallyUpdatedUser = User.builder().setEmail("updatedemail@gmail.com").setPhone("1122334455").build();

    // Act
    Response response = UserManagementAPI.getInstance().patchUser(partiallyUpdatedUser, userId.get());
    ExtentLogger.logResponse(response);

    // Assert
    User expectedUser = user
        .get()
        .setEmail(partiallyUpdatedUser.getEmail())
        .setPhone(partiallyUpdatedUser.getPhone());

    VerifyUserResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .responseTimeBelow(200)
        .matchesSchema(READ_UPDATE_USER_SCHEMA_FILE_PATH)
        .hasUser(expectedUser)
        .assertAll();
  }
}