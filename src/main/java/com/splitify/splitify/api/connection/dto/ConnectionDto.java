package com.splitify.splitify.api.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDto {
  private Integer fromId;
  private String userName;
  private String firstName;
  private String lastName;
  private String requestDate;
  private Integer connectionId;
}
