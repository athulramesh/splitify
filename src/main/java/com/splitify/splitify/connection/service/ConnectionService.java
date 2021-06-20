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

  public Integer sendConnectionRequest(Integer fromUserId, TargetUser targetUser) {
    ConnectionEntity connectionEntity =
        ConnectionEntity.builder()
            .connectionFromId(fromUserId)
            .connectionToId(targetUser.getTargetUserId())
            .status(ConnectionStatus.NEW.getCode())
            .requestDate(Calendar.getInstance())
            .approvalDate(null)
            .cancelledDate(null)
            .rejectedDate(null)
            .build();
    connectionRepository.save(connectionEntity);
    return connectionEntity.getConnectionId();
  }

  public List<ConnectionDetails> fetchConnectionRequests(Integer userId, ConnectionStatus type) {
    List<ConnectionDetails> connectionDetailsList = new ArrayList<>();
    List<ConnectionEntity> connectionEntityList =
        type == ConnectionStatus.NEW
            ? connectionRepository.findByConnectionToIdAndStatus(userId, type.getCode())
            : connectionRepository.findByStatusAndConnectionToIdOrConnectionFromId(
                type.getCode(), userId, userId);

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
                      .fromId(friendId)
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
