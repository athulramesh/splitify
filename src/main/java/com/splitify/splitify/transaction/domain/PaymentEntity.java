package com.splitify.splitify.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PAYMENT")
@Builder
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "PAYMENTID")
  private Integer paymentId;

  @Column(name = "FROMID")
  private Integer fromId;

  @Column(name = "TOID")
  private Integer toId;

  @Column(name = "AMOUNT")
  private BigDecimal amount;

  @Column(name = "ONDATE")
  private Calendar onDate;

  @Column(name = "CREATEDBY")
  private Integer createdBy;

  @Column(name = "GROUPID")
  private Integer groupId;
}
// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhdGgiLCJleHAiOjE2MjQyMTA2OTMsImlhdCI6MTYyNDE3NDY5M30.Hh1QnYRSBvxxkLN-xRtL9qz4tph3povBp2Lm8sb80qY
// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWV5IiwiZXhwIjoxNjI0MjEwNzcyLCJpYXQiOjE2MjQxNzQ3NzJ9.-3zShw_0jphG9hI9_sBbVilNJqzzEtt_Og6fnYyk-C0
