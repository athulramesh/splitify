package com.splitify.splitify.api.connection.assembler;

import com.splitify.splitify.api.connection.dto.ConnectionDto;
import com.splitify.splitify.api.connection.dto.TargetUserDto;
import com.splitify.splitify.connection.service.ConnectionDetails;
import com.splitify.splitify.connection.service.TargetUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectionAssembler {
  @Autowired private ModelMapper modelMapper;

  public TargetUser assembleUserId(TargetUserDto targetUserDto) {
    return modelMapper.map(targetUserDto, TargetUser.class);
  }

  public ConnectionDto assembleConnectionDetails(ConnectionDetails connectionDetails) {
    return modelMapper.map(connectionDetails, ConnectionDto.class);
  }
}
