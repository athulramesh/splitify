package com.splitify.splitify.api.security.assembler;

import com.splitify.splitify.api.security.dto.AuthRequestDto;
import com.splitify.splitify.api.security.dto.UserDto;
import com.splitify.splitify.security.domain.AuthRequest;
import com.splitify.splitify.security.service.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityAssembler {
  @Autowired private ModelMapper modelMapper;

  public User assembleUser(UserDto userDto) {
    return modelMapper.map(userDto, User.class);
  }

  public AuthRequest assembleAuthRequest(AuthRequestDto authRequestDto) {
    return modelMapper.map(authRequestDto, AuthRequest.class);
  }

  public UserDto assembleUserDetails(User user) {
    return modelMapper.map(user, UserDto.class);
  }
}
