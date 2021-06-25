package com.splitify.splitify.api.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDto {
  private Integer groupId;
  private String successMessage;
}
