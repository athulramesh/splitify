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
@Table(name = "CONNECTIONS")
@Builder
public class Connection {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer connectionId;

  private Integer connectionFromId;
  private Integer connectionToId;
  private String status;
  private Calendar approvalDate;
  private Calendar cancelledDate;
  private Calendar rejectedDate;
}
