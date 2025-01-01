package com.infogain.api.usermanagement;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder(setterPrefix = "set")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

  private Long id;

  @JsonAlias("username")
  private String userName;

  private String password;
  private String email;
  private String phone;
  private int roleId;
  private String created_at;
  private String modified_at;
}
