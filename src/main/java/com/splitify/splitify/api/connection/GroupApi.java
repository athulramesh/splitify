package com.splitify.splitify.api.connection;

import com.splitify.splitify.api.connection.dto.GroupDetailsDto;
import com.splitify.splitify.api.connection.dto.GroupMemberRequestDto;
import com.splitify.splitify.api.connection.dto.GroupRequestDto;
import com.splitify.splitify.api.connection.dto.GroupResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/groups/")
public interface GroupApi {

  /**
   * Creates new group
   *
   * @param userId userId
   * @param groupRequest groupRequest
   * @return groupId
   */
  @PostMapping("{userId}")
  GroupResponseDto createGroup(
      @PathVariable("userId") Integer userId, @RequestBody GroupRequestDto groupRequest);

  /**
   * Updates group
   *
   * @param groupId groupId
   * @param groupRequest groupRequestDto
   * @return SuccessMessage
   */
  @PutMapping("{groupId}")
  GroupResponseDto UpdateGroup(
      @PathVariable("groupId") Integer groupId, @RequestBody GroupRequestDto groupRequest);

  /**
   * Deletes group
   *
   * @param groupId groupId
   * @return SuccessMessage
   */
  @PutMapping("{groupId}/delete")
  GroupResponseDto DeleteGroup(@PathVariable("groupId") Integer groupId);

  /**
   * Get group details
   *
   * @param groupId groupId
   * @return GroupDetails
   */
  @GetMapping("{groupId}")
  GroupDetailsDto getGroupDetails(@PathVariable Integer groupId);

  /**
   * Add new group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   * @return SuccessMessage
   */
  @PostMapping("{groupId}/add-member")
  GroupResponseDto addGroupMember(
      @PathVariable("groupId") Integer groupId,
      @RequestBody GroupMemberRequestDto groupMemberRequest);

  /**
   * Removes a group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   * @return SuccessMessage
   */
  @PutMapping("{groupId}/remove-member")
  GroupResponseDto removeGroupMember(
      @PathVariable("groupId") Integer groupId,
      @RequestBody GroupMemberRequestDto groupMemberRequest);
}
