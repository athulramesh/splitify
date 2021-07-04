package com.splitify.splitify.connection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DEBT")
@Builder
public class DebtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "DEBTID")
  private Integer debtId;

  @Column(name = "FROMID")
  private Integer fromId;

  @Column(name = "TOID")
  private Integer toId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUPID", nullable = false)
  private GroupEntity group;
}
