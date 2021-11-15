package com.splitify.splitify.api.security;

import com.splitify.splitify.api.security.dto.AuthRequestDto;
import com.splitify.splitify.api.security.dto.AuthResponseDto;
import com.splitify.splitify.api.security.dto.UserDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://simplifysplit.web.app")
public interface SecurityApi {
  /**
   * Sign in
   *
   * @param authRequest authRequest
   * @return jwt
   */
  @PostMapping("/sign-in")
  AuthResponseDto signIn(@RequestBody AuthRequestDto authRequest) throws Exception;

  /**
   * Sign in
   *
   * @param user UserDto
   * @return jwt
   */
  @PostMapping("/sign-up")
  AuthResponseDto signUp(@RequestBody UserDto user);
}
