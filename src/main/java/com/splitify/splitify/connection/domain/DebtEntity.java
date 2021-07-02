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
@Table(name = "DEBT")
@Builder
public class DebtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "GROUPID")
  private Integer debtId;

  @Column(name = "FROMID")
  private Integer fromId;

  @Column(name = "TOID")
  private Integer toId;

  @Column(name = "AMOUNT")
  private Integer amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUPID", nullable = false)
  private GroupEntity group;
}
