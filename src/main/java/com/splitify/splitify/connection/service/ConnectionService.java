package com.splitify.splitify.connection.service;

import com.splitify.splitify.api.connection.dto.ConnectionIdDto;
import com.splitify.splitify.connection.domain.Connection;
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
            .status("new")
            .requestDate(Calendar.getInstance())
            .approvalDate(null)
            .cancelledDate(null)
            .rejectedDate(null)
            .build();
    connectionRepository.save(connection);
    return connection.getConnectionId();
  }

  public List<ConnectionDetails> fetchConnectionRequests(Integer userId, String type)
      throws Exception {
    List<ConnectionDetails> connectionDetails = new ArrayList<>();
    List<Connection> requests =
        new ArrayList<>(connectionRepository.findByConnectionToIdAndStatus(userId, type));
    for (Connection req : requests) {
      UserEntity user = findUserById(req.getConnectionFromId());
      if (user != null) {

        connectionDetails.add(
            ConnectionDetails.builder()
                .fromId(req.getConnectionFromId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .requestDate(req.getRequestDate())
                .connectionId(req.getConnectionId())
                .build());
      }
    }
    return (connectionDetails);
  }

  private UserEntity findUserById(Integer id) throws Exception {
    return userRepository.findById(id).orElseThrow(() -> new Exception("User Not Found"));
  }

  public String acceptConnectionRequest(ConnectionIdDto connectionIdDto) {
    Connection connectionRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionRequest.setStatus("ACTIVE");
    connectionRequest.setApprovalDate(Calendar.getInstance());
    connectionRepository.save(connectionRequest);
    return "Success";
  }

  public String rejectConnectionRequest(ConnectionIdDto connectionIdDto) {
    Connection connectionRequest =
        connectionRepository.findByConnectionId(connectionIdDto.getConnectionId());
    connectionRequest.setStatus("REJECTED");
    connectionRequest.setRejectedDate(Calendar.getInstance());
    connectionRepository.save(connectionRequest);
    return "Success";
  }
}
