package com.splitify.splitify.api.connection;

import com.splitify.splitify.api.connection.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/groups/")
@CrossOrigin(origins = "https://simplifysplit.web.app", allowedHeaders = "*")
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
  GroupResponseDto updateGroup(
      @PathVariable("groupId") Integer groupId, @RequestBody GroupRequestDto groupRequest);

  /**
   * Deletes group
   *
   * @param groupId groupId
   * @return SuccessMessage
   */
  @PutMapping("{groupId}/delete")
  GroupResponseDto deleteGroup(@PathVariable("groupId") Integer groupId);

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

  /**
   * Get group details
   *
   * @param userId userId
   * @return GroupDetails
   */
  @GetMapping("{userId}/all-groups")
  GroupDto getAllGroups(@PathVariable("userId") Integer userId);

  /**
   * Updates group
   *
   * @param groupId groupId
   * @param simplifyDebtDto simplifyDebtDto
   * @return SuccessMessage
   */
  @PutMapping("{groupId}/simplify")
  GroupResponseDto updateSimplifyDebt(
      @PathVariable("groupId") Integer groupId, @RequestBody SimplifyDebtDto simplifyDebtDto);
}
