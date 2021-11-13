package com.splitify.splitify.connection.domain;

import com.splitify.splitify.connection.enums.GroupMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_GROUP")
@Builder
public class GroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "GROUPID")
  private Integer groupId;

  @Column(name = "GROUPNAME")
  private String groupName;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @Column(name = "STATUS")
  private Integer status;

  @OneToMany(
      mappedBy = "group",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<GroupMemberEntity> groupMember;

  @Column(name = "ISSIMPLIFIED")
  private Boolean isSimplified;

  @OneToMany(
      mappedBy = "group",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<DebtEntity> debt;

  @Column(name = "ISINDIVIUAL")
  private Boolean isIndividual;

  /**
   * Add new group member
   *
   * @param userId userId
   */
  public void addGroupMember(Integer userId) {
    if (CollectionUtils.isEmpty(groupMember)) {
      groupMember = new ArrayList<>();
    }
    groupMember.add(
        GroupMemberEntity.builder()
            .group(this)
            .userId(userId)
            .status(GroupMemberStatus.ACTIVE.getCode())
            .build());
  }

  /**
   * update Debt entities as per the current expenses and payments
   *
   * @param debtMap debtMap
   */
  public void updateDebt(Map<String, BigDecimal> debtMap) {

    if (debt != null) {
      List<DebtEntity> deletedEntities = new ArrayList<>();
      debt.forEach(
          debtEntity -> {
            String key = debtEntity.getFromId() + "-" + debtEntity.getToId();
            BigDecimal updatedAmount = debtMap.get(key);
            if (updatedAmount != null) {
              debtEntity.setAmount(updatedAmount);
              debtMap.remove(key);
            } else {
              deletedEntities.add(debtEntity);
            }
          });
      debt.removeAll(deletedEntities);
    }
    addDebtEntities(debtMap);
  }

  /**
   * add debt entities
   *
   * @param debtMap debtMap
   */
  private void addDebtEntities(Map<String, BigDecimal> debtMap) {
    List<DebtEntity> insertEntities = new ArrayList<>();
    debtMap.forEach(
        (key, value) -> {
          String[] keys = key.split("-");
          insertEntities.add(
              DebtEntity.builder()
                  .fromId(Integer.parseInt(keys[0]))
                  .toId(Integer.parseInt(keys[1]))
                  .amount(value)
                  .group(this)
                  .build());
        });
    debt.addAll(insertEntities);
  }

  public void updateSimplify(boolean simplify) {
    if (getIsSimplified() && !simplify) {
      setIsSimplified(false);
      debt.clear();
    } else if (!getIsSimplified() && simplify) {
      setIsSimplified(true);
    }
  }

  public List<DebtEntity> getDebtOfUser(Integer userId) {
    return debt.stream()
        .filter(d -> d.getFromId().compareTo(userId) == 0 || d.getToId().compareTo(userId) == 0)
        .collect(Collectors.toList());
  }

  public BigDecimal getDebtAmountOfUser(Integer userId) {
    BigDecimal[] amount = {BigDecimal.ZERO};
    debt.forEach(
        d -> {
          if (d.getFromId().compareTo(userId) == 0) {
            amount[0] = amount[0].add(d.getAmount());
          }
          if (d.getToId().compareTo(userId) == 0) {
            amount[0] = amount[0].subtract(d.getAmount());
          }
        });
    return amount[0];
  }

  public void updateDebtPayment(Integer paidBy, Integer receivedBy, BigDecimal amount) {
    DebtEntity debtEntity =
        debt.stream()
            .filter(
                d -> d.getFromId().compareTo(paidBy) == 0 && d.getToId().compareTo(receivedBy) == 0)
            .findFirst()
            .orElse(null);
    if (debtEntity != null) {
      BigDecimal updatedAmount = debtEntity.getAmount().subtract(amount);
      if (updatedAmount.compareTo(BigDecimal.ZERO) > 0) {
        debtEntity.setAmount(updatedAmount);
      } else {
        debt.remove(debtEntity);
      }
    }
  }
}
