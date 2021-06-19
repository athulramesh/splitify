package com.splitify.splitify.security.service;

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

  /**
   * sign in
   *
   * @param authRequest authRequest
   * @return JWT token
   * @throws Exception exception
   */
  public String signIn(AuthRequest authRequest) throws Exception {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authRequest.getUserName(), authRequest.getPassword()));
    } catch (Exception ex) {
      throw new Exception("invalid username/password");
    }
    return jwtUtil.generateToken(authRequest.getUserName());
  }

  /**
   * Sign up
   *
   * @param user user
   * @return JWT token
   */
  public String signUp(User user) {
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
    return jwtUtil.generateToken(user.getUserName());
  }

  /**
   * Get user
   *
   * @param userName userName
   * @return User
   * @throws Exception exception
   */
  public UserDetails getUser(String userName) throws Exception {
    UserEntity user = userRepository.findByUserName(userName);
    if (user != null) {
      return buildUser(user);
    } else {
      throw new Exception("User no Found");
    }
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
}
