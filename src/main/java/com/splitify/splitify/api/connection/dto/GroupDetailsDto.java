package com.splitify.splitify.api.connection.dto;

import com.splitify.splitify.api.security.dto.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailsDto {
  private Integer groupId;
  private String groupName;
  private UserDetailsDto createdBy;
  private List<UserDetailsDto> groupMemberList;
}
