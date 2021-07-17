package com.splitify.splitify.connection.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.splitify.splitify.connection.domain.GroupEntity;
import com.splitify.splitify.connection.domain.QGroupEntity;
import com.splitify.splitify.connection.domain.QGroupMemberEntity;
import com.splitify.splitify.connection.enums.GroupStatus;
import com.splitify.splitify.transaction.service.DebtVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {

  @Autowired private EntityManager entityManager;

  /**
   * Gets all groups of user
   *
   * @param userId userId
   * @return all groups
   */
  @Override
  public List<Tuple> getAllGroups(Integer userId, Boolean isSimplified) {
    QGroupEntity group = QGroupEntity.groupEntity;
    QGroupMemberEntity groupMember = QGroupMemberEntity.groupMemberEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(groupMember.userId.eq(userId));
    where.and(group.status.eq(GroupStatus.ACTIVE.getCode()));
    where.and(group.isSimplified.eq(isSimplified));
    JPAQuery<DebtVo> query = new JPAQuery<>(entityManager);
    return query
        .select(group.groupId, group.groupName)
        .from(group)
        .innerJoin(groupMember)
        .on(group.groupId.eq(groupMember.group.groupId))
        .where(where)
        .fetch();
  }

  /**
   * Gets all groups of user
   *
   * @param userId userId
   * @return all groups
   */
  @Override
  public List<GroupEntity> getAllGroupsOfUser(Integer userId, Boolean isSimplified) {
    QGroupEntity group = QGroupEntity.groupEntity;
    QGroupMemberEntity groupMember = QGroupMemberEntity.groupMemberEntity;
    BooleanBuilder where = new BooleanBuilder();
    where.and(groupMember.userId.eq(userId));
    where.and(group.status.eq(GroupStatus.ACTIVE.getCode()));
    where.and(group.isSimplified.eq(isSimplified));
    JPAQuery<DebtVo> query = new JPAQuery<>(entityManager);
    return query
        .select(group)
        .from(group)
        .innerJoin(groupMember)
        .on(group.groupId.eq(groupMember.group.groupId))
        .where(where)
        .fetch();
  }
}
