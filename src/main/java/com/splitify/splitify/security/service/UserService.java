package com.splitify.splitify.security.service;

import com.splitify.splitify.common.exception.ExceptionUtils;
import com.splitify.splitify.security.SecurityConstants;
import com.splitify.splitify.security.domain.AuthRequest;
import com.splitify.splitify.security.domain.UserEntity;
import com.splitify.splitify.security.repository.UserRepository;
import com.splitify.splitify.security.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired CustomUserDetailsService userDetailsService;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtUtil jwtUtil;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ExceptionUtils exception;

  /**
   * sign in
   *
   * @param authRequest authRequest
   * @return JWT token
   */
  public AuthResponse signIn(AuthRequest authRequest) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUserName(), authRequest.getPassword()));
    } catch (Exception ex) {
      exception.throwBadRequestException(SecurityConstants.INVALID_CREDENTIALS);
    }
    return buildAutResponse(authRequest.getUserName());
  }

  /**
   * Builds auth response
   *
   * @param userName userName
   * @return Auth response
   */
  private AuthResponse buildAutResponse(String userName) {
    return AuthResponse.builder()
        .userDetails(buildUser(userRepository.findByUserName(userName)))
        .jwt(jwtUtil.generateToken(userName))
        .build();
  }

  /**
   * Sign up
   *
   * @param user user
   * @return JWT token
   */
  public AuthResponse signUp(User user) {
    validateUSer(user);
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    UserEntity userEntity =
        UserEntity.builder()
            .email(user.getEmail())
            .userName(user.getUserName())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();
    userEntity.addCredential(encodedPassword);
    userRepository.save(userEntity);
    return buildAutResponse(user.getUserName());
  }

  private void validateUSer(User user) {
    UserEntity userEntity=userRepository.findByUserName(user.getUserName());
    if(userEntity!=null){
      exception.throwBadRequestException("Username is taken");
    }
  }

  /**
   * Get user
   *
   * @param userName userName
   * @return User
   */
  public UserDetails getUser(String userName) {
    UserEntity user = userRepository.findByUserName(userName);
    if (user == null) {
      exception.throwBadRequestException(SecurityConstants.USER_NOT_FOUND);
    }
    return buildUser(user);
  }

  /**
   * get user Entity
   *
   * @param id id
   * @return User entity.
   */
  private UserEntity getUserEntity(Integer id) {
    return userRepository.findById(id).orElse(null);
  }

  /**
   * Build the user
   *
   * @param user user
   * @return User
   */
  private UserDetails buildUser(UserEntity user) {
    return UserDetails.builder()
        .email(user.getEmail())
        .id(user.getUserId())
        .userName(user.getUserName())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
  }

  /**
   * Get user By id
   *
   * @param id id
   * @return user Details
   */
  public UserDetails getUserById(Integer id) {
    UserEntity userEntity = getUserEntity(id);
    if (userEntity != null) {
      return buildUser(userEntity);
    }
    return null;
  }
}
