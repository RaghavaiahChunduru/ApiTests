package com.infogain.api.usermanagement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserResponse {
  private long userId;
  private User user;
}
