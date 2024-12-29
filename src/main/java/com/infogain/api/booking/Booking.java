package com.infogain.api.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javafaker.Faker;
import com.infogain.api.booking.entities.BookingDates;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder(setterPrefix = "set")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
  private String firstname;
  private String lastname;
  private long totalprice;
  private boolean depositpaid;
  private BookingDates bookingdates;
  private String additionalneeds;

  public static Booking getInstance() {
    BookingDates bookingdates =
        BookingDates.builder()
            .setCheckin(LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_DATE))
            .setCheckout(LocalDate.now().plusDays(4).format(DateTimeFormatter.ISO_DATE))
            .build();

    Booking booking =
        Booking.builder()
            .setFirstname(new Faker().name().firstName())
            .setLastname(new Faker().name().lastName())
            .setTotalprice(new Faker().number().numberBetween(1, 1000))
            .setDepositpaid(true)
            .setBookingdates(bookingdates)
            .setAdditionalneeds(new Faker().food().dish())
            .build();

    return booking;
  }
}
