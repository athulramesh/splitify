package com.splitify.splitify.connection.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectionDetails {

  private Integer fromId;
  private String userName;
  private String firstName;
  private String lastName;
  private Calendar requestDate;
  private Integer connectionId;
}
