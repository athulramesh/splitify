package com.splitify.splitify.api.security;

import com.splitify.splitify.api.security.assembler.SecurityAssembler;
import com.splitify.splitify.api.security.dto.AuthRequestDto;
import com.splitify.splitify.api.security.dto.AuthResponseDto;
import com.splitify.splitify.api.security.dto.UserDto;
import com.splitify.splitify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://simplifysplit.web.app")
public class SecurityApiImpl implements SecurityApi {

  @Autowired private UserService userService;

  @Autowired private SecurityAssembler assembler;

  /**
   * Sign in
   *
   * @param authRequest authRequest
   * @return jwt
   */
  @Override
  public AuthResponseDto signIn(AuthRequestDto authRequest) {
    return assembler.assembleAuthResponse(
        userService.signIn(assembler.assembleAuthRequest(authRequest)));
  }

  /**
   * Sign in
   *
   * @param user UserDto
   * @return jwt
   */
  @Override
  public AuthResponseDto signUp(UserDto user) {
    return assembler.assembleAuthResponse(userService.signUp(assembler.assembleUser(user)));
  }
}
