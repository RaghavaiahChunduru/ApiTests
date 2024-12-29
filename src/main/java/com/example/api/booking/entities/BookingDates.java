package com.example.api.booking.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
public class BookingDates {
  private String checkin;
  private String checkout;
}
