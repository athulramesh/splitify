package com.splitify.splitify.api.connection.assembler;

import com.splitify.splitify.api.connection.dto.*;
import com.splitify.splitify.connection.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionAssembler {
  @Autowired private ModelMapper modelMapper;

  public TargetUser assembleUserId(TargetUserDto targetUserDto) {
    return modelMapper.map(targetUserDto, TargetUser.class);
  }

  public ConnectionIdDto assembleConnectionIdDetails(ConnectionId connectionId) {
    return modelMapper.map(connectionId, ConnectionIdDto.class);
  }

  public GroupResponseDto assembleCreateGroupResponse(GroupResponse groupResponse) {
    return modelMapper.map(groupResponse, GroupResponseDto.class);
  }

  public GroupRequest assembleCreateGroupRequest(GroupRequestDto groupRequestDto) {
    return modelMapper.map(groupRequestDto, GroupRequest.class);
  }

  public GroupDetailsDto assembleGroupDetails(GroupDetails groupDetails) {
    return modelMapper.map(groupDetails, GroupDetailsDto.class);
  }

  public GroupMemberRequest assembleGroupMemberRequest(
      GroupMemberRequestDto groupMemberRequestDto) {
    return modelMapper.map(groupMemberRequestDto, GroupMemberRequest.class);
  }

  public GroupDto assembleGroupDto(Group groups) {
    return modelMapper.map(groups, GroupDto.class);
  }
}
