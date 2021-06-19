package com.splitify.splitify.api.user;

import com.splitify.splitify.api.security.dto.UserDetailsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/users")
public interface UserApi {
  @GetMapping()
  UserDetailsDto getUserByUserName(@RequestParam String userName) throws Exception;
}
