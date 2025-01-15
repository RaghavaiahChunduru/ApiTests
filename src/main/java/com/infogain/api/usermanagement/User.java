package com.infogain.api.usermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javafaker.Faker;
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
public class User {

  private String username;
  private String password;
  private String email;
  private String phone;
  private int roleId;

  public static User getInstance() {
    Faker faker = new Faker();

    User user = User.builder()
        .setUsername(faker.regexify("[a-zA-Z]{4,12}"))
        .setPassword(faker.regexify("[a-zA-Z0-9!@#$%^&*]{8,18}"))
        .setEmail(faker.lorem().characters(1, 8) + "@gmail.com")
        .setPhone(String.format("%010d", faker.number().randomNumber(10, true)))
        .setRoleId(faker.number().numberBetween(1, 9))
        .build();

    log.info("Generated User: {}", user);

    return user;
  }
}
