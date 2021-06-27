package com.splitify.splitify.connection.repository;

import com.splitify.splitify.connection.domain.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository
    extends JpaRepository<GroupEntity, Integer>, GroupRepositoryCustom {}
