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
@Table(name = "GROUPMEMBER")
@Builder
public class GroupMemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "GROUPMEMBERID")
  private Integer groupMemberId;

  @Column(name = "USERID")
  private Integer userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUPID", nullable = false)
  private GroupEntity group;

  @Column(name = "STATUS")
  private Integer status;
}
