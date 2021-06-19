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

import java.util.Optional;

@Service
public class UserService {
  @Autowired CustomUserDetailsService userDetailsService;
  @Autowired private UserRepository userRepository;
  @Autowired private JwtUtil jwtUtil;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private PasswordEncoder passwordEncoder;

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

  public String signUp(User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    UserEntity userEntity =
        UserEntity.builder().email(user.getEmail()).userName(user.getUserName()).build();
    userEntity.addCredential(encodedPassword);
    userRepository.save(userEntity);
    return jwtUtil.generateToken(user.getUserName());
  }

  public User getUser(int id) throws Exception {
    Optional<UserEntity> user = userRepository.findById(id);
    if (user.isPresent()) {
      return buildUser(user.get());
    } else {
      throw new Exception("User no Found");
    }
  }

  private User buildUser(UserEntity user) {
    return User.builder()
        .email(user.getEmail())
        .id(user.getUserId())
        .userName(user.getUserName())
        .build();
  }
}
