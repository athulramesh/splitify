package com.splitify.splitify.api.connection;

import com.splitify.splitify.api.connection.assembler.ConnectionAssembler;
import com.splitify.splitify.api.connection.dto.GroupDetailsDto;
import com.splitify.splitify.api.connection.dto.GroupMemberRequestDto;
import com.splitify.splitify.api.connection.dto.GroupRequestDto;
import com.splitify.splitify.api.connection.dto.GroupResponseDto;
import com.splitify.splitify.connection.ConnectionConstants;
import com.splitify.splitify.connection.service.GroupResponse;
import com.splitify.splitify.connection.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/groups/")
public class GroupApiImpl implements GroupApi {
  @Autowired GroupService groupService;
  @Autowired ConnectionAssembler assembler;

  /**
   * Creates new group
   *
   * @param userId userId
   * @param groupRequest groupRequest
   * @return groupId
   */
  @Override
  public GroupResponseDto createGroup(Integer userId, GroupRequestDto groupRequest) {
    return assembler.assembleCreateGroupResponse(
        groupService.createGroup(userId, assembler.assembleCreateGroupRequest(groupRequest)));
  }

  /**
   * Updates group
   *
   * @param groupId groupId
   * @param groupRequest groupRequestDto
   * @return SuccessMessage
   */
  @Override
  public GroupResponseDto UpdateGroup(Integer groupId, GroupRequestDto groupRequest) {

    groupService.updateGroup(groupId, assembler.assembleCreateGroupRequest(groupRequest));
    return assembler.assembleCreateGroupResponse(
        GroupResponse.builder().successMessage(ConnectionConstants.SUCCESS_MESSAGE).build());
  }

  /**
   * Deletes group
   *
   * @param groupId groupId
   * @return SuccessMessage
   */
  @Override
  public GroupResponseDto DeleteGroup(Integer groupId) {
    groupService.DeleteGroup(groupId);
    return assembler.assembleCreateGroupResponse(
        GroupResponse.builder().successMessage(ConnectionConstants.SUCCESS_MESSAGE).build());
  }

  /**
   * Get group details
   *
   * @param groupId groupId
   * @return GroupDetails
   */
  @Override
  public GroupDetailsDto getGroupDetails(Integer groupId) {
    return assembler.assembleGroupDetails(groupService.getGroupDetails(groupId));
  }

  /**
   * Add new group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   * @return SuccessMessage
   */
  @Override
  public GroupResponseDto addGroupMember(
      Integer groupId, GroupMemberRequestDto groupMemberRequest) {
    groupService.addGroupMember(groupId, assembler.assembleGroupMemberRequest(groupMemberRequest));
    return assembler.assembleCreateGroupResponse(
        GroupResponse.builder().successMessage(ConnectionConstants.SUCCESS_MESSAGE).build());
  }

  /**
   * Removes a group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   * @return SuccessMessage
   */
  @Override
  public GroupResponseDto removeGroupMember(
      Integer groupId, GroupMemberRequestDto groupMemberRequest) {
    groupService.removeGroupMember(
        groupId, assembler.assembleGroupMemberRequest(groupMemberRequest));
    return assembler.assembleCreateGroupResponse(
        GroupResponse.builder().successMessage(ConnectionConstants.SUCCESS_MESSAGE).build());
  }
}
