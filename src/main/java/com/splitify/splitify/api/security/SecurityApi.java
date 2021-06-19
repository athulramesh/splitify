package com.splitify.splitify.api.security;

import com.splitify.splitify.api.security.dto.AuthRequestDto;
import com.splitify.splitify.api.security.dto.UserDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface SecurityApi {
  /**
   * Sign in
   *
   * @param authRequest authRequest
   * @return jwt
   */
  @PostMapping("/sign-in")
  String signIn(@RequestBody AuthRequestDto authRequest) throws Exception;

  /**
   * Sign in
   *
   * @param user UserDto
   * @return jwt
   */
  @PostMapping("/sign-up")
  String signUp(@RequestBody UserDto user);

  class UserDetailsDto {}
}
