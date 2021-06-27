package com.splitify.splitify.connection.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupIdentity {
  private Integer groupId;
  private String groupName;
}
