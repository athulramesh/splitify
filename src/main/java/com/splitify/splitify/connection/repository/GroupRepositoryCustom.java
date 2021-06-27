package com.splitify.splitify.connection.repository;

import com.querydsl.core.Tuple;

import java.util.List;

public interface GroupRepositoryCustom {
  /**
   * Gets all groups of user
   *
   * @param userId userId
   * @return all groups
   */
  List<Tuple> getAllGroups(Integer userId);
}
