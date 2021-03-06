package com.splitify.splitify.api.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private String userName;
  private String firstName;
  private String lastName;
  private String password;
  private String email;
}
