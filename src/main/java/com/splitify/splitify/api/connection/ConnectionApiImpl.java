package com.splitify.splitify.api.connection;

import com.splitify.splitify.api.connection.assembler.ConnectionAssembler;
import com.splitify.splitify.api.connection.dto.ConnectionIdDto;
import com.splitify.splitify.api.connection.dto.TargetUserDto;
import com.splitify.splitify.connection.enums.ConnectionStatus;
import com.splitify.splitify.connection.service.ConnectionDetails;
import com.splitify.splitify.connection.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/api/connections/{userId}")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ConnectionApiImpl implements ConnectionApi {

  @Autowired ConnectionService connectionService;
  @Autowired ConnectionAssembler connectionAssembler;

  @Override
  public ConnectionIdDto sendConnectionRequest(Integer fromUserId, TargetUserDto targetUserDto) {
    return connectionAssembler.assembleConnectionIdDetails(
        connectionService.sendConnectionRequest(
            fromUserId, connectionAssembler.assembleUserId(targetUserDto)));
  }

  @Override
  public List<ConnectionDetails> fetchConnectionRequests(Integer userId, ConnectionStatus type) {

    return connectionService.fetchConnectionRequests(userId, type);
  }

  @Override
  public Integer acceptConnectionRequest(ConnectionIdDto connectionIdDto) {
    return connectionService.acceptConnectionRequest(connectionIdDto);
  }

  @Override
  public String rejectConnectionRequest(ConnectionIdDto connectionIdDto) {
    return connectionService.rejectConnectionRequest(connectionIdDto);
  }
}
