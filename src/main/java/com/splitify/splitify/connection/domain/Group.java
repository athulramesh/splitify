package com.splitify.splitify.connection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GROUP")
@Builder
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer groupId;

  private String groupName;
  private Integer createdBy;
  private String status;

  @OneToMany(
      mappedBy = "group",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private GroupMember groupMember;

  public void addGroupMember(Integer createdBy) {
    setGroupMember(GroupMember.builder().group(this).userId(createdBy).status("NEW").build());
  }
}
