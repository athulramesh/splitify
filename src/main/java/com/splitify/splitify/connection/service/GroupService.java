package com.splitify.splitify.connection.service;

import com.splitify.splitify.api.expense.dto.TransactionDto;
import com.splitify.splitify.connection.domain.GroupEntity;
import com.splitify.splitify.connection.domain.GroupMemberEntity;
import com.splitify.splitify.connection.enums.GroupMemberStatus;
import com.splitify.splitify.connection.enums.GroupStatus;
import com.splitify.splitify.connection.repository.GroupRepository;
import com.splitify.splitify.security.service.UserDetails;
import com.splitify.splitify.security.service.UserService;
import com.splitify.splitify.transaction.service.DebtVo;
import com.splitify.splitify.transaction.service.ExpenseService;
import com.splitify.splitify.transaction.service.GroupTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The Group service. */
@Service
public class GroupService {
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserService userService;
  @Autowired private ExpenseService expenseService;
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
            .isSimplified(groupRequest.isSimplify())
            .isIndividual(Boolean.FALSE)
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
    group.updateSimplify(groupRequest.isSimplify());
    if (group.getIsSimplified()) {
      updateDebts(groupId);
    }
    groupRepository.save(group);
  }

  /**
   * Get group by id
   *
   * @param id id
   * @return Group Entity
   */
  public GroupEntity getGroupById(Integer id) {
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
        .createdBy(userService.getUserById(groupEntity.getCreatedBy()))
        .groupMemberList(buildGroupMemberList(groupEntity.getGroupMember()))
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
                groupMemberDetailsList.add(userService.getUserById(groupMemberEntity.getUserId())));

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
        .findFirst()
        .ifPresent(entity -> entity.setStatus(GroupMemberStatus.REMOVED.getCode()));

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

  /**
   * update Debt entities as per the current expenses and payments
   *
   * @param groupId groupId
   */
  public void updateDebts(Integer groupId) {
    GroupEntity entity = getGroupById(groupId);
    if (entity != null) {
      List<DebtVo> newDebts = expenseService.getDebts(groupId);
      Map<String, BigDecimal> debtMap = new HashMap<>();
      newDebts.forEach(
          debtVo -> debtMap.put(debtVo.getFromId() + "-" + debtVo.getToId(), debtVo.getAmount()));
      entity.updateDebt(debtMap);
      groupRepository.save(entity);
    }
  }

  /**
   * Updates the simplify group
   *
   * @param groupId groupId
   * @param simplify simplify
   */
  public void simplifyDebt(Integer groupId, boolean simplify) {
    GroupEntity groupEntity = getGroupById(groupId);
    if (groupEntity != null) {
      groupEntity.updateSimplify(simplify);
      if (groupEntity.getIsSimplified()) {
        updateDebts(groupId);
      }
      groupRepository.save(groupEntity);
    }
  }

  public void updateDebtsAfterExpenseAdd(Integer groupId) {
    GroupEntity groupEntity = getGroupById(groupId);
    if (groupEntity != null && groupEntity.getIsSimplified()) {
      updateDebts(groupId);
      groupRepository.save(groupEntity);
    }
  }

  public boolean isSimplifiedGroup(Integer groupId) {
    GroupEntity groupEntity = getGroupById(groupId);
    return groupEntity != null && groupEntity.getIsSimplified();
  }

  public Integer createIndividualGroup(Integer connectionFromId, Integer connectionToId) {
    GroupEntity group =
        GroupEntity.builder()
            .groupName("INDIVIDUAL")
            .createdBy(connectionFromId)
            .status(GroupStatus.ACTIVE.getCode())
            .isSimplified(false)
            .isIndividual(Boolean.TRUE)
            .build();
    group.addGroupMember(connectionFromId);
    group.addGroupMember(connectionToId);
    return groupRepository.save(group).getGroupId();
  }

  public List<GroupTransaction> getSimplifiedTransactionsForUser(Integer fromId) {
    List<GroupTransaction> simplifiedTransactions = new ArrayList<>();
    List<GroupEntity> groups = groupRepository.getAllGroupsOfUser(fromId, Boolean.FALSE);
    groups.forEach(
        g -> {
          BigDecimal amount = g.getDebtAmountOfUser(fromId);
          if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            simplifiedTransactions.add(
                GroupTransaction.builder()
                    .groupId(g.getGroupId())
                    .groupName(g.getGroupName())
                    .user(null)
                    .transaction(
                        TransactionDto.builder()
                            .fromAmount(amount)
                            .toAmount(BigDecimal.ZERO)
                            .build())
                    .build());
          } else {
            simplifiedTransactions.add(
                GroupTransaction.builder()
                    .groupId(g.getGroupId())
                    .groupName(g.getGroupName())
                    .user(null)
                    .transaction(
                        TransactionDto.builder()
                            .toAmount(amount.negate())
                            .fromAmount(BigDecimal.ZERO)
                            .build())
                    .build());
          }
        });
    return simplifiedTransactions;
  }
}
