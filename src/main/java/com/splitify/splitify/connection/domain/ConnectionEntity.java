package com.splitify.splitify.connection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CONNECTION")
@Builder
public class ConnectionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "CONNECTIONID")
  private Integer connectionId;

  @Column(name = "CONNECTIONFROMID")
  private Integer connectionFromId;

  @Column(name = "CONNECTIONTOID")
  private Integer connectionToId;

  @Column(name = "STATUS")
  private Integer status;

  @Column(name = "REQUESTDATE")
  private Calendar requestDate;

  @Column(name = "APPROVALDATE")
  private Calendar approvalDate;

  @Column(name = "CANCELLEDDATE")
  private Calendar cancelledDate;

  @Column(name = "REJECTEDDATE")
  private Calendar rejectedDate;
}
