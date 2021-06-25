package com.splitify.splitify.connection.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Group response */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
  private Integer groupId;
  private String successMessage;
}
