package com.splitify.splitify.connection.domain;

import com.splitify.splitify.connection.enums.GroupMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_GROUP")
@Builder
public class GroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

  public void addGroupMember(Integer createdBy) {
    if (CollectionUtils.isEmpty(groupMember)) {
      groupMember = new ArrayList<>();
    }
    groupMember.add(
        GroupMemberEntity.builder()
            .group(this)
            .userId(createdBy)
            .status(GroupMemberStatus.ACTIVE.getCode())
            .build());
  }
}
