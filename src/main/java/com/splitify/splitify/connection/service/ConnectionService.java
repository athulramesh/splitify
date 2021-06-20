package com.splitify.splitify.connection.service;

import com.splitify.splitify.api.connection.dto.ConnectionIdDto;
import com.splitify.splitify.connection.domain.Connection;
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
    Connection connection =
        Connection.builder()
            .connectionFromId(fromUserId)
            .connectionToId(targetUser.getTargetUserId())
            .status(ConnectionStatus.NEW.getCode())
            .requestDate(Calendar.getInstance())
            .approvalDate(null)
            .cancelledDate(null)
            .rejectedDate(null)
            .build();
    connectionRepository.save(connection);
    return connection.getConnectionId();
  }

  public List<ConnectionDetails> fetchConnectionRequests(Integer userId, ConnectionStatus type) {
    List<ConnectionDetails> connectionDetailsList = new ArrayList<>();
    connectionRepository
        .findByStatusAndConnectionToIdOrConnectionFromId(type.getCode(), userId, userId)
        .forEach(
            connection -> {
              try {
                Integer friendId =
                    connection.getConnectionFromId().compareTo(userId) == 0
                        ? connection.getConnectionToId()
                        : connection.getConnectionFromId();
                UserEntity friend = findUserById(friendId);
                if (friend != null) {
                  connectionDetailsList.add(
                      ConnectionDetails.builder()
                          .fromId(friendId)
                          .userName(friend.getUserName())
                          .firstName(friend.getFirstName())
                          .lastName(friend.getLastName())
                          .requestDate(connection.getRequestDate())
                          .connectionId(connection.getConnectionId())
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
    Connection connectionRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionRequest.setStatus(ConnectionStatus.ACTIVE.getCode());
    connectionRequest.setApprovalDate(Calendar.getInstance());
    connectionRepository.save(connectionRequest);
    return "Success";
  }

  public String rejectConnectionRequest(ConnectionIdDto connectionIdDto) {
    Connection connectionRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionRequest.setStatus(ConnectionStatus.REJECTED.getCode());
    connectionRequest.setRejectedDate(Calendar.getInstance());
    connectionRepository.save(connectionRequest);
    return "Success";
  }
}
