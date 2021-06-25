package com.splitify.splitify.connection.service;

import com.splitify.splitify.security.service.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDetails {
  private Integer groupId;
  private String groupName;
  private UserDetails createdBy;
  private List<UserDetails> groupMemberList;
}
