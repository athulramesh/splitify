package com.splitify.splitify.connection.repository;

import com.splitify.splitify.connection.domain.ConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<ConnectionEntity, Integer> {
  List<ConnectionEntity> findByStatusAndConnectionToIdOrStatusAndConnectionFromId(
      Integer type, Integer connectionToId, Integer connectionType, Integer connectionFromId);

  List<ConnectionEntity> findByConnectionToIdAndStatus(Integer connectionToId, Integer type);

  ConnectionEntity findByConnectionId(Integer connectionId);
}
