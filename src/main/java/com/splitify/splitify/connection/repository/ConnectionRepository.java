package com.splitify.splitify.connection.repository;

import com.splitify.splitify.connection.domain.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
  List<Connection> findByStatusAndConnectionToIdOrConnectionFromId(
      Integer type, Integer connectionToId, Integer connectionFromId);

  List<Connection> findByConnectionToIdAndStatus(Integer connectionToId, Integer type);

  Connection findByConnectionId(Integer connectionId);
}
