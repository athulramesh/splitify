package com.splitify.splitify.connection.service;

import com.splitify.splitify.api.connection.dto.ConnectionIdDto;
import com.splitify.splitify.connection.domain.ConnectionEntity;
import com.splitify.splitify.connection.enums.ConnectionStatus;
import com.splitify.splitify.connection.repository.ConnectionRepository;
import com.splitify.splitify.security.domain.UserEntity;
import com.splitify.splitify.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class ConnectionService {

  @Autowired ConnectionRepository connectionRepository;
  @Autowired UserRepository userRepository;
  @Autowired GroupService groupService;

  public ConnectionId sendConnectionRequest(Integer fromUserId, TargetUser targetUser) {
    if (targetUser != null && targetUser.getTargetUserId() != null) {
      ConnectionEntity connectionEntity =
          ConnectionEntity.builder()
              .connectionFromId(fromUserId)
              .connectionToId(targetUser.getTargetUserId())
              .status(ConnectionStatus.NEW.getCode())
              .requestDate(Calendar.getInstance())
              .build();
      connectionRepository.save(connectionEntity);
      return ConnectionId.builder().connectionId(connectionEntity.getConnectionId()).build();
    }
    return null;
  }

  public List<ConnectionDetails> fetchConnectionRequests(Integer userId, ConnectionStatus type) {
    List<ConnectionDetails> connectionDetailsList = new ArrayList<>();
    List<ConnectionEntity> connectionEntityList =
        type == ConnectionStatus.NEW
            ? connectionRepository.findByConnectionToIdAndStatus(userId, type.getCode())
            : connectionRepository.findByStatusAndConnectionToIdOrStatusAndConnectionFromId(
                type.getCode(), userId, type.getCode(), userId);

    connectionEntityList.forEach(
        connectionEntity -> {
          try {
            Integer friendId =
                connectionEntity.getConnectionFromId().compareTo(userId) == 0
                    ? connectionEntity.getConnectionToId()
                    : connectionEntity.getConnectionFromId();
            UserEntity friend = findUserById(friendId);
            if (friend != null) {
              connectionDetailsList.add(
                  ConnectionDetails.builder()
                      .id(friendId)
                      .groupId(connectionEntity.getGroupid())
                      .userName(friend.getUserName())
                      .firstName(friend.getFirstName())
                      .lastName(friend.getLastName())
                      .requestDate(connectionEntity.getRequestDate())
                      .connectionId(connectionEntity.getConnectionId())
                      .build());
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    return (connectionDetailsList);
  }

  private UserEntity findUserById(Integer id) throws Exception {
    return userRepository.findById(id).orElseThrow(() -> new Exception("User Not Found"));
  }

  public String acceptConnectionRequest(ConnectionIdDto connectionIdDto) {
    ConnectionEntity connectionEntityRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionEntityRequest.setStatus(ConnectionStatus.ACTIVE.getCode());
    connectionEntityRequest.setApprovalDate(Calendar.getInstance());
    Integer groupId =
        groupService.createIndividualGroup(
            connectionEntityRequest.getConnectionFromId(),
            connectionEntityRequest.getConnectionToId());
    connectionEntityRequest.setGroupid(groupId);
    connectionRepository.save(connectionEntityRequest);
    return "Success";
  }

  public String rejectConnectionRequest(ConnectionIdDto connectionIdDto) {
    ConnectionEntity connectionEntityRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionEntityRequest.setStatus(ConnectionStatus.REJECTED.getCode());
    connectionEntityRequest.setRejectedDate(Calendar.getInstance());
    connectionRepository.save(connectionEntityRequest);
    return "Success";
  }
}
