package com.splitify.splitify.security.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {
  private int id;
  private String userName;
  private String email;
  private String firstName;
  private String lastName;
}
