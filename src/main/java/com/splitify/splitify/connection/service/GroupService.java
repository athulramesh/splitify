package com.splitify.splitify.connection.service;

import com.splitify.splitify.connection.domain.GroupEntity;
import com.splitify.splitify.connection.domain.GroupMemberEntity;
import com.splitify.splitify.connection.enums.GroupMemberStatus;
import com.splitify.splitify.connection.enums.GroupStatus;
import com.splitify.splitify.connection.repository.GroupRepository;
import com.splitify.splitify.security.service.UserDetails;
import com.splitify.splitify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** The Group service. */
@Service
public class GroupService {
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserService userService;

  /**
   * Creates group
   *
   * @param userId user id
   * @param groupRequest groupRequest
   * @return groupId
   */
  public GroupResponse createGroup(Integer userId, GroupRequest groupRequest) {
    GroupEntity group =
        GroupEntity.builder()
            .groupName(groupRequest.getGroupName())
            .createdBy(userId)
            .status(GroupStatus.ACTIVE.getCode())
            .build();
    group.addGroupMember(userId);
    groupRepository.save(group);
    return GroupResponse.builder().groupId(group.getGroupId()).build();
  }

  /**
   * Updates group
   *
   * @param groupId groupId
   * @param groupRequest groupRequest
   */
  public void updateGroup(Integer groupId, GroupRequest groupRequest) {
    GroupEntity group = getGroupById(groupId);
    group.setGroupName(groupRequest.getGroupName());
    groupRepository.save(group);
  }

  /**
   * Get group by id
   *
   * @param id id
   * @return Group Entity
   */
  private GroupEntity getGroupById(Integer id) {
    return groupRepository.findById(id).orElse(null);
  }

  /**
   * Delete group
   *
   * @param groupId groupId
   */
  public void deleteGroup(Integer groupId) {
    GroupEntity group = getGroupById(groupId);
    group.setStatus(GroupStatus.DELETED.getCode());
    groupRepository.save(group);
  }

  /**
   * get group details
   *
   * @param groupId groupId
   * @return Group details
   */
  public GroupDetails getGroupDetails(Integer groupId) {
    GroupEntity groupEntity = getGroupById(groupId);
    if (groupEntity != null) {
      return buildGroupDetails(groupEntity);
    }
    return null;
  }

  /**
   * build the group details
   *
   * @param groupEntity groupEntity
   * @return Group details
   */
  private GroupDetails buildGroupDetails(GroupEntity groupEntity) {
    return GroupDetails.builder()
        .groupId(groupEntity.getGroupId())
        .groupName(groupEntity.getGroupName())
        .createdBy(buildUser(groupEntity.getCreatedBy()))
        .groupMemberList(buildGroupMemberList(groupEntity.getGroupMember()))
        .build();
  }

  /**
   * builds user details
   *
   * @param userId userId
   * @return User details
   */
  private UserDetails buildUser(Integer userId) {
    return UserDetails.builder()
        .id(userService.getUserById(userId).getId())
        .userName(userService.getUserById(userId).getUserName())
        .firstName(userService.getUserById(userId).getFirstName())
        .lastName(userService.getUserById(userId).getLastName())
        .email(userService.getUserById(userId).getEmail())
        .build();
  }

  /**
   * builds group members list
   *
   * @param groupMemberList groupMemberList
   * @return user details list
   */
  private List<UserDetails> buildGroupMemberList(List<GroupMemberEntity> groupMemberList) {
    List<UserDetails> groupMemberDetailsList = new ArrayList<>();

    groupMemberList.stream()
        .filter(
            groupMemberEntity ->
                groupMemberEntity.getStatus() == GroupMemberStatus.ACTIVE.getCode())
        .forEach(
            groupMemberEntity ->
                groupMemberDetailsList.add(
                    UserDetails.builder()
                        .id(groupMemberEntity.getUserId())
                        .userName(
                            userService.getUserById(groupMemberEntity.getUserId()).getUserName())
                        .firstName(
                            userService.getUserById(groupMemberEntity.getUserId()).getFirstName())
                        .lastName(
                            userService.getUserById(groupMemberEntity.getUserId()).getLastName())
                        .email(userService.getUserById(groupMemberEntity.getUserId()).getEmail())
                        .build()));

    return groupMemberDetailsList;
  }

  /**
   * add group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   */
  public void addGroupMember(Integer groupId, GroupMemberRequest groupMemberRequest) {
    GroupEntity groupEntity = getGroupById(groupId);
    groupEntity.addGroupMember(groupMemberRequest.getUserId());
    groupRepository.save(groupEntity);
  }

  /**
   * removes group member
   *
   * @param groupId groupId
   * @param groupMemberRequest groupMemberRequest
   */
  public void removeGroupMember(Integer groupId, GroupMemberRequest groupMemberRequest) {
    GroupEntity groupEntity = getGroupById(groupId);
    List<GroupMemberEntity> groupMemberEntityList = groupEntity.getGroupMember();
    groupMemberEntityList.stream()
        .filter(
            groupMemberEntity ->
                groupMemberEntity.getUserId().compareTo(groupMemberRequest.getUserId()) == 0)
        .collect(Collectors.toList())
        .get(0)
        .setStatus(GroupMemberStatus.REMOVED.getCode());
    groupRepository.save(groupEntity);
  }

  /**
   * Get all groups of user
   *
   * @param userId userId
   * @return all groups
   */
  public Group getAllGroups(Integer userId) {
    List<GroupIdentity> groupIdentities = new ArrayList<>();
    groupRepository
        .getAllGroups(userId)
        .forEach(
            g -> {
              groupIdentities.add(
                  GroupIdentity.builder()
                      .groupId(g.get(0, Integer.class))
                      .groupName(g.get(1, String.class))
                      .build());
            });
    return Group.builder().groups(groupIdentities).build();
  }
}
