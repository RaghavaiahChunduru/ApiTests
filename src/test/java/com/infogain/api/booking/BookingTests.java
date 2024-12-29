package com.infogain.api.booking;

import static com.infogain.api.auth.Scope.ADMIN;
import static com.infogain.api.auth.Scope.GUEST;
import static org.apache.http.HttpStatus.*;

import com.infogain.annotations.FailingTest;
import com.infogain.annotations.RegressionTest;
import com.infogain.report.ExtentLogger;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
@RegressionTest
public class BookingTests {
  public static final String READ_UPDATE_BOOKING_SCHEMA_FILE_PATH =
      "schemas/read-update-booking-schema.json";
  public static final String CREATE_BOOKING_SCHEMA_FILE_PATH = "schemas/create-booking-schema.json";

  private ThreadLocal<Long> bookingId = new ThreadLocal<>();
  private ThreadLocal<Booking> booking = new ThreadLocal<>();

  @BeforeEach
  public void setup() {
    // Arrange
    Booking newBooking = Booking.getInstance();
    booking.set(newBooking);

    // Act
    Response response = BookingAPI.useAs(ADMIN).newBooking(newBooking);

    // Assert
    VerifyBookingResponse.assertThat(response)
        .statusCodeIs(SC_OK)
        .containsValue("bookingid")
        .doesNotContains("error")
        .matchesSchema(CREATE_BOOKING_SCHEMA_FILE_PATH)
        .postHasBooking(newBooking)
        .assertAll();

    // Set bookingId
    bookingId.set(response.body().jsonPath().getLong("bookingid"));
  }

  @AfterEach
  public void tearDown() {
    if (bookingId.get() != null) {
      Response response = BookingAPI.useAs(ADMIN).deleteBooking(bookingId.get());

      // Assert (It should ideally be 200 but this application has a bug and it gives
      // 201)
      VerifyBookingResponse.assertThat(response).statusCodeIs(SC_CREATED).assertAll();

      bookingId.remove();
      booking.remove();
    }
  }

  @Nested
  class AdminUser {
    @Test
    void assertThatAUserCanGetAnExistingBooking() {
      // Act
      Response response = BookingAPI.useAs(GUEST).getBooking(bookingId.get());
      ExtentLogger.logResponse(response);

      // Assert
      VerifyBookingResponse.assertThat(response)
          .statusCodeIs(SC_OK)
          .matchesSchema(READ_UPDATE_BOOKING_SCHEMA_FILE_PATH)
          .hasBooking(booking.get())
          .assertAll();
    }

    @Test
    void assertThatAUserCanUpdateAnExistingBooking() {
      // Arrange
      Booking updatedBooking = booking.get();
      updatedBooking.setFirstname("NewFirstName");

      // Act
      Response response = BookingAPI.useAs(ADMIN).updateBooking(updatedBooking, bookingId.get());
      ExtentLogger.logResponse(response);

      // Assert
      VerifyBookingResponse.assertThat(response)
          .statusCodeIs(SC_OK)
          .matchesSchema(READ_UPDATE_BOOKING_SCHEMA_FILE_PATH)
          .hasBooking(updatedBooking)
          .assertAll();
    }

    @FailingTest
    @Test
    void assertThatAUserCanPartiallyUpdateAnExistingBooking() {
      // Arrange
      Booking partialBooking =
          Booking.builder().setFirstname("Raghavaiah").setLastname("Chunduru").build();

      // Act
      Response response = BookingAPI.useAs(ADMIN).patchBooking(partialBooking, bookingId.get());
      ExtentLogger.logResponse(response);

      // Assert
      Booking expectedBooking =
          booking
              .get()
              .setFirstname(partialBooking.getFirstname())
              .setLastname(partialBooking.getLastname());

      VerifyBookingResponse.assertThat(response)
          .statusCodeIs(SC_OK)
          .matchesSchema(READ_UPDATE_BOOKING_SCHEMA_FILE_PATH)
          .hasBooking(expectedBooking)
          .assertAll();
    }
  }

  @Nested
  class GuestUser {
    @Test
    void assertThatAUserCanNotUpdateAnExistingBookingWithoutValidAuthentication() {
      // Arrange
      Booking unauthorizedBooking = booking.get();
      unauthorizedBooking.setFirstname("Vinod");

      // Act
      Response response =
          BookingAPI.useAs(GUEST).updateBooking(unauthorizedBooking, bookingId.get());
      ExtentLogger.logResponse(response);

      // Assert
      VerifyBookingResponse.assertThat(response).statusCodeIs(SC_FORBIDDEN).assertAll();
    }
  }
}
