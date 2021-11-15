package com.splitify.splitify.api.user;

import com.splitify.splitify.api.security.assembler.SecurityAssembler;
import com.splitify.splitify.api.security.dto.UserDetailsDto;
import com.splitify.splitify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/users")
@CrossOrigin(origins = "https://simplifysplit.web.app/")
public class UserApiImpl implements UserApi {

  @Autowired private UserService userService;

  @Autowired private SecurityAssembler assembler;

  @Override
  public UserDetailsDto getUserByUserName(String userName) throws Exception {
    return assembler.assembleUserDetailsDto(userService.getUser(userName));
  }
}
