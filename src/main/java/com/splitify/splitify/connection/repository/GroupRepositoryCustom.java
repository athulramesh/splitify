package com.splitify.splitify.connection.repository;

import com.querydsl.core.Tuple;
import com.splitify.splitify.connection.domain.GroupEntity;

import java.util.List;

public interface GroupRepositoryCustom {
  /**
   * Gets all groups of user
   *
   * @param userId userId
   * @return all groups
   */
  List<Tuple> getAllGroups(Integer userId, Boolean isSimplified);

  List<GroupEntity> getAllGroupsOfUser(Integer userId, Boolean isSimplified);
}
